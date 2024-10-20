package com.ffsupver.asplor.block.electrolyzer;

import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class ElectrolyzerRenderer extends SafeBlockEntityRenderer<ElectrolyzerEntity> {
    public ElectrolyzerRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    protected void renderSafe(ElectrolyzerEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider bufferSource, int light, int overlay) {
        FluidStack fluidStack = be.getFluidStackForRender();
        float liquidHeight = (float) fluidStack.getAmount() /81000 *(12/16f);
        float bottomHeight = switch (be.getCachedState().get(Electrolyzer.PART)){
            case LOWER -> 0f;
            case MIDDLE -> 1f;
            case UPPER -> 2f;
        }/16;
        ms.push();
        ms.translate(0, 0, 0);
        FluidRenderer.renderFluidBox(fluidStack, 1 / 16f, 1 / 16f+bottomHeight, 1 / 16f, 15 / 16f, 1 / 16f+liquidHeight+bottomHeight, 15 / 16f, bufferSource, ms, light, false);
        ms.pop();
    }
}
