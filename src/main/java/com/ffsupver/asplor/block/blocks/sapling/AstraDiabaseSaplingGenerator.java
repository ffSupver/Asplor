package com.ffsupver.asplor.block.blocks.sapling;

import com.ffsupver.asplor.Asplor;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class AstraDiabaseSaplingGenerator extends SaplingGenerator {
    private static final RegistryKey<ConfiguredFeature<?,?>> SAPLING_SMALL = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE,new Identifier(Asplor.MOD_ID,"astra_diabase_tree"));
    private static final RegistryKey<ConfiguredFeature<?,?>> SAPLING_LARGE = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE,new Identifier(Asplor.MOD_ID,"astra_diabase_fancy_tree"));
    @Nullable
    @Override
    protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
        if (random.nextInt(6) == 0) {
            return SAPLING_LARGE;
        }
        return SAPLING_SMALL;
    }
}
