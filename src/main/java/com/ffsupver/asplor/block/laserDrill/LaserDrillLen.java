package com.ffsupver.asplor.block.laserDrill;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

public class LaserDrillLen extends Block implements IBE<LaserDrillLenEntity>, IWrenchable {
    public LaserDrillLen(Settings settings) {
        super(settings);
    }

    @Override
    public Class<LaserDrillLenEntity> getBlockEntityClass() {
        return LaserDrillLenEntity.class;
    }

    @Override
    public BlockEntityType<? extends LaserDrillLenEntity> getBlockEntityType() {
        return AllBlockEntityTypes.LASER_DRILL_ENTITY_LEN.get();
    }
}
