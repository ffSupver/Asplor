package com.ffsupver.asplor.block.rocketFuelLoader;

import com.ffsupver.asplor.AllPartialModels;
import com.ffsupver.asplor.util.RenderUtil;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static com.ffsupver.asplor.util.RenderUtil.getBlockLight;
import static com.ffsupver.asplor.util.RenderUtil.getTextureLight;

public class RocketFuelLoaderRenderer extends SafeBlockEntityRenderer<RocketFuelLoaderEntity> {
    public RocketFuelLoaderRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    protected void renderSafe(RocketFuelLoaderEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider bufferSource, int light, int overlay) {
        if (be.getRocketPos() == null){
            return;
        }
        float process = be.getProcess();
        boolean twoPipe = calculateDistance(be) > 1;
        float pipeProcess = Math.max(process - 15/32f,0) *32/17;
        int worldLight = getTextureLight(getBlockLight(be.getWorld(),be.getPos().up()));

//        renderFluid(be,ms,bufferSource);

        ms.push();
        ms.translate(.5f,Math.min(2*process,15/16f)+1/16f,.5f);
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(getRotationRad(be.getPos(),be.getRocketPos()))));
        ms.translate(-.5f,0,-.5f);
        RenderUtil.renderModel(be, ms, bufferSource, AllPartialModels.ROCKET_FUEL_LOADER_PIPE_BASE,worldLight);
        ms.translate(2/16f,0,0);

        ms.translate(.5f,15/16f,0);
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(pipeProcess*90));
        ms.translate(-.5f,-15/16f,0);
        RenderUtil.renderModel(be, ms, bufferSource, AllPartialModels.ROCKET_FUEL_LOADER_PIPE_EXTEND,worldLight);

        if (twoPipe){
            ms.translate(0,-Math.min(calculateDistance(be) - 12/16f,12/16f)*pipeProcess,0);
            RenderUtil.renderModel(be, ms, bufferSource, AllPartialModels.ROCKET_FUEL_LOADER_PIPE_EXTEND,worldLight);
        }

        ms.pop();
    }

    private float getRotationRad(BlockPos pos,BlockPos rocketPos){
        if (pos.getX()==rocketPos.getX()){
            return (float) ((pos.getZ()-rocketPos.getZ()>0 ? 1 : -1)*Math.PI/2);
        }
        float aTan =(float) Math.atan((double) (pos.getZ() - rocketPos.getZ()) /(rocketPos.getX()-pos.getX()));
        return pos.getX() < rocketPos.getX() ? aTan : (float) (Math.PI + aTan);
    }

    private float calculateDistance(RocketFuelLoaderEntity be){
        BlockPos pos = be.getPos();
        BlockPos rPos = be.getRocketPos();
        return (float) Math.pow(Math.pow(pos.getX()-rPos.getX(),2)+Math.pow(pos.getZ()-rPos.getZ(),2),0.5f)-0.5f;
    }

    private void renderFluid(RocketFuelLoaderEntity be,MatrixStack ms,VertexConsumerProvider bufferSource){
        World world = be.getWorld();
        BlockState state = be.getCachedState();
        BlockPos pos = be.getPos();
        VertexConsumer consumer = bufferSource.getBuffer(RenderLayer.getSolid());
        // 假设你要在顶部渲染一个矩形
        float x1 = 0.0f, y1 = 0.0f, z1 = 0.0f;
        float x2 = 1.0f, y2 = 1.0f, z2 = 0.0f;
        float[] color = new float[]{1.0f,0.0f,0.0f};
        renderRectangle(consumer, ms, x1, y1, z1, x2, y2, z2, color, 16);
    }

    private void renderRectangle(VertexConsumer consumer, MatrixStack matrices, float x1, float y1, float z1, float x2, float y2, float z2, float[] color, int light) {
        matrices.push();

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix(); // 获取位置矩阵
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();     // 获取法线矩阵

        // 解除颜色固定
        consumer.unfixColor();

        // 渲染矩形顶点
        consumer.vertex(positionMatrix, x1, y1, z1).color(color[0], color[1], color[2], 1.0f).light(light).normal(normalMatrix, 0, 1, 0).texture(0.0f, 0.0f).overlay(0).next();
        consumer.vertex(positionMatrix, x2, y1, z1).color(color[0], color[1], color[2], 1.0f).light(light).normal(normalMatrix, 0, 1, 0).texture(0.0f, 0.0f).overlay(0).next();
        consumer.vertex(positionMatrix, x2, y2, z1).color(color[0], color[1], color[2], 1.0f).light(light).normal(normalMatrix, 0, 1, 0).texture(0.0f, 0.0f).overlay(0).next();
        consumer.vertex(positionMatrix, x1, y2, z1).color(color[0], color[1], color[2], 1.0f).light(light).normal(normalMatrix, 0, 1, 0).texture(0.0f, 0.0f).overlay(0).next();

        matrices.pop();
    }

}
