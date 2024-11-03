package com.ffsupver.asplor.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

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
    public static Vec3d readVec3dFromNbt(NbtCompound nbtCompound){
        return new Vec3d(nbtCompound.getDouble("x"),nbtCompound.getDouble("y"),nbtCompound.getDouble("z"));
    }
    public static NbtCompound writeVec3dToNbt(Vec3d vec3d){
        NbtCompound result = new NbtCompound();
        result.putDouble("x",vec3d.getX());
        result.putDouble("y",vec3d.getY());
        result.putDouble("z",vec3d.getZ());
        return result;
    }
}
