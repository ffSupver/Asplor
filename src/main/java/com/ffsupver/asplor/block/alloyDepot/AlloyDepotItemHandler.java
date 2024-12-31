package com.ffsupver.asplor.block.alloyDepot;

import com.google.common.collect.Iterators;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.item.ItemHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Unit;

import java.util.Iterator;

public class AlloyDepotItemHandler extends SnapshotParticipant<Unit> implements Storage<ItemVariant> {
    private static final int MAIN_SLOT = 0;
    private AlloyDepotBehaviour behaviour;
    public AlloyDepotItemHandler(AlloyDepotBehaviour behaviour){
        this.behaviour = behaviour;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (!behaviour.getHeldItemStack().isEmpty() && !behaviour.canMergeItems())
            return 0;
        if (!behaviour.isOutputEmpty() && !behaviour.canMergeItems())
            return 0;
        int toInsert = Math.min(ItemHelper.truncateLong(maxAmount), resource.getItem().getMaxCount());
        ItemStack stack = resource.toStack(toInsert);
        if (!behaviour.isItemValid(stack))
            return 0;
        ItemStack remainder = behaviour.insert(new TransportedItemStack(stack), transaction);
        return stack.getCount() - remainder.getCount();
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        long extracted = behaviour.processingOutputBuffer.extract(resource, maxAmount, transaction);
        if (extracted == maxAmount)
            return extracted;
        extracted += extractFromMain(resource, maxAmount - extracted, transaction);
        return extracted;
    }

    public long extractFromMain(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        TransportedItemStack held = behaviour.heldItem;
        if (held == null)
            return 0;
        ItemStack stack = held.stack;
        if (!resource.matches(stack))
            return 0;
        int toExtract = Math.min(ItemHelper.truncateLong(maxAmount), Math.min(stack.getCount(), 64));
        stack = stack.copy();
        stack.decrement(toExtract);
        if (stack.isEmpty())
            stack = ItemStack.EMPTY;
        behaviour.snapshotParticipant.updateSnapshots(transaction);
        behaviour.heldItem.stack = stack;
        if (stack.isEmpty())
            behaviour.heldItem = null;
        return toExtract;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        return Iterators.concat(Iterators.singletonIterator(new MainSlotView()), behaviour.processingOutputBuffer.iterator());
    }

    @Override
    protected Unit createSnapshot() {
        return Unit.INSTANCE;
    }

    @Override
    protected void readSnapshot(Unit snapshot) {
    }
    public class MainSlotView implements StorageView<ItemVariant> {

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return extractFromMain(resource, maxAmount, transaction);
        }

        @Override
        public boolean isResourceBlank() {
            return getResource().isBlank();
        }

        @Override
        public ItemVariant getResource() {
            return ItemVariant.of(getStack());
        }

        @Override
        public long getAmount() {
            ItemStack stack = getStack();
            return stack.isEmpty() ? 0 : stack.getCount();
        }

        @Override
        public long getCapacity() {
            ItemStack stack = getStack();
            return stack.isEmpty() ? 64 : Math.min(stack.getMaxCount(), 64);
        }

        public ItemStack getStack() {
            TransportedItemStack held = behaviour.heldItem;
            if (held == null || held.stack == null || held.stack.isEmpty())
                return ItemStack.EMPTY;
            return held.stack;
        }
    }
}
