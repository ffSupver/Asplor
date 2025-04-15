package com.ffsupver.asplor.mixin;

import com.ffsupver.asplor.entity.client.ZombifiedCosmonautRenderer;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static com.ffsupver.asplor.entity.custom.ZombifiedCosmonaut.SkullType.ZOMBIFIED_COSMONAUT;

@Environment(EnvType.CLIENT)
@Mixin(SkullBlockEntityRenderer.class)
public class SkullBlockEntityRendererMixin {
    @Shadow
    @Final
    private static Map<SkullBlock.SkullType, Identifier> TEXTURES;

    @Inject(
            method = "getModels",
            at = @At(value = "RETURN"),
            cancellable = true)
    private static void getModels(EntityModelLoader modelLoader, CallbackInfoReturnable<Map<SkullBlock.SkullType, SkullBlockEntityModel>> cir) {
        ImmutableMap.Builder<SkullBlock.SkullType,SkullBlockEntityModel> builder = ImmutableMap.builder();
        builder.putAll(cir.getReturnValue());
        builder.put(ZOMBIFIED_COSMONAUT, new SkullEntityModel(modelLoader.getModelPart(EntityModelLayers.ZOMBIE_HEAD)));
        cir.setReturnValue(builder.build());
    }

    @Inject(
            method = "<clinit>",
            at = @At("TAIL")
    )
    private static void addTexture(CallbackInfo ci){
        TEXTURES.put(ZOMBIFIED_COSMONAUT, ZombifiedCosmonautRenderer.ZOMBIFIED_COSMONAUT_TEXTURE);
    }




}
