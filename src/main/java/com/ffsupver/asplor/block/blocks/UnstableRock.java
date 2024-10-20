package com.ffsupver.asplor.block.blocks;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.ModTags;
import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class UnstableRock extends Block {
    public static final IntProperty STAGE = IntProperty.of("stage",0,3);
    public UnstableRock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(STAGE,0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
            if (world.getBiome(pos).isIn(ModTags.Biomes.MOON) && random.nextInt(2) == 0) {
                BlockState newState;
                if (state.get(STAGE) != 3) {
                    newState = state.with(STAGE,state.get(STAGE)+1);
                }else {
                    newState = ModBlocks.MOON_DESH_ORE.get().getDefaultState();
                }
                world.setBlockState(pos,newState,3);
            }

    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }


}
