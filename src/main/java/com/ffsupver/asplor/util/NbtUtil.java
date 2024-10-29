package com.ffsupver.asplor.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class NbtUtil {
    public static BlockPos readBlockPosFromNbt(NbtCompound nbtCompound){
        return new BlockPos(nbtCompound.getInt("x"),nbtCompound.getInt("y"),nbtCompound.getInt("z"));
    }
    public static NbtCompound writeBlockPosToNbt(BlockPos blockPos){
        NbtCompound result = new NbtCompound();
        result.putInt("x",blockPos.getX());
        result.putInt("y",blockPos.getY());
        result.putInt("z",blockPos.getZ());
        return result;
    }
}
