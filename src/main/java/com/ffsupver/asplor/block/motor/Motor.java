package com.ffsupver.asplor.block.motor;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public class Motor extends DirectionalKineticBlock implements IBE<MotorEntity> {
    public Motor(Settings properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.get(FACING)
                .getAxis();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction preferred = getPreferredFacing(context);
        if ((context.getPlayer() != null && context.getPlayer()
                .isSneaking()) || preferred == null)
            return super.getPlacementState(context);
        return getDefaultState().with(FACING, preferred);
    }

    @Override
    public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
        return face == state.get(FACING);
    }

    @Override
    public Class<MotorEntity> getBlockEntityClass() {
        return MotorEntity.class;
    }

    @Override
    public BlockEntityType<? extends MotorEntity> getBlockEntityType() {
        return AllBlockEntityTypes.MOTOR_ENTITY.get();
    }
}
