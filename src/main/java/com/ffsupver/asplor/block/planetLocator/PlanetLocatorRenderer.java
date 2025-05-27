package com.ffsupver.asplor.block.planetLocator;

import com.ffsupver.asplor.Asplor;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static com.ffsupver.asplor.util.MathUtil.cos;
import static com.ffsupver.asplor.util.MathUtil.sin;
import static com.ffsupver.asplor.util.RenderUtil.renderVertex;

public class PlanetLocatorRenderer extends SmartBlockEntityRenderer<PlanetLocatorEntity> {
    public PlanetLocatorRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(PlanetLocatorEntity blockEntity, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
        float planetSize = blockEntity.getPlanetRadius();

        if (blockEntity.hasPlanet()){
            float planetRotation = blockEntity.getWorld().getTime() % 360;
            renderPlanet(ms, buffer, 0.5f, 1.55f, 0.5f, 23, planetRotation, 0, 5 * planetSize);
            float planetRadius = planetSize * 10;
            float planetPosition = (float) Math.toRadians((double) (blockEntity.getWorld().getTime() % 720) / 2);
            renderPlanet(ms, buffer, 0.5f + planetRadius * cos(planetPosition), 1.55f + 4 * planetSize, 0.5f + planetRadius * sin(planetPosition), 45, planetRotation, 0,planetSize );
            renderFace(ms, buffer);
        }
        renderItem(blockEntity,ms,buffer,light,planetSize);
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
    }

    private void renderItem(PlanetLocatorEntity blockEntity, MatrixStack ms,VertexConsumerProvider buffer, int light,float planetSize){
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack navItem = blockEntity.getNavItem();
        ItemStack outputItem = blockEntity.getOutputItem();
        float scale = 0.5f;
        float process = 1 - blockEntity.getProcess();

        //输出
        if (!outputItem.isEmpty()){
            ms.push();
            ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            ms.translate(.5f, .5f, -1.3f );
            ms.scale(scale, scale, scale);
            itemRenderer.renderItem(outputItem, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, ms, buffer, blockEntity.getWorld(), 0);
            ms.pop();
        }

        //传输
        if (process > 0){
            ms.push();
            ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            ms.translate(.5f, .5f , -.5f - process * (.8f));
            ms.scale(scale, scale, scale);
            itemRenderer.renderItem(navItem, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, ms, buffer, blockEntity.getWorld(), 0);
            ms.pop();
        }

        //输入
        if (navItem.getCount() > 1 || process <= 0){
            ms.push();
            ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            ms.translate(.5f, .5f, -.5f);
            ms.scale(scale, scale, scale);
            itemRenderer.renderItem(navItem, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, ms, buffer, blockEntity.getWorld(), 0);
            ms.pop();
        }
    }

    private void renderFace(MatrixStack ms, VertexConsumerProvider bufferSource){
        BufferBuilder builder = (BufferBuilder) bufferSource.getBuffer(RenderLayer.getBeaconBeam(new Identifier(Asplor.MOD_ID,"textures/block/planet_locator_light.png"),true));
        Matrix3f normalMatrix = ms.peek().getNormalMatrix();
        Matrix4f positionMatrix = ms.peek().getPositionMatrix();

        int red = 60;
        int green = 20;
        int blue = 20;
        int redH = 160;
        int greenH = 120;
        int blueH = 120;

        renderVertex(builder,positionMatrix,normalMatrix,
                0.1f, 0.5f, 0.1f,
                0.2f, 1.3f, 0.2f,
                0.2f, 1.3f, 0.8f,
                0.1f, 0.5f, 0.9f,
                red,green,blue,redH,greenH,blueH
                );
        renderVertex(builder,positionMatrix,normalMatrix,
                0.1f, 0.5f, 0.9f,
                0.2f, 1.3f, 0.8f,
                0.8f, 1.3f, 0.8f,
                0.9f, 0.5f, 0.9f,
                red,green,blue,redH,greenH,blueH
        );
        renderVertex(builder,positionMatrix,normalMatrix,
                0.9f, 0.5f, 0.9f,
                0.8f, 1.3f, 0.8f,
                0.8f, 1.3f, 0.2f,
                0.9f, 0.5f, 0.1f,
                red,green,blue,redH,greenH,blueH
        );
        renderVertex(builder,positionMatrix,normalMatrix,
                0.9f, 0.5f, 0.1f,
                0.8f, 1.3f, 0.2f,
                0.2f, 1.3f, 0.2f,
                0.1f, 0.5f, 0.1f,
                red,green,blue,redH,greenH,blueH
        );
    }


