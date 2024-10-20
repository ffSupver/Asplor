package com.ffsupver.asplor.mixin.moltenMetal;

import com.ffsupver.asplor.ModTags;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.Camera;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public class CameraMixin {
    @Shadow private BlockView area;

    @Shadow @Final private BlockPos.Mutable blockPos;

    @ModifyExpressionValue(
            method = "getSubmersionType",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z",
            ordinal = 1)
    )
    private boolean isInMoltenMetal(boolean original){
        if (!original){
            FluidState fluidState2 = this.area.getFluidState(blockPos);
            return fluidState2.isIn(ModTags.Fluids.MOLTEN_METAL);
        }
        return true;
    }
}
