package com.ffsupver.asplor.util;

import net.minecraft.util.math.random.Random;

public final class MathUtil {
    public static long fromEtoAE(long E) {
        return E * 2;
    }

    public static double fromAEtoE(double AE) {
        return AE / 2;
    }

    public static float getRandomFloat(Random random, float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }
}