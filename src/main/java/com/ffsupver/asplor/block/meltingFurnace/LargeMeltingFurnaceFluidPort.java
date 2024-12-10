package com.ffsupver.asplor.block.meltingFurnace;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

public class LargeMeltingFurnaceFluidPort extends Block implements IBE<LargeMeltingFurnaceFluidPortEntity> {
    public LargeMeltingFurnaceFluidPort(Settings settings) {
        super(settings);
    }

    @Override
    public Class<LargeMeltingFurnaceFluidPortEntity> getBlockEntityClass() {
        return LargeMeltingFurnaceFluidPortEntity.class;
    }

    @Override
    public BlockEntityType<? extends LargeMeltingFurnaceFluidPortEntity> getBlockEntityType() {
        return AllBlockEntityTypes.LARGE_MELTING_FURNACE_FLUID_PORT_ENTITY.get();
    }
}
