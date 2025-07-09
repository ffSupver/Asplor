package com.ffsupver.asplor.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class MeltedBedrock extends Block {
    public static final Property<Integer> HEAT = IntProperty.of("heat",0,1);
    public MeltedBedrock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(HEAT,0));
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (random.nextInt(3) == 0){
            int heat = state.get(HEAT);
            BlockState newState = Blocks.BEDROCK.getDefaultState();
            if (heat != 0){
                newState = state.with(HEAT,heat - 1);
            }
            world.setBlockState(pos,newState);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(HEAT));
    }
}
