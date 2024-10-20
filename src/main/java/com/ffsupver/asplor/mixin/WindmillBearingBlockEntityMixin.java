package com.ffsupver.asplor.mixin;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.ModTags;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WindmillBearingBlockEntity.class)
public abstract class WindmillBearingBlockEntityMixin extends BlockEntityMixin {
//    @Inject(method = "getGeneratedSpeed" ,at = @At(value = "TAIL"),cancellable = true)
//    public void getGeneratedSpeed(CallbackInfoReturnable<Float> cir){
//        Asplor.LOGGER.info(world.getBiome(pos).isIn(ModTags.Biomes.MOON)+" ");
//        if (world != null && world.getBiome(pos).isIn(ModTags.Biomes.MOON)) {
//            cir.setReturnValue(0f);
//        }
//    }
}
