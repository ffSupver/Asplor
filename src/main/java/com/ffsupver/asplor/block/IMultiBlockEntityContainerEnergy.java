package com.ffsupver.asplor.block;

import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import team.reborn.energy.api.EnergyStorage;

public interface IMultiBlockEntityContainerEnergy extends IMultiBlockEntityContainer {
    // 检查是否存在能量存储
    default boolean hasEnergyStorage() { return false; }

    // 获取当前存储的能量
    default EnergyStorage getStoredEnergy() { return null; }

    // 设置当前存储的能量
    default void setStoredEnergy(long energy) {}

    // 获取能量存储上限
    default long getEnergyCapacity() { return 0; }

    // 设置能量存储上限
    default void setEnergyCapacity(int blocks) {}

    // 传入能量
    default long insertEnergy(long amount, boolean simulate) { return 0; }

    // 提取能量
    default long extractEnergy(long amount, boolean simulate) { return 0; }
}
