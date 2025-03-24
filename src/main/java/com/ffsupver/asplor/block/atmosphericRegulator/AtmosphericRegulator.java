
package com.ffsupver.asplor.block.atmosphericRegulator;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AtmosphericRegulator extends Block implements IBE<AtmosphericRegulatorEntity>, IWrenchable {
    public static Property<Direction> FACING = Properties.FACING;
    public AtmosphericRegulator(Settings settings) {
        super(settings);
        this.setDefaultState(getDefaultState().with(FACING,Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayerLookDirection();
        return super.getPlacementState(ctx).with(FACING,direction.getOpposite());
    }

    @Override
    public Class<AtmosphericRegulatorEntity> getBlockEntityClass() {
        return AtmosphericRegulatorEntity.class;
    }

    @Override
    public BlockEntityType<? extends AtmosphericRegulatorEntity> getBlockEntityType() {
        return AllBlockEntityTypes.ATMOSPHERIC_REGULATOR_ENTITY_IRON.get();
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())){
            withBlockEntityDo(world,pos, AtmosphericRegulatorEntity::destroy);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(FACING));
    }
}
