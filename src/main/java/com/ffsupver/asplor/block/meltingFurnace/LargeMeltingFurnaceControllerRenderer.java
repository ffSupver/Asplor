package com.ffsupver.asplor.block.meltingFurnace;

import com.ffsupver.asplor.util.ColoredVertexConsumerProvider;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;

public class LargeMeltingFurnaceControllerRenderer extends SafeBlockEntityRenderer<LargeMeltingFurnaceControllerEntity> {
    public LargeMeltingFurnaceControllerRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    protected void renderSafe(LargeMeltingFurnaceControllerEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider bufferSource, int light, int overlay) {
        if (be.data != null){
            BlockPos pos = be.getPos();
            Box fluidBox = be.getFluidBox();
            Box fluidRenderBox = fluidBox.offset(-pos.getX(),-pos.getY(),-pos.getZ());
            if (fluidBox.maxY > fluidBox.minY){
                FluidRenderer.renderFluidBox(
                        be.getRenderFluid(),
                        (float) fluidRenderBox.minX,(float)fluidRenderBox.minY,(float)fluidRenderBox.minZ,
                        (float)fluidRenderBox.maxX,(float) fluidRenderBox.maxY,(float) fluidRenderBox.maxZ,
                        bufferSource,ms,light,false);
            }

            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            int itemCount = 0;
            int width = (int) (fluidBox.maxX - fluidBox.minX) * 2;
            float offsetXBase = (float) (fluidBox.minX - be.getPos().getX()) + 4/16f;
            float offsetZBase = (float) (fluidBox.minZ - be.getPos().getZ()) + 4/16f;
            float offsetYBase = (float) (Math.max(fluidBox.minX + 2/16f,fluidBox.maxY) - be.getPos().getY());
            for (Pair<ItemStack,Float> pair : be.getRenderItem()){
                float offsetX = offsetXBase+(itemCount % width)*0.5f;
                float offsetZ = offsetZBase+(itemCount / width)*0.5f;

                renderItem(be,itemRenderer,ms,bufferSource,light,pair.getLeft(),offsetX,offsetYBase,offsetZ,20,pair.getRight());
                itemCount += 1;
            }
        }
    }

    private void renderItem(LargeMeltingFurnaceControllerEntity be, ItemRenderer itemRenderer, MatrixStack ms, VertexConsumerProvider bufferSource, int light, ItemStack itemStack, float x, float y, float z,float rotationX, float processPercentage){
        ms.push();
        ms.translate(x,y,z);
        float scale = itemStack.getItem() instanceof SkullItem ? 0.7f : 0.35f;
        ms.scale(scale,scale,scale);
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f+rotationX));

        float[] finalColor =new float[]{1.0f,0.2f,0.0f};
        float R = (1 - processPercentage) + finalColor[0] * processPercentage;
        float G = (1 - processPercentage) + finalColor[1] * processPercentage;
        float B = (1 - processPercentage) + finalColor[2] * processPercentage;

        ColoredVertexConsumerProvider coloredProvider = new ColoredVertexConsumerProvider(bufferSource, R, G, B, 1.0f);

        itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV,ms,coloredProvider,be.getWorld(),0);
        ms.pop();
    }
}
