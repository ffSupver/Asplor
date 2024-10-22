package com.ffsupver.asplor.fluid.moltenMetal;

import com.ffsupver.asplor.ModTags;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class MoltenMetal extends FlowableFluid {

    @Override
     public abstract Fluid getFlowing() ;

    @Override
    public abstract Fluid getStill() ;

    @Override
    public abstract Item getBucketItem() ;

    @Override
    protected abstract BlockState toBlockState(FluidState state) ;

    protected BlockState simpleToBlockState(BlockState blockState,FluidState fluidState){
        return blockState.with(FluidBlock.LEVEL,getBlockStateLevel(fluidState));
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
        return 2;
    }
    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return state.getHeight(world, pos) >= 0.44444445F && fluid.isIn(FluidTags.WATER);
    }
    @Override
    public int getTickRate(WorldView world) {
        return 10;
    }

    @Override
    protected float getBlastResistance() {
        return 100f;
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
    protected abstract void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) ;

    protected void flowIntoWater(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState,BlockState blockStateInWater){
        if (direction == Direction.DOWN) {
            FluidState fluidState2 = world.getFluidState(pos);
            boolean isWater = fluidState2.isIn(FluidTags.WATER)&&(!(fluidState2.isIn(ModTags.Fluids.REFINED_OIL)||fluidState2.isIn(ModTags.Fluids.REFINED_OIL)));
            if (this.isIn(ModTags.Fluids.MOLTEN_METAL)&&isWater) {
                if (state.getBlock() instanceof FluidBlock) {
                        world.setBlockState(pos,
                                world.getRandom().nextFloat() > 0.995 ? blockStateInWater : Blocks.STONE.getDefaultState(),
                                3);
                }
                this.playExtinguishEvent(world, pos);
                return;

            }
        }

        super.flow(world, pos, state, direction, fluidState);
    }

    protected void flowIntoWater(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState,BlockState blockStateInWaterCommon,BlockState blockStateInWater){
        if (direction == Direction.DOWN) {
            FluidState fluidState2 = world.getFluidState(pos);
            boolean isWater = fluidState2.isIn(FluidTags.WATER)&&(!(fluidState2.isIn(ModTags.Fluids.REFINED_OIL)||fluidState2.isIn(ModTags.Fluids.REFINED_OIL)));
            if (this.isIn(ModTags.Fluids.MOLTEN_METAL)&&isWater) {
                if (state.getBlock() instanceof FluidBlock) {
                    world.setBlockState(pos,
                            world.getRandom().nextFloat() > 0.995 ? blockStateInWater : blockStateInWaterCommon,
                            3);
                }
                this.playExtinguishEvent(world, pos);
                return;

            }
        }

        super.flow(world, pos, state, direction, fluidState);
    }
}


