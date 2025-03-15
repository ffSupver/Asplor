package com.ffsupver.asplor.util;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import earth.terrarium.adastra.client.dimension.ModDimensionSpecialEffects;
import earth.terrarium.adastra.client.utils.DimensionRenderingUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;


public final class RenderUtil {
    public static Box createRenderBoundingBox(BlockPos pos, Double renderDistance) {
        return new Box(pos.getX()+0.5+renderDistance,pos.getY()+0.5+renderDistance,pos.getZ()+0.5+renderDistance,
                pos.getX()+0.5-renderDistance,pos.getY()+0.5-renderDistance,pos.getZ()+0.5-renderDistance);
    }


    public static NbtCompound addDescription(NbtCompound nbt, Text text, Text textToReplace){
        NbtCompound displayNBT = nbt.getCompound("display");
        NbtList loreList = displayNBT.contains("Lore", 9) ? displayNBT.getList("Lore", 8) : new NbtList();
        if (!textToReplace.equals(Text.empty())){
            for (int i = 0; i < loreList.size(); i++) {
                String existingText =loreList.getString(i);
                if (existingText != null && existingText.equals(Text.Serializer.toJson(textToReplace))) {
                    loreList.set(i, NbtString.of(Text.Serializer.toJson(text))); // 替换为新文本
                    return nbt; // 替换后直接返回
                }
            }
        }

        loreList.add(NbtString.of(Text.Serializer.toJson(text)));
        displayNBT.put("Lore", loreList);
        nbt.put("display", displayNBT);
        return nbt;
    }


    public static void renderModel(BlockEntity be, MatrixStack ms, VertexConsumerProvider bufferSource, PartialModel model){
        renderModel(be, ms, bufferSource, model,LightmapTextureManager.MAX_LIGHT_COORDINATE);
    }

    public static void renderModel(BlockEntity be, MatrixStack ms, VertexConsumerProvider bufferSource, PartialModel model,int worldLight,boolean solid){
        if (solid){
            renderModel(be, ms, bufferSource, model,getTextureLight(worldLight));
        }else {
            renderModel(be, ms, bufferSource, model,LightmapTextureManager.MAX_LIGHT_COORDINATE);
        }
    }

    public static int getBlockLight(World world,BlockPos pos){
        return world.getLightLevel(LightType.BLOCK,pos);
    }

    public static int getTextureLight(int blockLight){
        return LightmapTextureManager.MAX_LIGHT_COORDINATE - (15 - blockLight )*16;
    }

    public static void renderModel(BlockEntity be, MatrixStack ms, VertexConsumerProvider bufferSource, PartialModel model,int light){
        VertexConsumer solid = bufferSource.getBuffer(RenderLayer.getSolid());
        SuperByteBuffer modelBuffer = CachedBufferer.partial(model,be.getCachedState());
        draw(modelBuffer,ms,solid,light);
    }

    public static void draw(SuperByteBuffer buffer, MatrixStack ms, VertexConsumer vc,int light) {
        buffer.light(light)
                .renderInto(ms, vc);
    }

    @Environment(EnvType.CLIENT)
    public static void registerDimensionEffects(RegistryKey<World> dimension, ModDimensionSpecialEffects effects){
        DimensionRenderingRegistry.registerDimensionEffects(dimension.getValue(), effects);
        if (effects.renderer().customClouds()) {
            DimensionRenderingRegistry.registerCloudRenderer(dimension, (context) -> {
                Vec3d camera = context.camera().getPos();
                effects.renderClouds(context.world(), DimensionRenderingUtils.getTicks(), context.tickDelta(), context.matrixStack(), camera.x, camera.y, camera.z, context.projectionMatrix());
            });
        }

        if (effects.renderer().customSky()) {
            DimensionRenderingRegistry.registerSkyRenderer(dimension, (context) -> {
                effects.renderSky(context.world(), DimensionRenderingUtils.getTicks(), context.tickDelta(), context.matrixStack(), context.camera(), context.projectionMatrix(), false, () -> {
                });
            });
        }

        if (effects.renderer().customWeather()) {
            DimensionRenderingRegistry.registerWeatherRenderer(dimension, (context) -> {
                Vec3d camera = context.camera().getPos();
                effects.renderSnowAndRain(context.world(), DimensionRenderingUtils.getTicks(), context.tickDelta(), context.lightmapTextureManager(), camera.x, camera.y, camera.z);
            });
        }
    }

    public static void checkAndRegisterDimensionEffects(RegistryKey<World> worldKey, ModDimensionSpecialEffects effects){
        if (DimensionRenderingRegistry.getSkyRenderer(worldKey) == null){
            registerDimensionEffects(worldKey,effects);
        }
    }

    public static boolean isShiftPress(){
        return Screen.hasShiftDown();
    }
}
