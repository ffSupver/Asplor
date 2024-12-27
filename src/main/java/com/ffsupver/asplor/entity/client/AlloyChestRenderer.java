package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.entity.custom.AlloyChestEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AlloyChestRenderer extends EntityRenderer<AlloyChestEntity> {

    private BlockRenderManager blockRenderManager ;
    protected AlloyChestRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
       this.blockRenderManager =ctx.getBlockRenderManager();
    }

    @Override
    public Identifier getTexture(AlloyChestEntity entity) {
        return null;
    }

    @Override
    public void render(AlloyChestEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        float scale = entity.getType().getWidth();
        matrices.scale(scale,scale,scale);
        float offset = 0.5f;
        matrices.translate(-offset,0,-offset);

        this.blockRenderManager.renderBlockAsEntity(AllBlocks.ALLOY_CHEST.getDefaultState(),matrices,vertexConsumers,light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        }
}
