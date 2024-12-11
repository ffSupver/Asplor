package com.ffsupver.asplor.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public interface GenerateBlockWhenLava {
   default BlockState getBlockToGenerateWhenLava(){
       return Blocks.STONE.getDefaultState();
   }
}
