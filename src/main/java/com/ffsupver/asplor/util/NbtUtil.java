package com.ffsupver.asplor.util;

import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public final class NbtUtil {
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

    public static NbtList writeBlockPosListToNbt(ArrayList<BlockPos> blockPosList){
        NbtList result = new NbtList();
        for (BlockPos blockPos : blockPosList){
            result.add(NbtUtil.writeBlockPosToNbt(blockPos));
        }
        return result;
    }

    public static ArrayList<BlockPos> readBlockPosListFromNbt(NbtList nbtList){
        ArrayList<BlockPos> result = new ArrayList<>();
        for (NbtElement e : nbtList){
            result.add(readBlockPosFromNbt((NbtCompound) e));
        }
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

    public static NbtList writeStringListToNbt(List<String> stringList){
        NbtList result = new NbtList();
        for (String s : stringList){
            result.add(NbtString.of(s));
        }
        return result;
    }

    public static List<String> readStringListFromNbt(NbtList nbtList){
        ArrayList<String> result = new ArrayList<>();
        for (NbtElement e : nbtList){
            result.add(e.asString());
        }
        return result;
    }

    public static NbtList writeFloatListToNbt(List<Float> floatList){
        NbtList result = new NbtList();
        for (Float s : floatList){
            result.add(NbtFloat.of(s));
        }
        return result;
    }

    public static List<Float> readFloatListFromNbt(NbtList nbtList){
        ArrayList<Float> result = new ArrayList<>();
        for (NbtElement e : nbtList){
            result.add(((NbtFloat)e).floatValue());
        }
        return result;
    }
}
