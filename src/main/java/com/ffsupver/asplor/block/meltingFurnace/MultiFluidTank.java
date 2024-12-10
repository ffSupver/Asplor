package com.ffsupver.asplor.block.meltingFurnace;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;

public class MultiFluidTank implements Storage<FluidVariant> {
    private long maxCapacity;
    private long amount;
    private ArrayList<Pair<FluidVariant,Long>> fluids;
    public MultiFluidTank(long maxCapacity){
        this(maxCapacity,0,new ArrayList<>());
    }
    public MultiFluidTank(long maxCapacity,long amount,ArrayList<Pair<FluidVariant,Long>> fluids){
        this.maxCapacity = maxCapacity;
        this.amount = amount;
        this.fluids = fluids;
    }

    public long getAmount() {
        return amount;
    }

    public long getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    private void updateAmount(){
        amount = 0;
        Iterator<Pair<FluidVariant,Long>> iterator = fluids.iterator();
        while (iterator.hasNext()){
            Pair<FluidVariant,Long> pair = iterator.next();
            if (pair.getRight() <= 0){
                iterator.remove();
            }else {
                amount += pair.getRight();
            }
        }
    }

    public boolean canInsert(long amount){
        return amount <= maxCapacity - this.amount;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (maxCapacity>amount){
            long inserted = Math.min(maxCapacity - amount, maxAmount);
            for (Pair<FluidVariant, Long> pair : fluids) {
                if (pair.getLeft().equals(resource)) {
                    pair.setRight(pair.getRight() + inserted);
                    updateAmount();
                    return inserted;
                }
            }
            fluids.add(new Pair<>(resource,inserted));
            updateAmount();
            return inserted;
        }

        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        for (Pair<FluidVariant,Long> pair : fluids){
            if (pair.getLeft().equals(resource)){
                long extracted = Math.min(maxAmount,pair.getRight());
                transaction.addCloseCallback((t, result) ->{
                    if (result.wasCommitted()){
                        pair.setRight(pair.getRight() - extracted);
                        updateAmount();
                    }
                });
                return extracted;
            }
        }
        return 0;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        ArrayList<StorageView<FluidVariant>> viewFluidList = new ArrayList<>();
        for (Pair<FluidVariant,Long> pair : fluids){
           viewFluidList.add(new StorageView<FluidVariant>() {
               @Override
               public FluidVariant getResource() {
                   return pair.getLeft();
               }

               @Override
               public long getAmount() {
                   return pair.getRight();
               }

               @Override
               public long getCapacity() {
                   return maxCapacity - amount;
               }

               @Override
               public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                   return MultiFluidTank.this.extract(resource,maxAmount,transaction);
               }

               @Override
               public boolean isResourceBlank() {
                   return pair.getLeft().isBlank();
               }
           });
        }
        return viewFluidList.iterator();
    }

    public boolean isEmpty(){
        return fluids.isEmpty();
    }

    public FluidVariant getFirstFluid(){
        return fluids.get(0).getLeft();
    }

    public NbtCompound writeToNbt(){
        NbtCompound result = new NbtCompound();
        NbtList fluidNbtList = new NbtList();
        for (Pair<FluidVariant,Long> pair : fluids){
            NbtCompound fluidNbt = new NbtCompound();
            fluidNbt.put("fluid",pair.getLeft().toNbt());
            fluidNbt.putLong("amount",pair.getRight());
            fluidNbtList.add(fluidNbt);
        }
        result.put("fluids",fluidNbtList);

        result.putLong("capacity",maxCapacity);

        result.putLong("amount",amount);

        return result;
    }

    public void readFromNbt(NbtCompound nbt){
        fluids = new ArrayList<>();
        NbtList fluidNbtList = nbt.getList("fluids",10);
        for (NbtElement e : fluidNbtList){
            NbtCompound fluidNbt = (NbtCompound) e;
            fluids.add(new Pair<>(FluidVariant.fromNbt(fluidNbt.getCompound("fluid")),fluidNbt.getLong("amount")));
        }
        maxCapacity=nbt.getLong("capacity");
        amount=nbt.getLong("amount");
    }
}
