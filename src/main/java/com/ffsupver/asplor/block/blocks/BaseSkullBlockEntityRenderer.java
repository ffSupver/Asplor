package com.ffsupver.asplor.block.blocks;

import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class BaseSkullBlockEntityRenderer extends SkullBlockEntityRenderer {
    public BaseSkullBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        super.render(skullBlockEntity, f, matrixStack, vertexConsumerProvider, i, j);
    }
}
