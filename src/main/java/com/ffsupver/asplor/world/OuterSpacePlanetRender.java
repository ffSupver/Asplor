package com.ffsupver.asplor.world;

import com.ffsupver.asplor.util.MathUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import earth.terrarium.adastra.client.dimension.ModDimensionSpecialEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
@Environment(EnvType.CLIENT)

public class OuterSpacePlanetRender extends ModDimensionSpecialEffects {
    private final WorldRenderingData.PlanetRendererData planetRendererData;

    public OuterSpacePlanetRender(WorldRenderingData.PlanetRendererData rendererData) {
        super(rendererData.planetRenderer());
        this.planetRendererData = rendererData;
    }

    private boolean shouldRenderRing(){
        return this.planetRendererData.ringTexture() != null;
    }

    private static void renderRing(MatrixStack poseStack,boolean isOrbit,Identifier ringTexture,ClientWorld clientWorld){
        Tessellator tessellator = Tessellator.getInstance();
        MatrixStack.Entry matrixEntry = poseStack.peek();


        BufferBuilder consumerProvider = tessellator.getBuffer();

        poseStack.push();

        MinecraftClient.getInstance().getTextureManager().bindTexture(ringTexture);

        RenderSystem.setShader(GameRenderer::getPositionTexLightmapColorProgram);
        RenderSystem.setShaderTexture(0, ringTexture);


        Matrix4f positionMatrix = matrixEntry.getPositionMatrix();
        consumerProvider.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_LIGHT_COLOR);

        int yMax = isOrbit ? 180 : 160;
        int yMin = isOrbit ? -1420 : -1440;
        int z = isOrbit ? 40 : 600;
        int x = (int) Math.pow((yMax-yMin) * (yMax - yMin) + 4 * z * z,0.5) / 2;

        int alpha = 255;


        long time = clientWorld.getTimeOfDay() % 24000;
        int t = (int) ((time < 6000 ? time : time < 18000 ? 12000 - time : time - 24000) + 6000) * 255 / 12000;
        int r = t;
        int g = t;
        int b = t;

        consumerProvider.vertex(positionMatrix, x,  yMax,  z).texture(1, 0).light(0).color(r,g,b,alpha).next();
        consumerProvider.vertex(positionMatrix,   x,  yMin, -z).texture(0, 0).light(0).color(r,g,b,alpha).next();
        consumerProvider.vertex(positionMatrix,  -x,  yMin, -z).texture(0, 1).light(0).color(r,g,b,alpha).next();
        consumerProvider.vertex(positionMatrix,  -x,  yMax, z).texture(1, 1).light(0).color(r,g,b,alpha).next();

        BufferRenderer.drawWithGlobalProgram(consumerProvider.end());

        poseStack.pop();
    }


    public static void renderSkybox(MatrixStack poseStack,Identifier texture) {
        Tessellator tessellator = Tessellator.getInstance();
        MatrixStack.Entry matrixEntry = poseStack.peek();

        BufferBuilder consumerProvider = tessellator.getBuffer();

        poseStack.push();
        // 绘制前面
        renderFace(matrixEntry, consumerProvider, texture, MathUtil.CubeFace.N,1);
        // 绘制后面
        renderFace(matrixEntry, consumerProvider, texture, MathUtil.CubeFace.S,1);
        // 绘制左侧
        renderFace(matrixEntry, consumerProvider, texture, MathUtil.CubeFace.W,1);
        // 绘制右侧
        renderFace(matrixEntry, consumerProvider, texture, MathUtil.CubeFace.E,1);
        // 绘制顶部
        renderFace(matrixEntry, consumerProvider, texture, MathUtil.CubeFace.U,1);
        // 绘制底部
        renderFace(matrixEntry, consumerProvider, texture, MathUtil.CubeFace.D,1);
        poseStack.pop();
    }

    private static void renderFace(MatrixStack.Entry matrixEntry, BufferBuilder vertexConsumer,
                                   Identifier texture, MathUtil.CubeFace face,float scaleY) {
        int offsetX = switch (face){
            case U,D,W -> 1;
            case S -> 0;
            case N -> 2;
            case E -> 3;
        };
        int offsetY = switch (face){
            case U -> 0;
            case E,W,N,S -> 1;
            case D -> 2;
        };

        renderFace(matrixEntry,vertexConsumer,texture,face,scaleY,offsetX / 4f,offsetY / 4f,(offsetX + 1) / 4f,(offsetY + 1) / 4f);
    }

    private static void renderFace(MatrixStack.Entry matrixEntry, BufferBuilder vertexConsumer,
                                   Identifier texture, MathUtil.CubeFace face,float scaleY,float u1,float v1,float u2,float v2) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(texture);

            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, texture);

            Matrix4f positionMatrix = matrixEntry.getPositionMatrix();
            vertexConsumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

            MathUtil.CubeNode node1 = face.nodes[0];
            MathUtil.CubeNode node2 = face.nodes[1];
            MathUtil.CubeNode node3 = face.nodes[2];
            MathUtil.CubeNode node4 = face.nodes[3];

            vertexConsumer.vertex(positionMatrix, node1.x * 100, node1.y * 100 * scaleY, node1.z * 100).texture(u2, v1).next();
            vertexConsumer.vertex(positionMatrix, node2.x * 100, node2.y * 100 * scaleY, node2.z * 100).texture(u1, v1).next();
            vertexConsumer.vertex(positionMatrix, node3.x * 100, node3.y * 100 * scaleY, node3.z * 100).texture(u1, v2).next();
            vertexConsumer.vertex(positionMatrix, node4.x * 100, node4.y * 100 * scaleY, node4.z * 100).texture(u2, v2).next();

            BufferRenderer.drawWithGlobalProgram(vertexConsumer.end());
    }

    @Override
    public boolean renderSky(ClientWorld level, int ticks, float partialTick, MatrixStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        boolean r = super.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
        boolean isOrbit = planetRendererData.isOrbit();
        if (shouldRenderRing()){
                renderRing(poseStack, isOrbit, planetRendererData.ringTexture(),level);
        }


        Tessellator tessellator = Tessellator.getInstance();
        MatrixStack.Entry matrixEntry = poseStack.peek();

        BufferBuilder consumerProvider = tessellator.getBuffer();

        poseStack.push();
        renderFace(matrixEntry,consumerProvider,planetRendererData.planetTexture(), MathUtil.CubeFace.D, isOrbit ? 1.5f : 1.2f,0,0,1,1);
        poseStack.pop();


        return r;
    }



}
