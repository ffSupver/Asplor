package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.entity.custom.Meteorite;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class MeteoriteRenderer extends EntityRenderer<Meteorite> {

    private BlockRenderManager blockRenderManager ;
    protected MeteoriteRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
       this.blockRenderManager =ctx.getBlockRenderManager();
    }

    @Override
    public Identifier getTexture(Meteorite entity) {
        return null;
    }

    @Override
    public void render(Meteorite entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        float scale = entity.getType().getWidth();
        float offset = 0.5f;
        matrices.scale(scale,scale,scale);
        matrices.translate(0,offset,0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getRotationAxis()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getRotation()));
        matrices.translate(-offset,-offset,-offset);

        this.blockRenderManager.renderBlockAsEntity(AllBlocks.METEORITE.getDefaultState(),matrices,vertexConsumers,light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        }
}
