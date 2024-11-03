package com.ffsupver.asplor.fluid;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class HydrochloricAcidFluid extends FlowableFluid {
    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_HYDROCHLORIC_ACID;
    }

    @Override
    public Fluid getStill() {
        return ModFluids.HYDROCHLORIC_ACID;
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid==getStill()||fluid==getFlowing();
    }

    @Override
    protected boolean isInfinite(World world) {
        return false;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity()?world.getBlockEntity(pos): null;
        Block.dropStacks(state,world,pos,blockEntity);
    }

    private void playExtinguishEvent(WorldAccess world, BlockPos pos) {
        world.syncWorldEvent(1501, pos, 0);
    }

    @Override
    protected int getFlowSpeed(WorldView world) {
        return 2;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return 1;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.HYDROCHLORIC_ACID_BUCKET;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return state.getHeight(world, pos) >= 0.44444445F && fluid.isIn(FluidTags.WATER) || fluid.isIn(FluidTags.LAVA);
    }

    @Override
    public int getTickRate(WorldView world) {
        return 5;
    }

    @Override
    protected float getBlastResistance() {
        return 100f;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return AllBlocks.HYDROCHLORIC_ACID.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
    }

    @Override
    public boolean isStill(FluidState state) {
        return false;
    }

    @Override
    public int getLevel(FluidState state) {
        return 0;
    }

    @Override
    protected int getNextTickDelay(World world, BlockPos pos, FluidState oldState, FluidState newState) {
        int i = this.getTickRate(world);
        if (!oldState.isEmpty() && !newState.isEmpty() && !(Boolean)oldState.get(FALLING)
                && !(Boolean)newState.get(FALLING) && newState.getHeight(world, pos) > oldState.getHeight(world, pos)
                && world.getRandom().nextInt(4) != 0) {
            i *= 4;
        }

        return i;
    }

    @Override
    protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        if (direction == Direction.DOWN) {
            BlockPos pos1 = pos.down();
            FluidState fluidState2 = world.getFluidState(pos);
            if (this.isIn(FluidTags.WATER)&&fluidState2.isIn(FluidTags.LAVA)) {
                if (state.getBlock() instanceof FluidBlock) {
                    world.setBlockState(pos, Blocks.SAND.getDefaultState(), 3);
                }

                this.playExtinguishEvent(world, pos);
                return;

            }
        }

        super.flow(world, pos, state, direction, fluidState);
    }

    public static class Flowing extends HydrochloricAcidFluid {
        public Flowing() {
        }

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
    public static class Still extends HydrochloricAcidFluid {
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