package com.ffsupver.asplor.entity.client;


import com.ffsupver.asplor.entity.custom.cargoRocket.CargoRocketEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class CargoRocketModel extends EntityModel<CargoRocketEntity> {
    private final ModelPart main;
    private final ModelPart main_body;
    private final ModelPart side3;
    private final ModelPart side1;
    private final ModelPart side2;
    private final ModelPart side4;
    private final ModelPart engine;
    private final ModelPart top;
    private final ModelPart fin;
    private final ModelPart booster1;
    private final ModelPart booster2;
    private final ModelPart booster3;
    private final ModelPart booster4;
    public CargoRocketModel(ModelPart root) {
        this.main = root.getChild("main");
        this.main_body = this.main.getChild("main_body"); // 确保从正确的父节点获取
        this.side3 = this.main_body.getChild("side3");    // 依次获取子节点
        this.side1 = this.main_body.getChild("side1");
        this.side2 = this.main_body.getChild("side2");
        this.side4 = this.main_body.getChild("side4");
        this.engine = this.main.getChild("engine");
        this.top = this.main.getChild("top");
        this.fin = this.main.getChild("fin");
        this.booster1 = this.fin.getChild("booster1");
        this.booster2 = this.fin.getChild("booster2");
        this.booster3 = this.fin.getChild("booster3");
        this.booster4 = this.fin.getChild("booster4");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData main_body = main.addChild("main_body", ModelPartBuilder.create().uv(0, 0).cuboid(-7.5F, -40.0F, -8.0F, 16.0F, 39.0F, 16.0F, new Dilation(0.0F))
                .uv(0, 55).cuboid(-7.0F, -38.0F, -7.5F, 15.0F, 36.0F, 15.0F, new Dilation(0.0F))
                .uv(64, 30).cuboid(-5.5F, -1.0F, -6.0F, 12.0F, 1.0F, 12.0F, new Dilation(0.0F))
                .uv(32, 106).cuboid(-8.0F, -38.75F, 6.5F, 2.0F, 37.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, -14.0F, 0.0F));

        ModelPartData p4_r1 = main_body.addChild("p4_r1", ModelPartBuilder.create().uv(32, 106).cuboid(-1.0F, -25.0F, -1.0F, 2.0F, 37.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -13.75F, -7.5F, 0.0F, 3.1416F, 0.0F));

        ModelPartData p3_r1 = main_body.addChild("p3_r1", ModelPartBuilder.create().uv(32, 106).cuboid(-1.0F, -25.0F, -1.0F, 2.0F, 37.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-7.0F, -13.75F, -7.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData p1_r1 = main_body.addChild("p1_r1", ModelPartBuilder.create().uv(32, 106).cuboid(-1.0F, -25.0F, -1.0F, 2.0F, 37.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -13.75F, 7.5F, 0.0F, 1.5708F, 0.0F));

        ModelPartData side3 = main_body.addChild("side3", ModelPartBuilder.create().uv(128, 26).cuboid(-7.0F, 11.0F, 8.0F, 14.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(128, 28).cuboid(-7.0F, -12.0F, 8.0F, 14.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 106).cuboid(-7.0F, -11.0F, 8.0F, 14.0F, 22.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, -14.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData side1 = main_body.addChild("side1", ModelPartBuilder.create().uv(128, 26).cuboid(-7.0F, 11.0F, 8.0F, 14.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(128, 28).cuboid(-7.0F, -12.0F, 8.0F, 14.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 106).cuboid(-7.0F, -11.0F, 8.0F, 14.0F, 22.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, -14.0F, 0.0F));

        ModelPartData side2 = main_body.addChild("side2", ModelPartBuilder.create().uv(128, 26).cuboid(-7.0F, 11.0F, 8.0F, 14.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(128, 28).cuboid(-7.0F, -12.0F, 8.0F, 14.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 106).cuboid(-7.0F, -11.0F, 8.0F, 14.0F, 22.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, -14.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData side4 = main_body.addChild("side4", ModelPartBuilder.create().uv(128, 26).cuboid(-7.0F, 11.0F, 8.0F, 14.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(128, 28).cuboid(-7.0F, -12.0F, 8.0F, 14.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 106).cuboid(-7.0F, -11.0F, 8.0F, 14.0F, 22.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, -14.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData engine = main.addChild("engine", ModelPartBuilder.create().uv(64, 43).cuboid(-5.0F, 0.0F, -5.0F, 10.0F, 2.0F, 10.0F, new Dilation(0.0F))
                .uv(128, 0).cuboid(-4.0F, -2.0F, -4.0F, 8.0F, 2.0F, 8.0F, new Dilation(0.0F))
                .uv(36, 145).cuboid(4.5F, 9.5F, 4.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(36, 145).cuboid(-6.5F, 9.5F, 4.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(36, 145).cuboid(-6.5F, 9.5F, -6.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(36, 145).cuboid(4.5F, 9.5F, -6.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(132, 109).cuboid(-4.5F, 10.5F, -6.5F, 9.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(132, 109).cuboid(-4.5F, 10.5F, 5.5F, 9.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(132, 66).cuboid(5.5F, 10.5F, -4.5F, 1.0F, 1.0F, 9.0F, new Dilation(0.0F))
                .uv(132, 66).cuboid(-6.5F, 10.5F, -4.5F, 1.0F, 1.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -12.0F, 0.0F));

        ModelPartData ep4_r1 = engine.addChild("ep4_r1", ModelPartBuilder.create().uv(144, 30).cuboid(-0.5F, -5.5F, -0.5F, 1.0F, 10.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.5F, 6.5F, 3.5F, 0.5236F, 0.0F, 0.5236F));

        ModelPartData ep3_r1 = engine.addChild("ep3_r1", ModelPartBuilder.create().uv(144, 30).cuboid(-0.5F, -5.5F, -0.5F, 1.0F, 10.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.5F, 6.5F, -3.5F, -0.4636F, 0.2527F, 0.4636F));

        ModelPartData ep2_r1 = engine.addChild("ep2_r1", ModelPartBuilder.create().uv(144, 30).cuboid(-0.5F, -5.5F, -0.5F, 1.0F, 10.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.5F, 6.5F, 3.5F, 0.4636F, 0.2527F, -0.4636F));

        ModelPartData ep1_r1 = engine.addChild("ep1_r1", ModelPartBuilder.create().uv(144, 30).cuboid(-0.5F, -5.5F, -0.5F, 1.0F, 10.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.5F, 6.5F, -3.5F, -0.4636F, -0.2527F, -0.4636F));

        ModelPartData e4_r1 = engine.addChild("e4_r1", ModelPartBuilder.create().uv(0, 130).cuboid(0.0F, -5.5F, -7.0F, 0.0F, 11.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 6.5F, -3.5F, 1.5708F, 2.0944F, 1.5708F));

        ModelPartData e3_r1 = engine.addChild("e3_r1", ModelPartBuilder.create().uv(0, 130).cuboid(0.0F, -5.5F, -7.0F, 0.0F, 11.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, 6.5F, 3.5F, 1.5708F, -1.0472F, -1.5708F));

        ModelPartData e2_r1 = engine.addChild("e2_r1", ModelPartBuilder.create().uv(0, 130).cuboid(0.0F, -5.5F, -5.0F, 0.0F, 11.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(3.5F, 6.5F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData e1_r1 = engine.addChild("e1_r1", ModelPartBuilder.create().uv(0, 130).cuboid(0.0F, -5.5F, -5.0F, 0.0F, 11.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-3.5F, 6.5F, 0.0F, -3.1416F, 0.0F, -2.618F));

        ModelPartData top = main.addChild("top", ModelPartBuilder.create().uv(132, 132).cuboid(4.0F, -8.5F, -1.0F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(60, 55).cuboid(-3.0F, 3.5F, -8.0F, 17.0F, 2.0F, 17.0F, new Dilation(0.0F))
                .uv(144, 132).cuboid(4.5F, -12.5F, -0.5F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F))
                .uv(32, 145).cuboid(5.0F, -21.5F, 0.0F, 1.0F, 9.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.5F, -58.25F, -0.5F));

        ModelPartData top4_r1 = top.addChild("top4_r1", ModelPartBuilder.create().uv(64, 0).cuboid(0.0F, -9.0F, -7.5F, 0.0F, 14.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, 0.0F, 6.0F, -1.5708F, 2.0944F, -1.5708F));

        ModelPartData top2_r1 = top.addChild("top2_r1", ModelPartBuilder.create().uv(64, 0).cuboid(0.0F, -9.0F, -7.5F, 0.0F, 14.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, 0.0F, -5.0F, -1.5708F, -1.0472F, 1.5708F));

        ModelPartData top1_r1 = top.addChild("top1_r1", ModelPartBuilder.create().uv(64, 0).cuboid(0.0F, -9.0F, -7.5F, 0.0F, 14.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

        ModelPartData p4_r2 = top.addChild("p4_r2", ModelPartBuilder.create().uv(56, 106).cuboid(-0.433F, -16.0F, -0.5F, 1.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.317F, 4.5179F, -7.25F, -0.473F, 0.0F, 0.5236F));

        ModelPartData p3_r2 = top.addChild("p3_r2", ModelPartBuilder.create().uv(56, 106).cuboid(-0.433F, -16.0F, -0.5F, 1.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(13.183F, 4.5179F, -7.25F, -0.473F, 0.0F, -0.5236F));

        ModelPartData p2_r1 = top.addChild("p2_r1", ModelPartBuilder.create().uv(56, 106).cuboid(-0.433F, -16.0F, -0.5F, 1.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(13.183F, 4.5179F, 8.25F, 0.473F, 0.0F, -0.5236F));

        ModelPartData p1_r2 = top.addChild("p1_r2", ModelPartBuilder.create().uv(56, 106).cuboid(-0.433F, -16.0F, -0.5F, 1.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.317F, 4.5179F, 8.25F, 0.473F, 0.0F, 0.5236F));

        ModelPartData top3_r1 = top.addChild("top3_r1", ModelPartBuilder.create().uv(64, 0).cuboid(0.0F, -9.0F, -7.5F, 0.0F, 14.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(11.0F, 0.0F, 1.0F, -3.1416F, 0.0F, 2.618F));

        ModelPartData fin = main.addChild("fin", ModelPartBuilder.create(), ModelTransform.pivot(-0.0152F, -8.6642F, 0.0F));

        ModelPartData booster1 = fin.addChild("booster1", ModelPartBuilder.create().uv(124, 113).cuboid(-16.0F, -19.0F, -16.0F, 6.0F, 13.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0152F, 7.6642F, 0.0F));

        ModelPartData bottomLL_r1 = booster1.addChild("bottomLL_r1", ModelPartBuilder.create().uv(132, 86).cuboid(-3.5F, 0.5F, -3.5F, 5.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, -2.75F, -12.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bottomL_r1 = booster1.addChild("bottomL_r1", ModelPartBuilder.create().uv(116, 132).cuboid(-3.5F, 0.5F, -3.5F, 4.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -4.75F, -11.5F, 0.0F, 0.0F, 0.0F));

        ModelPartData baseL_r1 = booster1.addChild("baseL_r1", ModelPartBuilder.create().uv(128, 18).cuboid(-5.5F, 0.5F, -5.5F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -6.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bottom_r1 = booster1.addChild("bottom_r1", ModelPartBuilder.create().uv(116, 143).cuboid(-3.5F, 0.5F, -3.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -5.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData topH_r1 = booster1.addChild("topH_r1", ModelPartBuilder.create().uv(56, 123).cuboid(-2.5F, 0.5F, -2.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -24.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData topBase_r1 = booster1.addChild("topBase_r1", ModelPartBuilder.create().uv(128, 10).cuboid(-5.5F, 1.5F, -5.5F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -20.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData topP4_r1 = booster1.addChild("topP4_r1", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -20.5F, -11.5F, -0.9163F, -2.3562F, 0.0F));

        ModelPartData topP3_r1 = booster1.addChild("topP3_r1", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, -20.5F, -14.5F, -0.9163F, 0.7854F, 0.0F));

        ModelPartData topP2_r1 = booster1.addChild("topP2_r1", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, -20.5F, -11.5F, -0.9163F, 2.3562F, 0.0F));

        ModelPartData topP1_r1 = booster1.addChild("topP1_r1", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -20.5F, -14.5F, -0.9163F, -0.7854F, 0.0F));

        ModelPartData top4_r2 = booster1.addChild("top4_r2", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-11.75F, -20.75F, -13.0F, 0.0F, 1.5708F, -0.7854F));

        ModelPartData top3_r2 = booster1.addChild("top3_r2", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-14.25F, -20.75F, -13.0F, 0.0F, -1.5708F, 0.7854F));

        ModelPartData top2_r2 = booster1.addChild("top2_r2", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-13.0F, -20.75F, -14.25F, 0.7854F, 3.1416F, 0.0F));

        ModelPartData top1_r2 = booster1.addChild("top1_r2", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-13.0F, -20.75F, -11.75F, 0.7854F, 0.0F, 0.0F));

        ModelPartData r1 = booster1.addChild("r1", ModelPartBuilder.create().uv(132, 95).cuboid(-3.0F, -6.5F, -0.5F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, -15.5F, -9.0F, -0.6155F, -0.5236F, 0.9553F));

        ModelPartData booster2 = fin.addChild("booster2", ModelPartBuilder.create().uv(124, 113).cuboid(-16.0F, -19.0F, -16.0F, 6.0F, 13.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0152F, 7.6642F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bottomLL_r2 = booster2.addChild("bottomLL_r2", ModelPartBuilder.create().uv(132, 86).cuboid(-3.5F, 0.5F, -3.5F, 5.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, -2.75F, -12.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bottomL_r2 = booster2.addChild("bottomL_r2", ModelPartBuilder.create().uv(116, 132).cuboid(-3.5F, 0.5F, -3.5F, 4.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -4.75F, -11.5F, 0.0F, 0.0F, 0.0F));

        ModelPartData baseL_r2 = booster2.addChild("baseL_r2", ModelPartBuilder.create().uv(128, 18).cuboid(-5.5F, 0.5F, -5.5F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -6.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bottom_r2 = booster2.addChild("bottom_r2", ModelPartBuilder.create().uv(116, 143).cuboid(-3.5F, 0.5F, -3.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -5.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData topH_r2 = booster2.addChild("topH_r2", ModelPartBuilder.create().uv(56, 123).cuboid(-2.5F, 0.5F, -2.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -24.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData topBase_r2 = booster2.addChild("topBase_r2", ModelPartBuilder.create().uv(128, 10).cuboid(-5.5F, 1.5F, -5.5F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -20.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData topP4_r2 = booster2.addChild("topP4_r2", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -20.5F, -11.5F, -0.9163F, -2.3562F, 0.0F));

        ModelPartData topP3_r2 = booster2.addChild("topP3_r2", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, -20.5F, -14.5F, -0.9163F, 0.7854F, 0.0F));

        ModelPartData topP2_r2 = booster2.addChild("topP2_r2", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, -20.5F, -11.5F, -0.9163F, 2.3562F, 0.0F));

        ModelPartData topP1_r2 = booster2.addChild("topP1_r2", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -20.5F, -14.5F, -0.9163F, -0.7854F, 0.0F));

        ModelPartData top4_r3 = booster2.addChild("top4_r3", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-11.75F, -20.75F, -13.0F, 0.0F, 1.5708F, -0.7854F));

        ModelPartData top3_r3 = booster2.addChild("top3_r3", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-14.25F, -20.75F, -13.0F, 0.0F, -1.5708F, 0.7854F));

        ModelPartData top2_r3 = booster2.addChild("top2_r3", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-13.0F, -20.75F, -14.25F, 0.7854F, 3.1416F, 0.0F));

        ModelPartData top1_r3 = booster2.addChild("top1_r3", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-13.0F, -20.75F, -11.75F, 0.7854F, 0.0F, 0.0F));

        ModelPartData r2 = booster2.addChild("r2", ModelPartBuilder.create().uv(132, 95).cuboid(-3.0F, -6.5F, -0.5F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, -15.5F, -9.0F, -0.6155F, -0.5236F, 0.9553F));

        ModelPartData booster3 = fin.addChild("booster3", ModelPartBuilder.create().uv(124, 113).cuboid(-16.0F, -19.0F, -16.0F, 6.0F, 13.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0152F, 7.6642F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData bottomLL_r3 = booster3.addChild("bottomLL_r3", ModelPartBuilder.create().uv(132, 86).cuboid(-3.5F, 0.5F, -3.5F, 5.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, -2.75F, -12.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bottomL_r3 = booster3.addChild("bottomL_r3", ModelPartBuilder.create().uv(116, 132).cuboid(-3.5F, 0.5F, -3.5F, 4.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -4.75F, -11.5F, 0.0F, 0.0F, 0.0F));

        ModelPartData baseL_r3 = booster3.addChild("baseL_r3", ModelPartBuilder.create().uv(128, 18).cuboid(-5.5F, 0.5F, -5.5F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -6.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bottom_r3 = booster3.addChild("bottom_r3", ModelPartBuilder.create().uv(116, 143).cuboid(-3.5F, 0.5F, -3.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -5.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData topH_r3 = booster3.addChild("topH_r3", ModelPartBuilder.create().uv(56, 123).cuboid(-2.5F, 0.5F, -2.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -24.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData topBase_r3 = booster3.addChild("topBase_r3", ModelPartBuilder.create().uv(128, 10).cuboid(-5.5F, 1.5F, -5.5F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -20.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData topP4_r3 = booster3.addChild("topP4_r3", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -20.5F, -11.5F, -0.9163F, -2.3562F, 0.0F));

        ModelPartData topP3_r3 = booster3.addChild("topP3_r3", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, -20.5F, -14.5F, -0.9163F, 0.7854F, 0.0F));

        ModelPartData topP2_r3 = booster3.addChild("topP2_r3", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, -20.5F, -11.5F, -0.9163F, 2.3562F, 0.0F));

        ModelPartData topP1_r3 = booster3.addChild("topP1_r3", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -20.5F, -14.5F, -0.9163F, -0.7854F, 0.0F));

        ModelPartData top4_r4 = booster3.addChild("top4_r4", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-11.75F, -20.75F, -13.0F, 0.0F, 1.5708F, -0.7854F));

        ModelPartData top3_r4 = booster3.addChild("top3_r4", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-14.25F, -20.75F, -13.0F, 0.0F, -1.5708F, 0.7854F));

        ModelPartData top2_r4 = booster3.addChild("top2_r4", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-13.0F, -20.75F, -14.25F, 0.7854F, 3.1416F, 0.0F));

        ModelPartData top1_r4 = booster3.addChild("top1_r4", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-13.0F, -20.75F, -11.75F, 0.7854F, 0.0F, 0.0F));

        ModelPartData r3 = booster3.addChild("r3", ModelPartBuilder.create().uv(132, 95).cuboid(-3.0F, -6.5F, -0.5F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, -15.5F, -9.0F, -0.6155F, -0.5236F, 0.9553F));

        ModelPartData booster4 = fin.addChild("booster4", ModelPartBuilder.create().uv(124, 113).cuboid(-16.0F, -19.0F, -16.0F, 6.0F, 13.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0152F, 7.6642F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bottomLL_r4 = booster4.addChild("bottomLL_r4", ModelPartBuilder.create().uv(132, 86).cuboid(-3.5F, 0.5F, -3.5F, 5.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-12.0F, -2.75F, -12.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bottomL_r4 = booster4.addChild("bottomL_r4", ModelPartBuilder.create().uv(116, 132).cuboid(-3.5F, 0.5F, -3.5F, 4.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -4.75F, -11.5F, 0.0F, 0.0F, 0.0F));

        ModelPartData baseL_r4 = booster4.addChild("baseL_r4", ModelPartBuilder.create().uv(128, 18).cuboid(-5.5F, 0.5F, -5.5F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -6.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bottom_r4 = booster4.addChild("bottom_r4", ModelPartBuilder.create().uv(116, 143).cuboid(-3.5F, 0.5F, -3.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -5.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData topH_r4 = booster4.addChild("topH_r4", ModelPartBuilder.create().uv(56, 123).cuboid(-2.5F, 0.5F, -2.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -24.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData topBase_r4 = booster4.addChild("topBase_r4", ModelPartBuilder.create().uv(128, 10).cuboid(-5.5F, 1.5F, -5.5F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-11.0F, -20.75F, -11.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData topP4_r4 = booster4.addChild("topP4_r4", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -20.5F, -11.5F, -0.9163F, -2.3562F, 0.0F));

        ModelPartData topP3_r4 = booster4.addChild("topP3_r4", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, -20.5F, -14.5F, -0.9163F, 0.7854F, 0.0F));

        ModelPartData topP2_r4 = booster4.addChild("topP2_r4", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, -20.5F, -11.5F, -0.9163F, 2.3562F, 0.0F));

        ModelPartData topP1_r4 = booster4.addChild("topP1_r4", ModelPartBuilder.create().uv(146, 101).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-11.5F, -20.5F, -14.5F, -0.9163F, -0.7854F, 0.0F));

        ModelPartData top4_r5 = booster4.addChild("top4_r5", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-11.75F, -20.75F, -13.0F, 0.0F, 1.5708F, -0.7854F));

        ModelPartData top3_r5 = booster4.addChild("top3_r5", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-14.25F, -20.75F, -13.0F, 0.0F, -1.5708F, 0.7854F));

        ModelPartData top2_r5 = booster4.addChild("top2_r5", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-13.0F, -20.75F, -14.25F, 0.7854F, 3.1416F, 0.0F));

        ModelPartData top1_r5 = booster4.addChild("top1_r5", ModelPartBuilder.create().uv(116, 138).cuboid(-3.0F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-13.0F, -20.75F, -11.75F, 0.7854F, 0.0F, 0.0F));

        ModelPartData r4 = booster4.addChild("r4", ModelPartBuilder.create().uv(132, 95).cuboid(-3.0F, -6.5F, -0.5F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, -15.5F, -9.0F, -0.6155F, -0.5236F, 0.9553F));
        return TexturedModelData.of(modelData, 256, 256);
    }
    @Override
    public void setAngles(CargoRocketEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}