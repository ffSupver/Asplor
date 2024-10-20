package com.ffsupver.asplor.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;


public class RenderUtil {
    public static Box createRenderBoundingBox(BlockPos pos, Double renderDistance) {
        return new Box(pos.getX()+0.5+renderDistance,pos.getY()+0.5+renderDistance,pos.getZ()+0.5+renderDistance,
                pos.getX()+0.5-renderDistance,pos.getY()+0.5-renderDistance,pos.getZ()+0.5-renderDistance);
    }
}
