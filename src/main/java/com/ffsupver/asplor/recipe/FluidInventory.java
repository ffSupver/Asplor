package com.ffsupver.asplor.recipe;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class FluidInventory implements Inventory {
    private final DefaultedList<ItemStack> items;



    private DefaultedList<Pair<FluidVariant,Long>> storedFluids;

    public FluidInventory(int size, long capacity) {
        this(size,1);
    }

    public FluidInventory(int itemSize,int fluidSize){
        this.items =   DefaultedList.ofSize(itemSize,ItemStack.EMPTY);
        this.storedFluids = DefaultedList.ofSize(fluidSize,new Pair<>(FluidVariant.blank(),0L));
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        for (Pair<FluidVariant, Long> fluid : storedFluids) {
            if (!(fluid.getRight() == 0 && fluid.getLeft().isBlank())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(items, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(items, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
    }

    @Override
    public void markDirty() {
        // 物品或液体发生变化时调用
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        items.clear();
        storedFluids.clear();
    }

    // 液体相关方法
    public FluidVariant getStoredFluid(int slot) {
        return storedFluids.get(slot).getLeft();
    }

    public long getStoredAmount(int slot) {
        return storedFluids.get(slot).getRight();
    }


    public boolean canInsertFluid(int slot,FluidVariant fluid, long amount) {
        return (storedFluids.get(slot).getLeft().equals(fluid) || storedFluids.get(slot).getLeft().isBlank());
    }

    public void insertFluid(int slot,FluidVariant fluid, long amount) {
        if (canInsertFluid(slot,fluid, amount)) {
            long oldAmount = storedFluids.get(slot).getRight();
            storedFluids.set(slot,new Pair<>(fluid,amount + oldAmount));
        }
    }

    public boolean canExtractFluid(int slot,FluidVariant fluid, long amount) {
        return storedFluids.get(slot).getLeft().equals(fluid) && storedFluids.get(slot).getRight() >= amount;
    }

    public void extractFluid(int slot,long amount) {
        if (storedFluids.get(slot).getRight() >= amount) {
            long oldAmount = storedFluids.get(slot).getRight();
            Pair<FluidVariant, Long> newFluidStore = new Pair(storedFluids.get(slot).getLeft(), oldAmount - amount);
            storedFluids.set(slot,newFluidStore);
            if (oldAmount - amount <= 0) {
                storedFluids.set(slot,new Pair<>(FluidVariant.blank(),0L));
            }
        }
    }

    @Override
    public String toString() {
        return items.toString()+" Fluid "+storedFluids.toString();
    }
}
