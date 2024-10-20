package com.ffsupver.asplor.block.generator;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class Generator extends KineticBlock implements IBE<GeneratorEntity>, IWrenchable {
    public static final DirectionProperty FACING;
    public Generator(Settings properties) {
        super(properties);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING,Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING,ctx.getPlayerLookDirection().getOpposite());
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.get(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
        return state.get(FACING)==face;
    }

    @Override
    public Class<GeneratorEntity> getBlockEntityClass() {
        return GeneratorEntity.class;
    }

    @Override
    public BlockEntityType<? extends GeneratorEntity> getBlockEntityType() {
        return AllBlockEntityTypes.GENERATOR_ENTITY.get();
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING,rotation.rotate(state.get(FACING)));
    }
    static {
        FACING= Properties.FACING;
    }
}
