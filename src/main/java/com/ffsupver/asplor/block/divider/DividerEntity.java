package com.ffsupver.asplor.block.divider;

import com.ffsupver.asplor.recipe.DividerRecipe;
import com.ffsupver.asplor.sound.ModSounds;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.ViewOnlyWrappedStorageView;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class DividerEntity extends KineticBlockEntity implements SidedStorageBlockEntity {
    public ItemStackHandlerContainer inputInv;
    public ItemStackHandler outputInv;
    public DividerInventoryHandler capability;
    public int timer;

    private DividerRecipe dividerRecipe;

    public DividerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inputInv = new ItemStackHandlerContainer(1);
        outputInv = new ItemStackHandler(9);
        capability = new DividerInventoryHandler();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
        super.addBehaviours(behaviours);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        if (getSpeed() == 0)
            return;
        if (inputInv.getStackInSlot(0)
                .isEmpty())
            return;

        float pitch = MathHelper.clamp((Math.abs(getSpeed()) / 256f) + .45f, .85f, 1f);
        if (world != null) {
            world.playSound(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5, ModSounds.DIVIDER_CUT,
                    SoundCategory.BLOCKS,0.2f,pitch,false);
        }

    }

    @Override
    public void tick() {
        super.tick();

        if (getSpeed() == 0)
            return;
        for (int i = 0; i < outputInv.getSlotCount(); i++) {
            if (outputInv.getStackInSlot(i)
                    .getCount() == outputInv.getSlotLimit(i))
                return;
        }
        if (getCurrentRecipe().isPresent()) {
            if (timer <= 0) {
                craftItem();
                timer = 2560;
                markDirty();
                sendData();
            } else {
                timer -= getProcessingSpeed();
                sendData();
            }
        } else {
            timer = 2560;
        }

        if (getWorld().isClient() && timer > 0) {
            spawnParticles();
            return;
        }

        if (inputInv.getStackInSlot(0)
                .isEmpty())
            return;

        sendData();
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    private void craftItem() {
        ItemStack result = getCurrentRecipe().get().getOutput(null);
        try (Transaction t = TransferUtil.getTransaction()) {
            ItemStackHandlerSlot slot = inputInv.getSlot(0);
            slot.extract(slot.getResource(), 1, t);
            outputInv.insert(ItemVariant.of(result), result.getCount(), t);
            t.commit();
        }

    }

    private boolean isMiscCategory(ShapelessRecipe recipe) {
        return recipe.getCategory().equals(CraftingRecipeCategory.MISC);
    }

    private Optional<Recipe<?>> getCurrentRecipe() {

        return testRecipe(inputInv.getStackInSlot(0));

    }

    private Optional<Recipe<?>> testRecipe(ItemStack itemStack) {
        SimpleInventory inv = new SimpleInventory(1);
        inv.setStack(0, itemStack);

        Optional<DividerRecipe> dividerRecipe1 = getWorld().getRecipeManager().getFirstMatch(DividerRecipe.Type.INSTANCE, inv, getWorld());


        ShapelessRecipeTesterInventory inv2 = new ShapelessRecipeTesterInventory(itemStack);
        inv2.setStack(0, itemStack);

        Optional<ShapelessRecipe> miscRecipe = getWorld().getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream()
                .filter(recipe -> recipe instanceof ShapelessRecipe)
                .map(recipe -> (ShapelessRecipe) recipe)
                .filter(recipe -> recipe.matches(inv2, getWorld()) && isMiscCategory(recipe))
                .findFirst();

        if (dividerRecipe1.isPresent()) {
            return Optional.of(dividerRecipe1.get());
        }
        if (miscRecipe.isPresent()) {
            return Optional.of(miscRecipe.get());
        }
        return Optional.empty();


    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(world, pos, inputInv);
        ItemHelper.dropContents(world, pos, outputInv);
    }


    public void spawnParticles() {
        ItemStack stackInSlot = inputInv.getStackInSlot(0);
        if (stackInSlot.isEmpty())
            return;
        ItemStackParticleEffect data = new ItemStackParticleEffect(ParticleTypes.ITEM, stackInSlot);
        float angle = world.random.nextFloat() * 360;
        Vec3d offset = new Vec3d(0, -0.1, 0.1f);
        Vec3d offset2 = new Vec3d(0, 0.1, 0.1f);
        offset = VecHelper.rotate(offset, angle, Direction.Axis.Y);
        offset2 = VecHelper.rotate(offset2, angle, Direction.Axis.Y);
        Vec3d target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Direction.Axis.Y);
        Vec3d target2 = VecHelper.rotate(offset2, getSpeed() > 0 ? 25 : -25, Direction.Axis.Y);

        Vec3d center = offset.add(VecHelper.getCenterOf(pos));
        target = VecHelper.offsetRandomly(target.subtract(offset), world.random, 1 / 128f);
        world.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
        world.addParticle(data, center.x, center.y, center.z, target2.x, target2.y, target2.z);
    }

    @Override
    public void write(NbtCompound compound, boolean clientPacket) {
        compound.putInt("Timer", timer);
        compound.put("InputInventory", inputInv.serializeNBT());
        compound.put("OutputInventory", outputInv.serializeNBT());
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(NbtCompound compound, boolean clientPacket) {
        timer = compound.getInt("Timer");
        inputInv.deserializeNBT(compound.getCompound("InputInventory"));
        outputInv.deserializeNBT(compound.getCompound("OutputInventory"));
        super.read(compound, clientPacket);
    }

    public int getProcessingSpeed() {
        return MathHelper.clamp((int) Math.abs(getSpeed() / 1f), 1, 512);
    }

    @Nullable
    @Override
    public Storage<ItemVariant> getItemStorage(@Nullable Direction direction) {
        return capability;
    }

    private boolean canProcess(ItemStack stack) {
        return testRecipe(stack).isPresent();
    }

    private class DividerInventoryHandler extends CombinedStorage<ItemVariant, ItemStackHandler> {

        public DividerInventoryHandler() {
            super(List.of(inputInv, outputInv));
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
//            System.out.println(canProcess(resource.toStack()) + " " + resource.toStack());
            if (canProcess(resource.toStack()))
                return inputInv.insert(resource, maxAmount, transaction);
            return 0;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return outputInv.extract(resource, maxAmount, transaction);
        }

        @Override
        public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
            return new Divider3InventoryHandlerIterator();
        }

        private class Divider3InventoryHandlerIterator implements Iterator<StorageView<ItemVariant>> {
            private boolean output = true;
            private Iterator<StorageView<ItemVariant>> wrapped;

            public Divider3InventoryHandlerIterator() {
                wrapped = outputInv.iterator();
            }

            @Override
            public boolean hasNext() {
                return wrapped.hasNext();
            }

            @Override
            public StorageView<ItemVariant> next() {
                StorageView<ItemVariant> view = wrapped.next();
                if (!output) view = new ViewOnlyWrappedStorageView<>(view);
                if (output && !hasNext()) {
                    wrapped = inputInv.iterator();
                    output = false;
                }
                return view;
            }
        }
    }
}
