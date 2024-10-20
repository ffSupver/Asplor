package com.ffsupver.asplor.block.battery;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.ffsupver.asplor.block.EnergyConnectiveHandler;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class Battery extends Block implements IBE<BatteryEntity> {
    public static final EnumProperty<shape> SHAPE= EnumProperty.of("shape",shape.class);
    public static final BooleanProperty BOTTOM = BooleanProperty.of("bottom");
    public static final BooleanProperty TOP = BooleanProperty.of("top");
    public Battery(Settings settings) {

        super(settings);
        setDefaultState(getDefaultState()
                .with(SHAPE,shape.SINGLE)
                .with(BOTTOM,true)
                .with(TOP,true)
        );
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        if (world.isClient)
            return;
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;
        Consumer<BatteryEntity> consumer = BatteryEntity::updateConnectivity;
        withBlockEntityDo(world,pos,consumer);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof BatteryEntity))
                return;
            BatteryEntity batteryBe = (BatteryEntity) be;
            world.removeBlockEntity(pos);
            EnergyConnectiveHandler.splitMulti(batteryBe);
        }
    }

    @Override
    public Class<BatteryEntity> getBlockEntityClass() {
        return BatteryEntity.class;
    }

    @Override
    public BlockEntityType<? extends BatteryEntity> getBlockEntityType() {
        return AllBlockEntityTypes.BATTERY_ENTITY.get();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHAPE,BOTTOM,TOP);
    }

    public enum shape implements StringIdentifiable{
        INNER,CORNER_ES,CORNER_WN,CORNER_SW,CORNER_NE,SINGLE,EDGE_E,EDGE_S,EDGE_W,EDGE_N;

        @Override
        public String asString() {
            return name().toLowerCase();
        }
    }

    public static boolean isBattery(BlockState state){return state.getBlock() instanceof Battery;}
}
