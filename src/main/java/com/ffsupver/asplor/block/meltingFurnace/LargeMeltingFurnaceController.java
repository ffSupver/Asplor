package com.ffsupver.asplor.block.meltingFurnace;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

public class LargeMeltingFurnaceController extends Block implements IBE<LargeMeltingFurnaceControllerEntity> {
    public LargeMeltingFurnaceController(Settings settings) {
        super(settings);
    }

    @Override
    public Class<LargeMeltingFurnaceControllerEntity> getBlockEntityClass() {
        return LargeMeltingFurnaceControllerEntity.class;
    }

    @Override
    public BlockEntityType<? extends LargeMeltingFurnaceControllerEntity> getBlockEntityType() {
        return AllBlockEntityTypes.LARGE_MELTING_FURNACE_CONTROLLER_ENTITY.get();
    }
}