    private void renderPlanet(MatrixStack ms,VertexConsumerProvider bufferSource,float xC,float yC,float zC,float rXDeg,float rYDeg,float rZDeg,float halfWidth) {
        BufferBuilder builder = (BufferBuilder) bufferSource.getBuffer(RenderLayer.getBeaconBeam(new Identifier(Asplor.MOD_ID, "textures/block/planet_locator_planet.png"), false));
        Matrix3f normalMatrix = ms.peek().getNormalMatrix();
        Matrix4f positionMatrix = ms.peek().getPositionMatrix();

         yC += halfWidth;



        float rX = (float) Math.toRadians(rXDeg);
        float rY = (float) Math.toRadians(rYDeg);
        float rZ = (float) Math.toRadians(rZDeg);


        int red = 100;
        int green = 100;
        int blue = 100;

        float cosX = cos(rX), sinX = sin(rX);
        float cosY = cos(rY), sinY = sin(rY);
        float cosZ = cos(rZ), sinZ = sin(rZ);

        // 旋转矩阵元素
        float m00 = cosY * cosZ;
        float m01 = sinX * sinY * cosZ - cosX * sinZ;
        float m02 = cosX * sinY * cosZ + sinX * sinZ;
        float m10 = cosY * sinZ;
        float m11 = sinX * sinY * sinZ + cosX * cosZ;
        float m12 = cosX * sinY * sinZ - sinX * cosZ;
        float m20 = -sinY;
        float m21 = sinX * cosY;
        float m22 = cosX * cosY;

        float x1 = xC + halfWidth * (m00+m01+m02);
        float y1 = yC + halfWidth * (m10+m11+m12);
        float z1 = zC + halfWidth * (m20+m21+m22);
        float x2 = xC + halfWidth * (m00+m01-m02);
        float y2 = yC + halfWidth * (m10+m11-m12);
        float z2 = zC + halfWidth * (m20+m21-m22);
        float x3 = xC + halfWidth * (m00-m01+m02);
        float y3 = yC + halfWidth * (m10-m11+m12);
        float z3 = zC + halfWidth * (m20-m21+m22);
        float x4 = xC + halfWidth * (m00-m01-m02);
        float y4 = yC + halfWidth * (m10-m11-m12);
        float z4 = zC + halfWidth * (m20-m21-m22);
        float x5 = xC + halfWidth * (-m00+m01+m02);
        float y5 = yC + halfWidth * (-m10+m11+m12);
        float z5 = zC + halfWidth * (-m20+m21+m22);
        float x6 = xC + halfWidth * (-m00+m01-m02);
        float y6 = yC + halfWidth * (-m10+m11-m12);
        float z6 = zC + halfWidth * (-m20+m21-m22);
        float x7 = xC + halfWidth * (-m00-m01+m02);
        float y7 = yC + halfWidth * (-m10-m11+m12);
        float z7 = zC + halfWidth * (-m20-m21+m22);
        float x8 = xC + halfWidth * (-m00-m01-m02);
        float y8 = yC + halfWidth * (-m10-m11-m12);
        float z8 = zC + halfWidth * (-m20-m21-m22);


        renderVertex(builder, positionMatrix, normalMatrix,
                x1, y1, z1,
                x3, y3, z3,
                x4, y4, z4,
                x2,y2,z2,
                red, green, blue, red, green, blue
        );
        renderVertex(builder, positionMatrix, normalMatrix,
                x6, y6, z6,
                x5, y5, z5,
                x1, y1, z1,
                x2,y2,z2,
                red, green, blue, red, green, blue
        );
        renderVertex(builder, positionMatrix, normalMatrix,
                x5, y5, z5,
                x6, y6, z6,
                x8,y8,z8,
                x7, y7, z7,
                red, green, blue, red, green, blue
        );
        renderVertex(builder, positionMatrix, normalMatrix,
                x1, y1, z1,
                x5, y5, z5,
                x7, y7, z7,
                x3,y3,z3,
                red, green, blue, red, green, blue
        );
        renderVertex(builder, positionMatrix, normalMatrix,
                x3, y3, z3,
                x7, y7, z7,
                x8, y8, z8,
                x4, y4, z4,
                red, green, blue, red, green, blue
        );
        renderVertex(builder, positionMatrix, normalMatrix,
                x4, y4, z4,
                x8, y8, z8,
                x6, y6, z6,
                x2, y2, z2,
                red, green, blue, red, green, blue
        );
    }
}
