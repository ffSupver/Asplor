package com.ffsupver.asplor.fluid.moltenMetal;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.fluid.ModFluids;
import com.ffsupver.asplor.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public abstract class MoltenIron extends MoltenMetal {

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_MOLTEN_IRON;
    }

    @Override
    public Fluid getStill() {
        return ModFluids.MOLTEN_IRON;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.MOLTEN_IRON_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return simpleToBlockState(AllBlocks.MOLTEN_IRON.getDefaultState(),state);
    }

    @Override
    protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        this.flowIntoWater(world,pos,state,direction,fluidState,Blocks.IRON_ORE.getDefaultState());
    }




    public static class Flowing extends MoltenIron{

        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(new Property[]{LEVEL});
        }

        public int getLevel(FluidState state) {
            return (Integer)state.get(LEVEL);
        }

        public boolean isStill(FluidState state) {
            return false;
        }
    }
    public static class Still extends MoltenIron{
        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }
    }
}
