package com.ffsupver.asplor.block;

import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionCallback;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import java.util.function.Consumer;

public class SmartEnergyStorage implements EnergyStorage {

    private long capacity;
    private long maxInsert;
    private long maxExtract;
    private long amount;
    private Consumer<Long> updateCallback;

    public SmartEnergyStorage(long capacity, long maxInsert, long maxExtract, Consumer<Long> updateCallback) {
        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
        this.amount = 0;
        this.updateCallback = updateCallback;
    }

    @Override
    public long insert(long maxAmount, @Nullable TransactionContext transaction) {
        long toInsert = Math.min(maxAmount, Math.min(maxInsert, capacity - amount));
        if (toInsert > 0) {
            if (transaction == null) {
                amount += toInsert;
                updateCallback.accept(amount);
            } else {
                long finalToInsert = toInsert;
                TransactionCallback.onSuccess(transaction, () -> {
                    amount += finalToInsert;
                    updateCallback.accept(amount);
                });
            }
        }
        return toInsert;
    }

    @Override
    public long extract(long maxAmount, @Nullable TransactionContext transaction) {
        long toExtract = Math.min(maxAmount, Math.min(maxExtract, amount));
        if (toExtract > 0) {
            if (transaction == null) {
                amount -= toExtract;
                updateCallback.accept(amount);
            } else {
                long finalToExtract = toExtract;
                TransactionCallback.onSuccess(transaction, () -> {
                    amount -= finalToExtract;
                    updateCallback.accept(amount);
                });
            }
        }
        return toExtract;
    }

    public long extractEnergy(long toExtract){
        long extracted = toExtract;
        try (Transaction t = Transaction.openOuter()){
             extracted = this.extract(toExtract,t);
            t.commit();
        }
        return extracted;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }
    public long getSpace(){
        return capacity-amount;
    }

    @Override
    public boolean supportsInsertion() {
        return maxInsert > 0;
    }

    @Override
    public boolean supportsExtraction() {
        return maxExtract > 0;
    }

    public void setEnergy(long amount, @Nullable TransactionContext ctx) {
        this.amount = Math.min(amount, capacity);
        if (ctx == null) {
            updateCallback.accept(this.amount);
        } else {
            TransactionCallback.onSuccess(ctx, () -> updateCallback.accept(this.amount));
        }
    }

    // 动态调整能量容量
    public void setCapacity(long newCapacity) {
        this.capacity = newCapacity;
        // 确保当前存储的能量不超过新的上限
        if (this.amount > this.capacity) {
            this.amount = this.capacity;
            updateCallback.accept(this.amount);
        }
    }
    public NbtCompound writeToNBT(NbtCompound compound){
        compound.putLong("amount",amount);
        compound.putLong("capacity",capacity);
        return compound;
    }
    public void readFromNBT(NbtCompound compound){
        this.amount = compound.getLong("amount");
        this.capacity = compound.getLong("capacity");
    }
}
