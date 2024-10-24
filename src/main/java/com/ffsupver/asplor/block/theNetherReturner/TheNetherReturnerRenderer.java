package com.ffsupver.asplor.block.theNetherReturner;

import com.ffsupver.asplor.item.ModItems;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class TheNetherReturnerRenderer extends SafeBlockEntityRenderer<TheNetherReturnerEntity> {
    public TheNetherReturnerRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    protected void renderSafe(TheNetherReturnerEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider bufferSource, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        ms.push();
        ms.translate(.5f, .5f, .5f);
        ms.translate(0f, 1.0f, 0f);
        float scale = 1.5f;
        ms.scale(scale,scale,scale);
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(be.getRotation()));
        if (be.getActive()) {
            itemRenderer.renderItem(ModItems.LOCATOR.getDefaultStack(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, ms, bufferSource, be.getWorld(), 0);
        }
        ms.pop();
    }
}
