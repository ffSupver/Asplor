package com.ffsupver.asplor.fluid.moltenMetal;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.fluid.ModFluids;
import com.ffsupver.asplor.item.ModItems;
import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public abstract class MoltenDesh extends MoltenMetal {

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_MOLTEN_DESH;
    }

    @Override
    public Fluid getStill() {
        return ModFluids.MOLTEN_DESH;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.MOLTEN_DESH_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return simpleToBlockState(AllBlocks.MOLTEN_DESH.getDefaultState(),state);
    }

    @Override
    protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        this.flowIntoWater(world,pos,state,direction,fluidState, ModBlocks.MOON_STONE.get().getDefaultState(),ModBlocks.MOON_DESH_ORE.get().getDefaultState());
    }




    public static class Flowing extends MoltenDesh {

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
    public static class Still extends MoltenDesh {
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