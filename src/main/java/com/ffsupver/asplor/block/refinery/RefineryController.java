package com.ffsupver.asplor.block.refinery;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

public class RefineryController extends Block implements IBE<RefineryControllerEntity> {
    public RefineryController(Settings settings) {
        super(settings);
    }

    @Override
    public Class<RefineryControllerEntity> getBlockEntityClass() {
        return RefineryControllerEntity.class;
    }

    @Override
    public BlockEntityType<? extends RefineryControllerEntity> getBlockEntityType() {
        return AllBlockEntityTypes.REFINERY_CONTROLLER_ENTITY.get();
    }
}
