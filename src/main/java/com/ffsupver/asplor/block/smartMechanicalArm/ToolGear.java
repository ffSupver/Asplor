package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ToolGear extends Block implements IBE<ToolGearEntity> {
    public ToolGear(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0,0,0,16/16.0,14/16.0,16/16.0);
    }

    @Override
    public Class<ToolGearEntity> getBlockEntityClass() {
        return ToolGearEntity.class;
    }

    @Override
    public BlockEntityType<? extends ToolGearEntity> getBlockEntityType() {
        return AllBlockEntityTypes.TOOL_GEAR_ENTITY.get();
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        IBE.onRemove(state, world, pos, newState);
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
