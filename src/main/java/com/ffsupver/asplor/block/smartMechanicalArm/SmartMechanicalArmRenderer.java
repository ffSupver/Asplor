package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.AllPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

import static com.ffsupver.asplor.util.RenderUtil.renderModel;

public class SmartMechanicalArmRenderer extends SafeBlockEntityRenderer<SmartMechanicalArmEntity> {
    public SmartMechanicalArmRenderer(BlockEntityRendererFactory.Context context) {
        super();
    }

    @Override
    protected void renderSafe(SmartMechanicalArmEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        SmartMechanicalArmEntity.ArmData armData =be.getArmData();
        Vec3d headPos = armData.headPos;
        boolean isWorking = be.getProcess() < be.PROCESS_TIME;
        int lightAbove = getLight(be);


        ms.push();
        ms.translate(.5f,0,.5f);
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(armData.baseRotationDegree));
        ms.translate(-.5f,0,-.5f);
        ms.translate(0,1,0);
        renderModel(be,ms,buffer,AllPartialModels.SMART_MECHANICAL_ARM_BASE,lightAbove,true);
        ms.translate(0,-1,0);
        //整体高度
        ms.translate(0,4/16f,0);
        //移动到铰链
        ms.translate(0,1.2,-1);
        //旋转第一动力臂
        ms.translate(0,0.2,1.5);
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(armData.firstRotationDegree));
        ms.translate(0,-0.2,-1.5);
        renderModel(be,ms,buffer,AllPartialModels.SMART_MECHANICAL_ARM_FIRST_ARM,lightAbove,true);

        //移动到第一动力臂末端
        ms.translate(0,0,-2);
        //旋转第二条动力臂
        ms.translate(0,1.5/16f,23.5/16f);
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(armData.secondRotationDegree));
        ms.translate(0,-1.5/16f,-23.5/16f);

        ms.translate(0,0,-1/16f);
        renderModel(be,ms,buffer,AllPartialModels.SMART_MECHANICAL_ARM_SECOND_ARM,lightAbove,true);
        ms.translate(0,0,1/16f);

        ms.translate(0,1.5/16f,23.5/16f);
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-armData.secondRotationDegree));
        ms.translate(0,-1.5/16f,-23.5/16f);
        //第二条动力臂旋转归位
        ms.translate(0,0,2);
        //位置回归铰链

        ms.translate(0,0.2,1.5);
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-armData.firstRotationDegree));
        ms.translate(0,-0.2,-1.5);
        //第一动力臂旋转复位
        ms.translate(0,-1.2,1);
        //位置归零

        ms.translate(0,0.0 + headPos.y,-headPos.length()-4/16f+0.5f);
        renderModel(be,ms,buffer,AllPartialModels.SMART_MECHANICAL_ARM_HEAD,lightAbove,true);
        PartialModel toolModel = be.getToolModel(isWorking);
        ms.translate(0,-6/16f,0);
        if (toolModel != null){
            renderModel(be, ms, buffer, toolModel,lightAbove,true);
        }

        ms.pop();
    }

    private int getLight(SmartMechanicalArmEntity be){
        BlockPos above = be.getPos().up();
       return be.getWorld().getLightLevel(LightType.BLOCK,above);
    }

}
