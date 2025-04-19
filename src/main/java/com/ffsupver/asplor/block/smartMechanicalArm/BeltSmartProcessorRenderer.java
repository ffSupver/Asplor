package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.AllPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

import static com.ffsupver.asplor.util.RenderUtil.renderModel;

public class BeltSmartProcessorRenderer extends SmartBlockEntityRenderer<BeltSmartProcessorEntity> implements IWrenchable {
    public BeltSmartProcessorRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BeltSmartProcessorEntity blockEntity, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        int lightAbove = getLight(blockEntity);
        int process = blockEntity.getProcessTick();
        int maxProcess = blockEntity.getMaxProcessTick();
        boolean work = process != maxProcess;
        PartialModel toolModel = blockEntity.getToolModel(work);

        ms.translate(0,-16/16f,0);

        ms.translate(-4/16f * Math.sin(4 * Math.PI * process / maxProcess),0,-4/16f * Math.sin(6 * Math.PI * process / maxProcess));
        renderModel(blockEntity, ms, buffer, AllPartialModels.BELT_SMART_PROCESSOR_ARM, lightAbove, true);
        ms.translate(0,-4/16f,0);

        if (toolModel != null){
            renderModel(blockEntity, ms, buffer, toolModel, lightAbove, true);
        }
    }

    public int getLight(BeltSmartProcessorEntity be){
        BlockPos pos = be.getPos().down();
        return be.getWorld().getLightLevel(LightType.BLOCK,pos);
    }
}
