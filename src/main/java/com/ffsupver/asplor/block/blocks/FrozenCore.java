package com.ffsupver.asplor.block.blocks;

import com.ffsupver.asplor.Asplor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FrozenCore extends Block {
    public FrozenCore(Settings settings) {
        super(settings);
    }


    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        super.onEntityLand(world, entity);
        entity.setInPowderSnow(true);
    }
}
