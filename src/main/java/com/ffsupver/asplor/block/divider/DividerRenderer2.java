package com.ffsupver.asplor.block.divider;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class DividerRenderer2 implements BlockEntityRenderer<DividerEntity>{
    public DividerRenderer2(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(DividerEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        System.out.println(entity.inputInv.getStackInSlot(0));
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack itemStack =entity.inputInv.getStackInSlot(0);
        matrices.push();
        matrices.translate(0,0,0);
        matrices.scale(1,1,1);
        itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND,light, OverlayTexture.DEFAULT_UV,matrices,vertexConsumers,entity.getWorld(),0);
        matrices.pop();
    }
}
