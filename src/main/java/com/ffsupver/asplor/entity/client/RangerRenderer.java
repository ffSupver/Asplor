package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.entity.custom.Ranger;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class RangerRenderer extends EntityRenderer<Ranger> {

    private BlockRenderManager blockRenderManager ;
    protected RangerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
       this.blockRenderManager =ctx.getBlockRenderManager();
    }

    @Override
    public Identifier getTexture(Ranger entity) {
        return null;
    }

    @Override
    public void render(Ranger entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        float scale = entity.getType().getWidth();
        matrices.scale(scale,scale,scale);
        float offset = 0.5f;
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(entity.getYaw()));
        matrices.translate(-offset,0,-offset);

        matrices.translate(0,0,1);
        this.blockRenderManager.renderBlockAsEntity(AllBlocks.ASSEMBLER.getDefaultState(),matrices,vertexConsumers,light, OverlayTexture.DEFAULT_UV);
        matrices.translate(0,0,-1);
        this.blockRenderManager.renderBlockAsEntity(AllBlocks.CHARGED_ALLOY_BLOCK.getDefaultState(),matrices,vertexConsumers,light, OverlayTexture.DEFAULT_UV);
        matrices.translate(0,0,-1);
        this.blockRenderManager.renderBlockAsEntity(Blocks.DIAMOND_BLOCK.getDefaultState(),matrices,vertexConsumers,light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        }
}
