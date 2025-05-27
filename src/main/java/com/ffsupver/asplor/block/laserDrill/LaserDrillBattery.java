package com.ffsupver.asplor.block.laserDrill;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;

public class LaserDrillBattery extends Block implements IBE<LaserDrillBatteryEntity>, IWrenchable {
    public static Property<Integer> ENERGY = IntProperty.of("energy",0,4);
    public LaserDrillBattery(Settings settings) {
        super(settings);
        this.setDefaultState(getDefaultState().with(ENERGY,0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(ENERGY));
    }

    @Override
    public Class<LaserDrillBatteryEntity> getBlockEntityClass() {
        return LaserDrillBatteryEntity.class;
    }

    @Override
    public BlockEntityType<? extends LaserDrillBatteryEntity> getBlockEntityType() {
        return AllBlockEntityTypes.LASER_DRILL_BATTERY_ENTITY.get();
    }
}
