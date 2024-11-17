package com.ffsupver.asplor.block.divider;

import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.SuperByteBufferCache;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class DividerRenderer extends SafeBlockEntityRenderer<DividerEntity> {

    public DividerRenderer(BlockEntityRendererFactory.Context ctx){
        super();

    }
    public static final SuperByteBufferCache.Compartment<BlockState> DIVIDER = new SuperByteBufferCache.Compartment<>();


    @Override
    protected void renderSafe(DividerEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
        renderItem(be,ms,buffer,light,overlay);
//        if (Backend.canUseInstancing(be.getWorld())) return;

    }

    private static void renderItem(DividerEntity be, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay){
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack itemStack = be.inputInv.getStackInSlot(0);
        float scale = 0.5f;
        ms.push();
        ms.translate(0.5,0.5,0.5);
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        ms.scale(scale,scale,scale);
        itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED,light, OverlayTexture.DEFAULT_UV,ms,buffer,be.getWorld(),0);
        ms.pop();
    }
//    @Override
//    protected SuperByteBuffer getRotatedModel(DividerEntity be, BlockState state) {
//
//        return CachedBufferer.partial(AllPartialModels.MILLSTONE_COG,state);
//    }



}
