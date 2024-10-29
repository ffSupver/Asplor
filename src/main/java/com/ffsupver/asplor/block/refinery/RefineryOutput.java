package com.ffsupver.asplor.block.refinery;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

public class RefineryOutput extends Block implements IBE<RefineryOutputEntity> {
    public RefineryOutput(Settings settings) {
        super(settings);
    }

    @Override
    public Class<RefineryOutputEntity> getBlockEntityClass() {
        return RefineryOutputEntity.class;
    }

    @Override
    public BlockEntityType<? extends RefineryOutputEntity> getBlockEntityType() {
        return AllBlockEntityTypes.REFINERY_OUTPUT_ENTITY.get();
    }
}
