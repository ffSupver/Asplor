package com.ffsupver.asplor.block.mechanicalPump;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.ffsupver.asplor.AllBlocks;
import com.simibubi.create.content.contraptions.BlockMovementChecks;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class MechanicalPump extends KineticBlock implements IBE<MechanicalPumpEntity> {
    public static EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    public MechanicalPump(Settings properties) {
        super(properties);
        this.setDefaultState(this.stateManager.getDefaultState().with(HALF, DoubleBlockHalf.LOWER));
        BlockMovementChecks.registerMovementAllowedCheck((state, world, pos) ->
            state.isOf(AllBlocks.MECHANICAL_PUMP.get())?
                    BlockMovementChecks.CheckResult.FAIL: BlockMovementChecks.CheckResult.PASS
        );
    }




    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
        return face==Direction.UP && state.get(HALF).equals(DoubleBlockHalf.UPPER);
    }

    @Override
    public Class<MechanicalPumpEntity> getBlockEntityClass() {
        return MechanicalPumpEntity.class;
    }

    @Override
    public BlockEntityType<? extends MechanicalPumpEntity> getBlockEntityType() {
        return AllBlockEntityTypes.MECHANICAL_PUMP_ENTITY.get();
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HALF});
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onStateReplaced(state, world, pos, newState, isMoving);
        DoubleBlockHalf doubleBlockHalf = state.get(HALF);
        if (!isMoving){
            if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
                BlockPos blockPos = pos.down();
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.isOf(state.getBlock()) && blockState.get(HALF) == DoubleBlockHalf.LOWER) {
                    BlockState blockState2 = blockState.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                    world.setBlockState(blockPos, blockState2, 35);
                }
            } else {
                BlockPos blockPos = pos.up();
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.isOf(state.getBlock()) && blockState.get(HALF) == DoubleBlockHalf.UPPER) {
                    BlockState blockState2 = blockState.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                    world.setBlockState(blockPos, blockState2, 35);
                }
            }
        }
    }


    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        if (blockPos.getY() < world.getTopY() - 1 && world.getBlockState(blockPos.up()).canReplace(ctx)) {
            return this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 3);
    }


}
