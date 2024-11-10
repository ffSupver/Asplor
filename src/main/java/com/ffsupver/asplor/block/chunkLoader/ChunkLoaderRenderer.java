package com.ffsupver.asplor.block.chunkLoader;

import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

import static com.ffsupver.asplor.util.RenderUtil.getBlockLight;
import static com.ffsupver.asplor.util.RenderUtil.getTextureLight;

public class ChunkLoaderRenderer extends SafeBlockEntityRenderer<ChunkLoaderEntity> {
    public ChunkLoaderRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    protected void renderSafe(ChunkLoaderEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider bufferSource, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack itemStack = new ItemStack(Items.CLOCK,1);
        int tickRemain = be.getTicksRemain();
        int maxTick = ChunkLoaderEntity.MAX_TICK;

        float rotationX = (1 - (float) tickRemain /maxTick )* 90;
        float rotationY =  tickRemain % 360;

        float scale = 0.5f;

        int lightReal = getTextureLight(getBlockLight(be.getWorld(),be.getPos()));

        ms.push();
        ms.translate(.5f,0.5f + 5/16f,.5f);
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationY));
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX));
        ms.scale(scale,scale,scale);
        itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, lightReal,overlay,ms,bufferSource,be.getWorld(),0);
        ms.pop();
    }
}
