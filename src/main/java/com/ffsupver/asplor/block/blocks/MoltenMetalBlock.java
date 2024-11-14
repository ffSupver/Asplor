package com.ffsupver.asplor.block.blocks;

import net.fabricmc.fabric.api.registry.LandPathNodeTypesRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class MoltenMetalBlock extends FluidBlock {
    public MoltenMetalBlock(FlowableFluid fluid, Settings settings) {
        super(fluid, settings);
        LandPathNodeTypesRegistry.register(this, PathNodeType.LAVA,null);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}
