package com.ffsupver.asplor.block.refinery;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;

public class RefineryController extends Block implements IBE<RefineryControllerEntity> {
    public static final Property<Boolean> ACTIVE = BooleanProperty.of("active");
    public RefineryController(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(ACTIVE,false));
    }

    @Override
    public Class<RefineryControllerEntity> getBlockEntityClass() {
        return RefineryControllerEntity.class;
    }

    @Override
    public BlockEntityType<? extends RefineryControllerEntity> getBlockEntityType() {
        return AllBlockEntityTypes.REFINERY_CONTROLLER_ENTITY.get();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
}
