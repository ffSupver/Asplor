package com.ffsupver.asplor.block.meltingFurnace;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

public class LargeMeltingFurnaceItemPort extends Block implements IBE<LargeMeltingFurnaceItemPortEntity> {
    public LargeMeltingFurnaceItemPort(Settings settings) {
        super(settings);
    }

    @Override
    public Class<LargeMeltingFurnaceItemPortEntity> getBlockEntityClass() {
        return LargeMeltingFurnaceItemPortEntity.class;
    }

    @Override
    public BlockEntityType<? extends LargeMeltingFurnaceItemPortEntity> getBlockEntityType() {
        return AllBlockEntityTypes.LARGE_MELTING_FURNACE_ITEM_PORT_ENTITY.get();
    }
}
