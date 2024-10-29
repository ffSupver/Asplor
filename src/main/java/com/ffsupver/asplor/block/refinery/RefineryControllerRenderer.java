package com.ffsupver.asplor.block.refinery;

import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class RefineryControllerRenderer extends SmartBlockEntityRenderer<RefineryControllerEntity> {
    public RefineryControllerRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(RefineryControllerEntity blockEntity, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        FluidTank fluidTank = blockEntity.getFluidTank();
        FluidStack fluidStack = fluidTank.getFluid();

        Vec3d minPosA = blockEntity.getLiquidBox().get(0);
        Vec3d maxPosA = blockEntity.getLiquidBox().get(1);
        double liquidHeight = blockEntity.getLiquidHeight();

        Vec3d posA = blockEntity.getPos().toCenterPos().add(-.5,-.5,-.5);
        Vec3d minPos = new Vec3d(minPosA.x-posA.x,minPosA.y-posA.y,minPosA.z-posA.z);
        Vec3d maxPos = new Vec3d(maxPosA.x-posA.x,minPosA.y- posA.y + liquidHeight,maxPosA.z-posA.z);
        ms.push();
        FluidRenderer.renderFluidBox(fluidStack,(float) minPos.x,(float)minPos.y,(float)minPos.z,(float) maxPos.x,(float) maxPos.y,(float) maxPos.z,buffer,ms,light,true);
        ms.pop();
    }
}
