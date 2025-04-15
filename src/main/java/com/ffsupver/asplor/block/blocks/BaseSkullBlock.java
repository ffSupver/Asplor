package com.ffsupver.asplor.block.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class BaseSkullBlock extends SkullBlock {
    public BaseSkullBlock(SkullType skullType, Settings settings) {
        super(skullType, settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BaseSkullBlockEntity(pos,state);
    }
}
