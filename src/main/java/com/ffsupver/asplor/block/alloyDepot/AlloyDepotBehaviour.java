package com.ffsupver.asplor.block.alloyDepot;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionCallback;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class AlloyDepotBehaviour extends BlockEntityBehaviour {
    TransportedItemStack heldItem;
    List<TransportedItemStack> incoming;
    ItemStackHandler processingOutputBuffer;
    AlloyDepotItemHandler itemHandler;
    TransportedItemStackHandlerBehaviour transportedHandler;
    boolean allowMerge;
    SnapshotParticipant<Data> snapshotParticipant = new SnapshotParticipant<>() {
        @Override
        protected Data createSnapshot() {
            // incoming stacks are not mutated during transfer, no need to deep copy
            return new Data(new ArrayList<>(incoming), heldItem == null ? null : heldItem.fullCopy());
        }

        @Override
        protected void readSnapshot(Data snapshot) {
            incoming = snapshot.incoming;
            heldItem = snapshot.held;
        }

        @Override
        protected void onFinalCommit() {
            blockEntity.notifyUpdate();
        }
    };
    record Data(List<TransportedItemStack> incoming, TransportedItemStack held) {
    }
    public static final BehaviourType<AlloyDepotBehaviour> TYPE = new BehaviourType<>();
    public AlloyDepotBehaviour(SmartBlockEntity be) {
        super(be);
        incoming = new ArrayList<>();
        processingOutputBuffer = new ItemStackHandler(8);
        itemHandler=new AlloyDepotItemHandler(this);
    }

    protected boolean tick(TransportedItemStack heldItem) {
        heldItem.prevBeltPosition = heldItem.beltPosition;
        heldItem.prevSideOffset = heldItem.sideOffset;
        float diff = .5f - heldItem.beltPosition;
        if (diff > 1 / 512f) {
            if (diff > 1 / 32f && !BeltHelper.isItemUpright(heldItem.stack))
                heldItem.angle += 1;
            heldItem.beltPosition += diff / 4f;
        }
        return diff < 1 / 16f;
    }

    public boolean canMergeItems() {
        return allowMerge;
    }

    @Override
    public void tick() {
        super.tick();

        World world = blockEntity.getWorld();

        for (Iterator<TransportedItemStack> iterator = incoming.iterator(); iterator.hasNext();) {
            TransportedItemStack ts = iterator.next();
            if (!tick(ts))
                continue;
            if (world.isClient && !blockEntity.isVirtual())
                continue;
            if (heldItem == null) {
                heldItem = ts;
            } else {
                if (!ItemHelper.canItemStackAmountsStack(heldItem.stack, ts.stack)) {
                    Vec3d vec = VecHelper.getCenterOf(blockEntity.getPos());
                    ItemScatterer.spawn(blockEntity.getWorld(), vec.x, vec.y + .5f, vec.z, ts.stack);
                } else {
                    heldItem.stack.increment(ts.stack.getCount());
                }
            }
            iterator.remove();
            blockEntity.notifyUpdate();
        }

        if (heldItem == null) {
            return;
        }

        if (!tick(heldItem))
            return;

        BlockPos pos = blockEntity.getPos();

        if (world.isClient)
            return;
        if (handleBeltFunnelOutput())
            return;

        if (heldItem.stack.isEmpty()){
            if (!processingOutputBuffer.empty()) {
                Iterator<SingleSlotStorage<ItemVariant>> iterator = processingOutputBuffer.getSlots().iterator();

                while (iterator.hasNext() && heldItem.stack.isEmpty()) {

                    SingleSlotStorage<ItemVariant> slot = iterator.next();
                    if (!slot.isResourceBlank()) {
                        System.out.println("no blank");

                        try (Transaction t = Transaction.openOuter()) {
                            ItemStack newHeldItem = slot.getResource().toStack();
                            int amount = (int) slot.extract(slot.getResource(), slot.getAmount(), t);
                            heldItem.stack = newHeldItem.copyWithCount(amount);
                            snapshotParticipant.updateSnapshots(t);
                            System.out.println(amount+" "+slot+" "+heldItem.stack);
                            t.commit();
                        }
                    }
                }
            }
        }

        BeltProcessingBehaviour processingBehaviour =
                BlockEntityBehaviour.get(world, pos.up(2), BeltProcessingBehaviour.TYPE);
        if (processingBehaviour == null)
            return;
        if (!heldItem.locked && BeltProcessingBehaviour.isBlocked(world, pos))
            return;

        ItemStack previousItem = heldItem.stack;
        boolean wasLocked = heldItem.locked;
        BeltProcessingBehaviour.ProcessingResult result = wasLocked ? processingBehaviour.handleHeldItem(heldItem, transportedHandler)
                : processingBehaviour.handleReceivedItem(heldItem, transportedHandler);
        if (result == BeltProcessingBehaviour.ProcessingResult.REMOVE) {
            heldItem = null;
            blockEntity.sendData();
            return;
        }


        if (heldItem == null) {
            blockEntity.sendData();
            return;
        }

        heldItem.locked = result == BeltProcessingBehaviour.ProcessingResult.HOLD;
        if (heldItem.locked != wasLocked || !ItemStack.areEqual(previousItem, heldItem.stack))
            blockEntity.sendData();
    }

    private boolean handleBeltFunnelOutput() {
        BlockState funnel = getWorld().getBlockState(getPos().up());
        Direction funnelFacing = AbstractFunnelBlock.getFunnelFacing(funnel);
        if (funnelFacing == null)
            return false;

        for (int slot = 0; slot < processingOutputBuffer.getSlotCount(); slot++) {
            ItemStack previousItem = processingOutputBuffer.getStackInSlot(slot);
            if (previousItem.isEmpty())
                continue;
            ItemStack afterInsert = blockEntity.getBehaviour(DirectBeltInputBehaviour.TYPE)
                    .tryExportingToBeltFunnel(previousItem, null, false);
            if (afterInsert == null)
                return false;
            if (previousItem.getCount() != afterInsert.getCount()) {
                processingOutputBuffer.setStackInSlot(slot, afterInsert);
                blockEntity.notifyUpdate();
                return true;
            }
        }

        ItemStack previousItem = heldItem.stack;
        if (previousItem.isEmpty()) { // fabric: this is not allowed
            return false;
        }
        ItemStack afterInsert = blockEntity.getBehaviour(DirectBeltInputBehaviour.TYPE)
                .tryExportingToBeltFunnel(previousItem, null, false);
        if (afterInsert == null)
            return false;
        if (previousItem.getCount() != afterInsert.getCount()) {
            if (afterInsert.isEmpty())
                heldItem = null;
            else
                heldItem.stack = afterInsert;
            blockEntity.notifyUpdate();
            return true;
        }

        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
        World level = getWorld();
        BlockPos pos = getPos();
        ItemHelper.dropContents(level, pos, processingOutputBuffer);
        for (TransportedItemStack transportedItemStack : incoming)
            Block.dropStack(level, pos, transportedItemStack.stack);
        if (!getHeldItemStack().isEmpty())
            Block.dropStack(level, pos, getHeldItemStack());
    }

    public void addSubBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(blockEntity).allowingBeltFunnels()
                .setInsertionHandler(this::tryInsertingFromSide).considerOccupiedWhen(this::isOccupied));
        transportedHandler = new TransportedItemStackHandlerBehaviour(blockEntity, this::applyToAllItems)
                .withStackPlacement(this::getWorldPositionOf);
        behaviours.add(transportedHandler);
    }

    public void setCenteredHeldItem(TransportedItemStack heldItem) {
        this.heldItem = heldItem;
        this.heldItem.beltPosition = 0.5f;
        this.heldItem.prevBeltPosition = 0.5f;
    }

    private void applyToAllItems(float maxDistanceFromCentre,
                                 Function<TransportedItemStack, TransportedItemStackHandlerBehaviour.TransportedResult> processFunction) {
        if (heldItem == null)
            return;
        if (.5f - heldItem.beltPosition > maxDistanceFromCentre)
            return;

        boolean dirty = false;
        TransportedItemStack transportedItemStack = heldItem;
        ItemStack stackBefore = transportedItemStack.stack.copy();
        TransportedItemStackHandlerBehaviour.TransportedResult result = processFunction.apply(transportedItemStack);
        if (result == null || result.didntChangeFrom(stackBefore))
            return;

        dirty = true;
        heldItem = null;
        if (result.hasHeldOutput())
            setCenteredHeldItem(result.getHeldOutput());

        for (TransportedItemStack added : result.getOutputs()) {
            if (getHeldItemStack().isEmpty()) {
                setCenteredHeldItem(added);
                continue;
            }
            try (Transaction t = TransferUtil.getTransaction()) {
                long inserted = processingOutputBuffer.insert(ItemVariant.of(added.stack), added.stack.getCount(), t);
                t.commit();
                ItemStack remainder = added.stack.copy();
                remainder.setCount(ItemHelper.truncateLong(added.stack.getCount() - inserted));
                Vec3d vec = VecHelper.getCenterOf(blockEntity.getPos());
                ItemScatterer.spawn(blockEntity.getWorld(), vec.x, vec.y + .5f, vec.z, remainder);
            }
        }

        if (dirty)
            blockEntity.notifyUpdate();
    }


    private ItemStack tryInsertingFromSide(TransportedItemStack transportedStack, Direction side, boolean simulate) {
        ItemStack inserted = transportedStack.stack;

        if (isOccupied(side))
            return inserted;

        int size = transportedStack.stack.getCount();
        transportedStack = transportedStack.copy();
        transportedStack.beltPosition = side.getAxis()
                .isVertical() ? .5f : 0;
        transportedStack.insertedFrom = side;
        transportedStack.prevSideOffset = transportedStack.sideOffset;
        transportedStack.prevBeltPosition = transportedStack.beltPosition;
        try (Transaction t = TransferUtil.getTransaction()) {
            snapshotParticipant.updateSnapshots(t);
            ItemStack remainder = insert(transportedStack, t);
            if (remainder.getCount() != size)
                blockEntity.notifyUpdate();
            if (!simulate)
                t.commit();

            return remainder;
        }
    }

    private boolean isOccupied(Direction side) {
        if (!getHeldItemStack().isEmpty() && !canMergeItems())
            return true;
        return !isOutputEmpty() && !canMergeItems();
    }

    private Vec3d getWorldPositionOf(TransportedItemStack transportedItemStack) {
        return VecHelper.getCenterOf(blockEntity.getPos());
    }


    public void enableMerging() {
        allowMerge = true;
    }

    @Override
    public void write(NbtCompound nbt, boolean clientPacket) {
        if (heldItem != null)
            nbt.put("HeldItem", heldItem.serializeNBT());
        nbt.put("OutputBuffer", processingOutputBuffer.serializeNBT());
        if (!incoming.isEmpty())
            nbt.put("Incoming", NBTHelper.writeCompoundList(incoming, TransportedItemStack::serializeNBT));
    }

    @Override
    public void read(NbtCompound compound, boolean clientPacket) {
        heldItem = null;
        if (compound.contains("HeldItem"))
            heldItem = TransportedItemStack.read(compound.getCompound("HeldItem"));
        processingOutputBuffer.deserializeNBT(compound.getCompound("OutputBuffer"));
        NbtList list = compound.getList("Incoming", NbtElement.COMPOUND_TYPE);
        incoming = NBTHelper.readCompoundList(list, TransportedItemStack::read);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public boolean isEmpty() {
        return heldItem == null && isOutputEmpty();
    }

    public boolean isOutputEmpty() {
        for (int i = 0; i < processingOutputBuffer.getSlotCount(); i++)
            if (!processingOutputBuffer.getStackInSlot(i)
                    .isEmpty())
                return false;
        return true;
    }

    public ItemStack getHeldItemStack() {
        return heldItem == null ? ItemStack.EMPTY : heldItem.stack;
    }

    public TransportedItemStack getHeldItem() {
        return heldItem;
    }

    public void removeHeldItem() {
        this.heldItem = null;
    }

    public void setHeldItem(TransportedItemStack heldItem) {
        this.heldItem = heldItem;
    }

    public boolean isItemValid(ItemStack stack) {
        return true;
    }

    public int getPresentStackSize() {
        int cumulativeStackSize = 0;
        cumulativeStackSize += getHeldItemStack().getCount();
        for (int slot = 0; slot < processingOutputBuffer.getSlotCount(); slot++)
            cumulativeStackSize += processingOutputBuffer.getStackInSlot(slot)
                    .getCount();
        return cumulativeStackSize;
    }

    public int getRemainingSpace() {
        int cumulativeStackSize = getPresentStackSize();
        for (TransportedItemStack transportedItemStack : incoming)
            cumulativeStackSize += transportedItemStack.stack.getCount();
        int fromGetter = 64;
        return fromGetter - cumulativeStackSize;
    }

    public ItemStack insert(TransportedItemStack heldItem, TransactionContext ctx) {
        if (canMergeItems()) {
            int remainingSpace = getRemainingSpace();
            ItemStack inserted = heldItem.stack;
            if (remainingSpace <= 0)
                return inserted;
            if (this.heldItem != null && !ItemHelper.canItemStackAmountsStack(this.heldItem.stack, inserted))
                return inserted;

            ItemStack returned = ItemStack.EMPTY;
            snapshotParticipant.updateSnapshots(ctx);
            if (remainingSpace < inserted.getCount()) {
                returned = ItemHandlerHelper.copyStackWithSize(heldItem.stack, inserted.getCount() - remainingSpace);
                TransportedItemStack copy = heldItem.copy();
                copy.stack.setCount(remainingSpace);
                if (this.heldItem != null)
                    incoming.add(copy);
                else
                    this.heldItem = copy;
            } else {
                if (this.heldItem != null)
                    incoming.add(heldItem);
                else
                    this.heldItem = heldItem;
            }
            return returned;
        }

        if (this.isEmpty()) {
            if (heldItem.insertedFrom.getAxis()
                    .isHorizontal())
                TransactionCallback.onSuccess(ctx, () -> AllSoundEvents.DEPOT_SLIDE.playOnServer(getWorld(), getPos()));
            else
                TransactionCallback.onSuccess(ctx, () -> AllSoundEvents.DEPOT_PLOP.playOnServer(getWorld(), getPos()));
        }
        snapshotParticipant.updateSnapshots(ctx);
        this.heldItem = heldItem;
        TransactionCallback.onSuccess(ctx, () -> {});
        return ItemStack.EMPTY;
    }
}
