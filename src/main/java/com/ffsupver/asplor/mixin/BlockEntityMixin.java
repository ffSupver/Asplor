package com.ffsupver.asplor.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {
    @Shadow
    protected World world;
    @Shadow
    protected BlockPos pos;

}
