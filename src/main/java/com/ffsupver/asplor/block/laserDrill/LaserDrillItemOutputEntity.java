package com.ffsupver.asplor.block.laserDrill;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LaserDrillItemOutputEntity extends SmartBlockEntity implements SidedStorageBlockEntity {
    private final LaserDrillItemOutputItemHandler itemStackHandler = new LaserDrillItemOutputItemHandler(9);
    public LaserDrillItemOutputEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        itemStackHandler.deserializeNBT(tag.getCompound("item"));
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("item",itemStackHandler.serializeNBT());
    }

    public ItemStack insert(ItemStack itemStack){
        long inserted = 0;
        try(Transaction t = Transaction.openOuter()) {
            inserted = this.itemStackHandler.insert(ItemVariant.of(itemStack), itemStack.getCount(), t);
            t.commit();
        }
        return itemStack.copyWithCount((int) (itemStack.getCount() - inserted));
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        return itemStackHandler;
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(getWorld(),getPos(),itemStackHandler);
    }

    private class LaserDrillItemOutputItemHandler extends ItemStackHandler{
        public LaserDrillItemOutputItemHandler(int i) {
            super(i);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            notifyUpdate();
        }
    }
}
