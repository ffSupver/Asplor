package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.entity.custom.GlacioVillagerShaman;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

public class GlacioVillagerShamanModel<T extends GlacioVillagerShaman> extends SinglePartEntityModel<T> implements ModelWithHead, ModelWithArms {
    private final ModelPart main;
    private final ModelPart right_leg;
    private final ModelPart body;
    private final ModelPart left_leg;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart head;
    public GlacioVillagerShamanModel(ModelPart root) {
        this.main = root.getChild("main");
        this.right_leg = main.getChild("right_leg");
        this.body = main.getChild("body");
        this.left_leg = main.getChild("left_leg");
        this.right_arm = main.getChild("right_arm");
        this.left_arm = main.getChild("left_arm");
        this.head = main.getChild("head");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData right_leg = main.addChild("right_leg", ModelPartBuilder.create().uv(32, 0).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -12.0F, 0.0F));

        ModelPartData body = main.addChild("body", ModelPartBuilder.create().uv(1, 16).cuboid(-4.0F, -11.0F, -2.0F, 8.0F, 13.0F, 4.0F, new Dilation(0.0F))
                .uv(21, 18).cuboid(-4.0F, -9.0F, -3.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(21, 18).cuboid(-4.0F, -6.0F, -3.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(21, 18).cuboid(-4.0F, -3.0F, -3.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(21, 16).cuboid(1.0F, -9.0F, -3.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(21, 16).cuboid(1.0F, -6.0F, -3.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(21, 16).cuboid(1.0F, -3.0F, -3.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 2).cuboid(1.0F, -7.0F, 2.0F, 2.0F, 5.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 2).cuboid(-3.0F, -7.0F, 2.0F, 2.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -15.0F, 0.0F));

        ModelPartData left_leg = main.addChild("left_leg", ModelPartBuilder.create().uv(32, 0).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -12.0F, 0.0F));

        ModelPartData right_arm = main.addChild("right_arm", ModelPartBuilder.create().uv(28, 17).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.0F))
                .uv(40, 19).cuboid(1.0F, -3.0F, 1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 19).cuboid(1.0F, -3.0F, -2.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, -24.0F, 0.0F));

        ModelPartData left_arm = main.addChild("left_arm", ModelPartBuilder.create().uv(28, 17).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.0F))
                .uv(40, 19).cuboid(-2.0F, -3.0F, 1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 19).cuboid(-2.0F, -3.0F, -2.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, -24.0F, 0.0F));

        ModelPartData head = main.addChild("head", ModelPartBuilder.create().uv(2, 2).cuboid(-4.0F, -7.0F, -3.0F, 8.0F, 7.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 34).cuboid(-3.0F, -7.0F, -4.0F, 6.0F, 6.0F, 1.0F, new Dilation(0.0F))
                .uv(10, 34).cuboid(-3.0F, -8.0F, -3.0F, 6.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(24, 34).cuboid(2.0F, -9.0F, 0.0F, 1.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(24, 34).cuboid(-3.0F, -9.0F, 0.0F, 1.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -26.0F, 0.0F));

        ModelPartData hat_r_r1 = head.addChild("hat_r_r1", ModelPartBuilder.create().uv(44, 17).cuboid(-0.5F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.75F, -4.75F, -4.0F, 0.0F, 0.0F, 0.0873F));

        ModelPartData hat_l_r1 = head.addChild("hat_l_r1", ModelPartBuilder.create().uv(44, 17).cuboid(-0.5F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.75F, -4.75F, -4.0F, 0.0076F, -0.0869F, -0.0876F));

        ModelPartData hat_u_l_r1 = head.addChild("hat_u_l_r1", ModelPartBuilder.create().uv(34, 34).cuboid(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(2.5F, -7.75F, -2.75F, 0.7873F, 0.0617F, 0.0618F));

        ModelPartData hat_u_r_r1 = head.addChild("hat_u_r_r1", ModelPartBuilder.create().uv(34, 34).cuboid(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, -7.75F, -2.75F, 0.7873F, -0.0617F, -0.0618F));

        ModelPartData hat_u_r1 = head.addChild("hat_u_r1", ModelPartBuilder.create().uv(14, 38).cuboid(-2.0F, -1.0F, -0.5F, 4.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -7.0F, -3.5F, -0.7854F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles(GlacioVillagerShaman entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw,headPitch);

        this.animateMovement(GlacioVillagerShamanAnimation.WALK,limbSwing,limbSwingAmount,2f,2.5f);
        this.updateAnimation(entity.idleAnimationState,GlacioVillagerShamanAnimation.IDLE,ageInTicks,1f);
        this.updateAnimation(entity.attackAnimationState,GlacioVillagerShamanAnimation.ATTACK,ageInTicks,1f);
        this.updateAnimation(entity.meteoriteAttackAnimationState,GlacioVillagerShamanAnimation.CASTING_SPELLS,ageInTicks,1f);
    }
    private void setHeadAngles(float headYaw, float headPitch) {
        headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
        headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

        this.head.yaw = headYaw * (float) (Math.PI / 180.0);
        this.head.pitch = headPitch * (float) (Math.PI / 180.0);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
    @Override
    public ModelPart getPart() {
        return this.main;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        if (arm.equals(Arm.LEFT)) {
            this.left_arm.rotate(matrices);
        }else if (arm.equals(Arm.RIGHT)){
            this.right_arm.rotate(matrices);
        }

    }
}
