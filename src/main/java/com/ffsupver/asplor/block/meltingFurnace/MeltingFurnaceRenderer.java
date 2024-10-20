package com.ffsupver.asplor.block.meltingFurnace;

import com.ffsupver.asplor.util.ColoredVertexConsumerProvider;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.util.math.RotationAxis;

public class MeltingFurnaceRenderer extends SafeBlockEntityRenderer<MeltingFurnaceEntity> {
    public MeltingFurnaceRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    protected void renderSafe(MeltingFurnaceEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider bufferSource, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack itemStack = be.getItemForRender();
        float processPercentage = 1 - (float) Math.max(be.getProcess(),0) /be.getProcessTime();
        if (itemStack.getCount() <= 1) {
            renderItem(be, itemRenderer, ms, bufferSource, light, itemStack,0f,0,0,processPercentage);
        }else {
            int itemCount = Math.min(itemStack.getCount(),10);
            for (int i=0;i<itemCount;i++){
                renderItem(be, itemRenderer, ms, bufferSource, light, itemStack, (float) 360 /itemCount*(i+1),10.0f,0.4f,i==0?processPercentage:0);
            }
        }
    }
    private void renderItem(MeltingFurnaceEntity be, ItemRenderer itemRenderer, MatrixStack ms, VertexConsumerProvider bufferSource, int light, ItemStack itemStack,float rotationDegree,float rotationX,float offsetZ,float processPercentage){
        ms.push();
        ms.translate(0.5f,0.2f,0.5f);
        float scale = itemStack.getItem() instanceof SkullItem? 1.0f : 0.5f;
        ms.scale(scale,scale,scale);
        rotationCenter(ms,rotationDegree,offsetZ);
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f+rotationX));

        float[] finalColor =new float[]{1.0f,0.2f,0.0f};
        float R = (1 - processPercentage) + finalColor[0] * processPercentage;
        float G = (1 - processPercentage) + finalColor[1] * processPercentage;
        float B = (1 - processPercentage) + finalColor[2] * processPercentage;

        ColoredVertexConsumerProvider coloredProvider = new ColoredVertexConsumerProvider(bufferSource, R, G, B, 1.0f);

        itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, getBlockLight(be), OverlayTexture.DEFAULT_UV,ms,coloredProvider,be.getWorld(),0);



        ms.pop();
    }
    private void rotationCenter(MatrixStack ms,float rotationDegree,float offsetZ){
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationDegree));
        ms.translate(0,.0f,-offsetZ);

    }

    private int getBlockLight(MeltingFurnaceEntity be){
        int light = be.getWorld().getLightLevel(be.getPos().up());
       return light*16;
    }
}
