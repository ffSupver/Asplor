package com.ffsupver.asplor.block.alloy_mechanical_press;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class AlloyMechanicalPress extends HorizontalKineticBlock implements IBE<AlloyMechanicalPressEntity> {


    public AlloyMechanicalPress(Settings properties) {
        super(properties);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        if (context instanceof EntityShapeContext
                && ((EntityShapeContext) context).getEntity() instanceof PlayerEntity)
            return AllShapes.CASING_14PX.get(Direction.DOWN);

        return AllShapes.MECHANICAL_PROCESSOR_SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos) {
        return !AllBlocks.BASIN.has(worldIn.getBlockState(pos.down()));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction prefferedSide = getPreferredHorizontalFacing(context);
        if (prefferedSide != null)
            return getDefaultState().with(HORIZONTAL_FACING, prefferedSide);
        return super.getPlacementState(context);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.get(HORIZONTAL_FACING)
                .getAxis();
    }

    @Override
    public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.get(HORIZONTAL_FACING)
                .getAxis();
    }

    @Override
    public Class<AlloyMechanicalPressEntity> getBlockEntityClass() {
        return AlloyMechanicalPressEntity.class;
    }

    @Override
    public BlockEntityType<? extends AlloyMechanicalPressEntity> getBlockEntityType() {
        return AllBlockEntityTypes.ALLOY_MECHANICAL_PRESS_ENTITY.get();
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView reader, BlockPos pos, NavigationType type) {
        return false;
    }


}
