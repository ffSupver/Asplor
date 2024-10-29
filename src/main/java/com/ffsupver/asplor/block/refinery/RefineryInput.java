package com.ffsupver.asplor.block.refinery;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

public class RefineryInput extends Block implements IBE<RefineryInputEntity> {
    public RefineryInput(Settings settings) {
        super(settings);
    }

    @Override
    public Class<RefineryInputEntity> getBlockEntityClass() {
        return RefineryInputEntity.class;
    }

    @Override
    public BlockEntityType<? extends RefineryInputEntity> getBlockEntityType() {
        return AllBlockEntityTypes.REFINERY_INPUT_ENTITY.get();
    }
}
