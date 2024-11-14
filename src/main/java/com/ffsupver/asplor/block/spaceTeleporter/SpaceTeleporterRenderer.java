package com.ffsupver.asplor.block.spaceTeleporter;

import com.ffsupver.asplor.AllPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static com.ffsupver.asplor.util.RenderUtil.getBlockLight;
import static com.ffsupver.asplor.util.RenderUtil.getTextureLight;

@Environment(EnvType.CLIENT)
public class SpaceTeleporterRenderer extends SafeBlockEntityRenderer<SpaceTeleporterEntity> {
    public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");

    public SpaceTeleporterRenderer(BlockEntityRendererFactory.Context context) {}

    @Override
    protected void renderSafe(SpaceTeleporterEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider bufferSource, int light, int overlay) {
        renderBeam(be,partialTicks,ms,bufferSource);
        renderInner(be,ms,bufferSource,0.5f,1);
        renderInner(be,ms,bufferSource,0.4f,-1);
    }

    private void renderBeam(SpaceTeleporterEntity be,float partialTicks,MatrixStack ms,VertexConsumerProvider bufferSource){
        int mayY=be.getHighestEntity();
        float outerRadius = 0.25f;
        float innerRadius = 0.2f;
        float f = (float)Math.floorMod(be.getWorld().getTime(), 40) + partialTicks;
        ms.push();
        ms.translate(0.5f,1.0f,0.5f);
        ms.push();
            ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * 2.25F - 45.0F));
            renderBeamLayer(ms,bufferSource.getBuffer(RenderLayer.getBeaconBeam(BEAM_TEXTURE,false)), (float) 160 /255, (float) 252 /255, (float) 255 /255,1.0f,0,mayY,-0,-innerRadius,innerRadius,-0,-innerRadius,0,0,innerRadius,0.0f,1.0f,mayY,-1.0f);
        ms.pop();
        renderBeamLayer(ms,bufferSource.getBuffer(RenderLayer.getBeaconBeam(BEAM_TEXTURE,true)), (float) 160 /255, (float) 252 /255, (float) 255 /255,0.125f,0,mayY,-outerRadius,-outerRadius,outerRadius,-outerRadius,-outerRadius,outerRadius,outerRadius,outerRadius,0.0f,1.0f,mayY,-1.0f);
        ms.pop();

    }

    private static void renderBeamLayer(MatrixStack matrices, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, int height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x1, z1, x2, z2, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x4, z4, x3, z3, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x2, z2, x4, z4, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x3, z3, x1, z1, u1, u2, v1, v2);
    }

    private static void renderBeamFace(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, int height, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x1, z1, u2, v1);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x1, z1, u2, v2);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x2, z2, u1, v2);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x2, z2, u1, v1);
    }

    private static void renderBeamVertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, int y, float x, float z, float u, float v) {
        vertices.vertex(positionMatrix, x, (float)y, z).color(red, green, blue, alpha).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
    }


    private void renderInner(SpaceTeleporterEntity be, MatrixStack ms, VertexConsumerProvider bufferSource, float innerRingScale, float rotationSpeed){
        VertexConsumer solid = bufferSource.getBuffer(RenderLayer.getSolid());
        PartialModel model = AllPartialModels.SPACE_TELEPORTER_INNER;
        SuperByteBuffer modelBuffer = CachedBufferer.partial(model,be.getCachedState());
        float rotationDegrees = (float) be.getWorld().getTime() /20*rotationSpeed;

        ms.push();

        modelBuffer.rotateCentered(Direction.UP, rotationDegrees)
                .rotateCentered(Direction.NORTH, rotationDegrees/4)
                .rotateCentered(Direction.EAST, rotationDegrees/2)
                .translate(.5f, .5f, .5f)
                .scale(innerRingScale)
                .translate(-.5f, -.475f, -.5f);

        draw(modelBuffer,ms,solid,getTextureLight(getBlockLight(be.getWorld(),be.getPos())));

        ms.pop();
    }

    private static void draw(SuperByteBuffer buffer, MatrixStack ms, VertexConsumer vc,int light) {
        buffer.light(light)
                .renderInto(ms, vc);
    }
    @Override
    public int getRenderDistance() {
        return 256;
    }

    @Override
    public boolean isInRenderDistance(SpaceTeleporterEntity blockEntity, Vec3d pos) {
        return true;
    }
}
