package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public class SmartMechanicalArm extends KineticBlock implements IBE<SmartMechanicalArmEntity> {
    public SmartMechanicalArm(Settings properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
        return face==Direction.DOWN;
    }

    @Override
    public Class<SmartMechanicalArmEntity> getBlockEntityClass() {
        return SmartMechanicalArmEntity.class;
    }


    @Override
    public BlockEntityType<? extends SmartMechanicalArmEntity> getBlockEntityType() {
        return AllBlockEntityTypes.SMART_MECHANICAL_ARM_ENTITY.get();
    }
}
