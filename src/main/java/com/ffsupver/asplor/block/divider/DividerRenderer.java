package com.ffsupver.asplor.block.divider;

import com.jozufozu.flywheel.backend.Backend;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.KineticDebugger;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.render.SuperByteBufferCache;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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
        System.out.println(1+"1111");
        ms.push();
        ms.translate(0,0,0);
        itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED,light, OverlayTexture.DEFAULT_UV,ms,buffer,be.getWorld(),0);
        ms.pop();
    }
//    @Override
//    protected SuperByteBuffer getRotatedModel(DividerEntity be, BlockState state) {
//
//        return CachedBufferer.partial(AllPartialModels.MILLSTONE_COG,state);
//    }



}
