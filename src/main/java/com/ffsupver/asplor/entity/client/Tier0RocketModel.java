// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package com.ffsupver.asplor.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class Tier0RocketModel extends EntityModel<Entity> {
	private final ModelPart main;
	private final ModelPart main_body;
	private final ModelPart engine;
	private final ModelPart top;
	private final ModelPart fin;

	public ModelPart getMain() {
		return main;
	}

	public Tier0RocketModel(ModelPart root) {
		this.main = root.getChild("main");
		this.main_body = root.getChild("main_body");
		this.engine = root.getChild("engine");
		this.top = root.getChild("top");
		this.fin = root.getChild("fin");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData main_body = main.addChild("main_body", ModelPartBuilder.create().uv(0, 0).cuboid(-7.5F, -40.0F, -8.0F, 16.0F, 40.0F, 16.0F, new Dilation(0.0F))
				.uv(-16, 105).cuboid(-7.5F, -10.0F, -8.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, -4.0F, 0.0F));

		ModelPartData engine = main.addChild("engine", ModelPartBuilder.create().uv(64, 60).cuboid(-5.0F, 0.0F, -5.0F, 10.0F, 2.0F, 10.0F, new Dilation(0.0F))
				.uv(64, 72).cuboid(-4.0F, -2.0F, -4.0F, 8.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -2.0F, 0.0F));

		ModelPartData top = main.addChild("top", ModelPartBuilder.create().uv(96, 72).cuboid(4.0F, -8.5F, -1.0F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F))
				.uv(96, 77).cuboid(4.5F, -14.5F, -0.5F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.5F, -48.25F, -0.5F));

		ModelPartData top4_r1 = top.addChild("top4_r1", ModelPartBuilder.create().uv(64, 30).cuboid(0.0F, -9.0F, -7.5F, 0.0F, 14.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -3.5F, 4.0F, -1.5708F, -1.0472F, -1.5708F));

		ModelPartData top2_r1 = top.addChild("top2_r1", ModelPartBuilder.create().uv(64, 0).cuboid(0.0F, -9.0F, -7.5F, 0.0F, 14.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, 0.0F, -5.0F, -1.5708F, 1.0472F, -1.5708F));

		ModelPartData top1_r1 = top.addChild("top1_r1", ModelPartBuilder.create().uv(0, 56).cuboid(0.0F, -9.0F, -7.5F, 0.0F, 14.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

		ModelPartData p4_r1 = top.addChild("p4_r1", ModelPartBuilder.create().uv(96, 39).cuboid(-0.433F, -16.0F, -0.5F, 1.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.317F, 4.5179F, -7.25F, -0.473F, 0.0F, 0.5236F));

		ModelPartData p3_r1 = top.addChild("p3_r1", ModelPartBuilder.create().uv(96, 22).cuboid(-0.433F, -16.0F, -0.5F, 1.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(13.183F, 4.5179F, -7.25F, -0.473F, 0.0F, -0.5236F));

		ModelPartData p2_r1 = top.addChild("p2_r1", ModelPartBuilder.create().uv(60, 86).cuboid(-0.433F, -16.0F, -0.5F, 1.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(13.183F, 4.5179F, 8.25F, 0.473F, 0.0F, -0.5236F));

		ModelPartData p1_r1 = top.addChild("p1_r1", ModelPartBuilder.create().uv(56, 86).cuboid(-0.433F, -16.0F, -0.5F, 1.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.317F, 4.5179F, 8.25F, 0.473F, 0.0F, 0.5236F));

		ModelPartData top3_r1 = top.addChild("top3_r1", ModelPartBuilder.create().uv(32, 56).cuboid(0.0F, -9.0F, -7.5F, 0.0F, 14.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(11.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

		ModelPartData fin = main.addChild("fin", ModelPartBuilder.create().uv(64, 82).cuboid(-2.0F, -14.0F, -2.0F, 4.0F, 15.0F, 4.0F, new Dilation(0.0F))
				.uv(0, 86).cuboid(-15.0F, -14.0F, -15.0F, 4.0F, 15.0F, 4.0F, new Dilation(0.0F))
				.uv(16, 86).cuboid(11.0F, -14.0F, -15.0F, 4.0F, 15.0F, 4.0F, new Dilation(0.0F))
				.uv(80, 82).cuboid(-2.0F, -14.0F, -28.0F, 4.0F, 15.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 13.0F));

		ModelPartData connect1_r1 = fin.addChild("connect1_r1", ModelPartBuilder.create().uv(32, 86).cuboid(0.0F, -9.0F, -2.5F, 1.0F, 10.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -6.0F, -1.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData connect4_r1 = fin.addChild("connect4_r1", ModelPartBuilder.create().uv(96, 0).cuboid(-4.0F, -9.0F, -2.0F, 5.0F, 10.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -5.0F, -11.5F, 0.0F, 0.0F, 0.7854F));

		ModelPartData connect3_r1 = fin.addChild("connect3_r1", ModelPartBuilder.create().uv(96, 11).cuboid(-4.0F, -9.0F, -2.0F, 5.0F, 10.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(13.0F, -7.0F, -11.5F, 0.0F, 0.0F, -0.7854F));

		ModelPartData connect2_r1 = fin.addChild("connect2_r1", ModelPartBuilder.create().uv(44, 86).cuboid(0.0F, -9.0F, -2.5F, 1.0F, 10.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -6.0F, -25.0F, -0.7854F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}