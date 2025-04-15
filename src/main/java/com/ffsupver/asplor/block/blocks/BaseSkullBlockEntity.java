package com.ffsupver.asplor.block.blocks;

import com.ffsupver.asplor.AllBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class BaseSkullBlockEntity extends net.minecraft.block.entity.SkullBlockEntity {
    public BaseSkullBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return AllBlockEntityTypes.ZOMBIFIED_COSMONAUT_SKULL_ENTITY;
    }
}
