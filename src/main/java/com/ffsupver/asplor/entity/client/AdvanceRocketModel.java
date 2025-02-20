package com.ffsupver.asplor.entity.client;


import com.ffsupver.asplor.entity.custom.rocket.AdvanceRocketEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class AdvanceRocketModel extends EntityModel<AdvanceRocketEntity> {
    private final ModelPart main;
    private final ModelPart engine;
    private final ModelPart down;
    private final ModelPart edown1;
    private final ModelPart edown2;
    private final ModelPart edown3;
    private final ModelPart edown4;
    private final ModelPart edown5;
    private final ModelPart edown6;
    private final ModelPart edown7;
    private final ModelPart edown8;
    private final ModelPart up;
    private final ModelPart eup1;
    private final ModelPart eup2;
    private final ModelPart eup3;
    private final ModelPart eup4;
    private final ModelPart eup5;
    private final ModelPart eup6;
    private final ModelPart eup7;
    private final ModelPart eup8;
    private final ModelPart body;
    private final ModelPart bl;
    private final ModelPart ml;
    private final ModelPart fin;
    private final ModelPart f1;
    private final ModelPart boostdown1;
    private final ModelPart boostdown2;
    private final ModelPart boostdown3;
    private final ModelPart boostdown4;
    private final ModelPart f2;
    private final ModelPart boostdown5;
    private final ModelPart boostdown6;
    private final ModelPart boostdown7;
    private final ModelPart boostdown8;
    private final ModelPart f3;
    private final ModelPart boostdown9;
    private final ModelPart boostdown10;
    private final ModelPart boostdown11;
    private final ModelPart boostdown12;
    private final ModelPart f4;
    private final ModelPart boostdown13;
    private final ModelPart boostdown14;
    private final ModelPart boostdown15;
    private final ModelPart boostdown16;
    private final ModelPart top;
    private final ModelPart down1;
    private final ModelPart down2;
    private final ModelPart down3;
    private final ModelPart down4;
    private final ModelPart middle1;
    private final ModelPart middle2;
    private final ModelPart middle3;
    private final ModelPart middle4;
    public AdvanceRocketModel(ModelPart root) {
        this.main = root.getChild("main");
        this.engine = main.getChild("engine");
        this.down = engine.getChild("down");
        this.edown1 = down.getChild("edown1");
        this.edown2 = down.getChild("edown2");
        this.edown3 = down.getChild("edown3");
        this.edown4 = down.getChild("edown4");
        this.edown5 = down.getChild("edown5");
        this.edown6 = down.getChild("edown6");
        this.edown7 = down.getChild("edown7");
        this.edown8 = down.getChild("edown8");
        this.up = engine.getChild("up");
        this.eup1 = up.getChild("eup1");
        this.eup2 = up.getChild("eup2");
        this.eup3 = up.getChild("eup3");
        this.eup4 = up.getChild("eup4");
        this.eup5 = up.getChild("eup5");
        this.eup6 = up.getChild("eup6");
        this.eup7 = up.getChild("eup7");
        this.eup8 = up.getChild("eup8");
        this.body = main.getChild("body");
        this.bl = body.getChild("bl");
        this.ml = body.getChild("ml");
        this.fin = main.getChild("fin");
        this.f1 = fin.getChild("f1");
        this.boostdown1 = f1.getChild("boostdown1");
        this.boostdown2 = f1.getChild("boostdown2");
        this.boostdown3 = f1.getChild("boostdown3");
        this.boostdown4 = f1.getChild("boostdown4");
        this.f2 = fin.getChild("f2");
        this.boostdown5 = f2.getChild("boostdown5");
        this.boostdown6 = f2.getChild("boostdown6");
        this.boostdown7 = f2.getChild("boostdown7");
        this.boostdown8 = f2.getChild("boostdown8");
        this.f3 = fin.getChild("f3");
        this.boostdown9 = f3.getChild("boostdown9");
        this.boostdown10 = f3.getChild("boostdown10");
        this.boostdown11 = f3.getChild("boostdown11");
        this.boostdown12 = f3.getChild("boostdown12");
        this.f4 = fin.getChild("f4");
        this.boostdown13 = f4.getChild("boostdown13");
        this.boostdown14 = f4.getChild("boostdown14");
        this.boostdown15 = f4.getChild("boostdown15");
        this.boostdown16 = f4.getChild("boostdown16");
        this.top = main.getChild("top");
        this.down1 = top.getChild("down1");
        this.down2 = top.getChild("down2");
        this.down3 = top.getChild("down3");
        this.down4 = top.getChild("down4");
        this.middle1 = top.getChild("middle1");
        this.middle2 = top.getChild("middle2");
        this.middle3 = top.getChild("middle3");
        this.middle4 = top.getChild("middle4");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData engine = main.addChild("engine", ModelPartBuilder.create().uv(0, 9).cuboid(-2.0F, -9.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData down = engine.addChild("down", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData edown1 = down.addChild("edown1", ModelPartBuilder.create().uv(10, 14).cuboid(5.0F, -1.0F, -2.5F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(16, 9).cuboid(3.75F, -6.5F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData l_r1 = edown1.addChild("l_r1", ModelPartBuilder.create().uv(18, 20).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.75F, -1.0F, 2.5F, 0.1068F, -0.3786F, -0.2823F));

        ModelPartData fdown_r1 = edown1.addChild("fdown_r1", ModelPartBuilder.create().uv(0, 14).cuboid(0.0F, -5.0F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 0.0F, 0.0F, 3.1416F, -0.2618F));

        ModelPartData edown2 = down.addChild("edown2", ModelPartBuilder.create().uv(10, 14).cuboid(5.0F, -1.0F, -2.5F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(16, 9).cuboid(3.75F, -6.5F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData l_r2 = edown2.addChild("l_r2", ModelPartBuilder.create().uv(18, 20).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.75F, -1.0F, 2.5F, 0.1068F, -0.3786F, -0.2823F));

        ModelPartData fdown_r2 = edown2.addChild("fdown_r2", ModelPartBuilder.create().uv(0, 14).cuboid(0.0F, -5.0F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 0.0F, 0.0F, 3.1416F, -0.2618F));

        ModelPartData edown3 = down.addChild("edown3", ModelPartBuilder.create().uv(10, 14).cuboid(5.0F, -1.0F, -2.5F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(16, 9).cuboid(3.75F, -6.5F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData l_r3 = edown3.addChild("l_r3", ModelPartBuilder.create().uv(18, 20).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.75F, -1.0F, 2.5F, 0.1068F, -0.3786F, -0.2823F));

        ModelPartData fdown_r3 = edown3.addChild("fdown_r3", ModelPartBuilder.create().uv(0, 14).cuboid(0.0F, -5.0F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 0.0F, 0.0F, 3.1416F, -0.2618F));

        ModelPartData edown4 = down.addChild("edown4", ModelPartBuilder.create().uv(10, 14).cuboid(5.0F, -1.0F, -2.5F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(16, 9).cuboid(3.75F, -6.5F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.3562F, 0.0F));

        ModelPartData l_r4 = edown4.addChild("l_r4", ModelPartBuilder.create().uv(18, 20).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.75F, -1.0F, 2.5F, 0.1068F, -0.3786F, -0.2823F));

        ModelPartData fdown_r4 = edown4.addChild("fdown_r4", ModelPartBuilder.create().uv(0, 14).cuboid(0.0F, -5.0F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 0.0F, 0.0F, 3.1416F, -0.2618F));

        ModelPartData edown5 = down.addChild("edown5", ModelPartBuilder.create().uv(10, 14).cuboid(5.0F, -1.0F, -2.5F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(16, 9).cuboid(3.75F, -6.5F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData l_r5 = edown5.addChild("l_r5", ModelPartBuilder.create().uv(18, 20).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.75F, -1.0F, 2.5F, 0.1068F, -0.3786F, -0.2823F));

        ModelPartData fdown_r5 = edown5.addChild("fdown_r5", ModelPartBuilder.create().uv(0, 14).cuboid(0.0F, -5.0F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 0.0F, 0.0F, 3.1416F, -0.2618F));

        ModelPartData edown6 = down.addChild("edown6", ModelPartBuilder.create().uv(10, 14).cuboid(5.0F, -1.0F, -2.5F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(16, 9).cuboid(3.75F, -6.5F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.3562F, 0.0F));

        ModelPartData l_r6 = edown6.addChild("l_r6", ModelPartBuilder.create().uv(18, 20).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.75F, -1.0F, 2.5F, 0.1068F, -0.3786F, -0.2823F));

        ModelPartData fdown_r6 = edown6.addChild("fdown_r6", ModelPartBuilder.create().uv(0, 14).cuboid(0.0F, -5.0F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 0.0F, 0.0F, 3.1416F, -0.2618F));

        ModelPartData edown7 = down.addChild("edown7", ModelPartBuilder.create().uv(10, 14).cuboid(5.0F, -1.0F, -2.5F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(16, 9).cuboid(3.75F, -6.5F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData l_r7 = edown7.addChild("l_r7", ModelPartBuilder.create().uv(18, 20).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.75F, -1.0F, 2.5F, 0.1068F, -0.3786F, -0.2823F));

        ModelPartData fdown_r7 = edown7.addChild("fdown_r7", ModelPartBuilder.create().uv(0, 14).cuboid(0.0F, -5.0F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 0.0F, 0.0F, 3.1416F, -0.2618F));

        ModelPartData edown8 = down.addChild("edown8", ModelPartBuilder.create().uv(10, 14).cuboid(5.0F, -1.0F, -2.5F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(16, 9).cuboid(3.75F, -6.5F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData l_r8 = edown8.addChild("l_r8", ModelPartBuilder.create().uv(18, 20).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.75F, -1.0F, 2.5F, 0.1068F, -0.3786F, -0.2823F));

        ModelPartData fdown_r8 = edown8.addChild("fdown_r8", ModelPartBuilder.create().uv(0, 14).cuboid(0.0F, -5.0F, -2.5F, 0.0F, 5.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -1.0F, 0.0F, 0.0F, 3.1416F, -0.2618F));

        ModelPartData up = engine.addChild("up", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData eup1 = up.addChild("eup1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -6.5F, 0.0F));

        ModelPartData l_r9 = eup1.addChild("l_r9", ModelPartBuilder.create().uv(22, 14).cuboid(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.1F, 0.25F, -1.75F, -0.3375F, -0.186F, 1.1189F));

        ModelPartData up_r1 = eup1.addChild("up_r1", ModelPartBuilder.create().uv(10, 20).cuboid(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 0.0F, 0.0F, 3.1416F, 0.0F, -2.0944F));

        ModelPartData eup2 = up.addChild("eup2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -6.5F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData l_r10 = eup2.addChild("l_r10", ModelPartBuilder.create().uv(22, 14).cuboid(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.1F, 0.25F, -1.75F, -0.3375F, -0.186F, 1.1189F));

        ModelPartData up_r2 = eup2.addChild("up_r2", ModelPartBuilder.create().uv(10, 20).cuboid(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 0.0F, 0.0F, 3.1416F, 0.0F, -2.0944F));

        ModelPartData eup3 = up.addChild("eup3", ModelPartBuilder.create(), ModelTransform.of(0.0F, -6.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData l_r11 = eup3.addChild("l_r11", ModelPartBuilder.create().uv(22, 14).cuboid(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.1F, 0.25F, -1.75F, -0.3375F, -0.186F, 1.1189F));

        ModelPartData up_r3 = eup3.addChild("up_r3", ModelPartBuilder.create().uv(10, 20).cuboid(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 0.0F, 0.0F, 3.1416F, 0.0F, -2.0944F));

        ModelPartData eup4 = up.addChild("eup4", ModelPartBuilder.create(), ModelTransform.of(0.0F, -6.5F, 0.0F, 0.0F, -2.3562F, 0.0F));

        ModelPartData l_r12 = eup4.addChild("l_r12", ModelPartBuilder.create().uv(22, 14).cuboid(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.1F, 0.25F, -1.75F, -0.3375F, -0.186F, 1.1189F));

        ModelPartData up_r4 = eup4.addChild("up_r4", ModelPartBuilder.create().uv(10, 20).cuboid(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 0.0F, 0.0F, 3.1416F, 0.0F, -2.0944F));

        ModelPartData eup5 = up.addChild("eup5", ModelPartBuilder.create(), ModelTransform.of(0.0F, -6.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData l_r13 = eup5.addChild("l_r13", ModelPartBuilder.create().uv(22, 14).cuboid(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.1F, 0.25F, -1.75F, -0.3375F, -0.186F, 1.1189F));

        ModelPartData up_r5 = eup5.addChild("up_r5", ModelPartBuilder.create().uv(10, 20).cuboid(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 0.0F, 0.0F, 3.1416F, 0.0F, -2.0944F));

        ModelPartData eup6 = up.addChild("eup6", ModelPartBuilder.create(), ModelTransform.of(0.0F, -6.5F, 0.0F, 0.0F, 2.3562F, 0.0F));

        ModelPartData l_r14 = eup6.addChild("l_r14", ModelPartBuilder.create().uv(22, 14).cuboid(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.1F, 0.25F, -1.75F, -0.3375F, -0.186F, 1.1189F));

        ModelPartData up_r6 = eup6.addChild("up_r6", ModelPartBuilder.create().uv(10, 20).cuboid(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 0.0F, 0.0F, 3.1416F, 0.0F, -2.0944F));

        ModelPartData eup7 = up.addChild("eup7", ModelPartBuilder.create(), ModelTransform.of(0.0F, -6.5F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData l_r15 = eup7.addChild("l_r15", ModelPartBuilder.create().uv(22, 14).cuboid(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.1F, 0.25F, -1.75F, -0.3375F, -0.186F, 1.1189F));

        ModelPartData up_r7 = eup7.addChild("up_r7", ModelPartBuilder.create().uv(10, 20).cuboid(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 0.0F, 0.0F, 3.1416F, 0.0F, -2.0944F));

        ModelPartData eup8 = up.addChild("eup8", ModelPartBuilder.create(), ModelTransform.of(0.0F, -6.5F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData l_r16 = eup8.addChild("l_r16", ModelPartBuilder.create().uv(22, 14).cuboid(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.1F, 0.25F, -1.75F, -0.3375F, -0.186F, 1.1189F));

        ModelPartData up_r8 = eup8.addChild("up_r8", ModelPartBuilder.create().uv(10, 20).cuboid(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 0.0F, 0.0F, 3.1416F, 0.0F, -2.0944F));

        ModelPartData body = main.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -2.0F, -4.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 28).cuboid(-6.0F, -3.0F, -6.0F, 12.0F, 1.0F, 12.0F, new Dilation(0.0F))
                .uv(0, 41).cuboid(-6.5F, -38.0F, -6.5F, 13.0F, 35.0F, 13.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData bl = body.addChild("bl", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bl4_r1 = bl.addChild("bl4_r1", ModelPartBuilder.create().uv(18, 20).cuboid(-0.5F, -0.5F, 2.0F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.3054F, 2.3562F, 0.0F));

        ModelPartData bl3_r1 = bl.addChild("bl3_r1", ModelPartBuilder.create().uv(18, 20).cuboid(-0.5F, -0.5F, 2.0F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.3054F, -2.3562F, 0.0F));

        ModelPartData bl2_r1 = bl.addChild("bl2_r1", ModelPartBuilder.create().uv(18, 20).cuboid(-0.5F, -0.5F, 2.0F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.3054F, 0.7854F, 0.0F));

        ModelPartData bl1_r1 = bl.addChild("bl1_r1", ModelPartBuilder.create().uv(18, 20).cuboid(-0.5F, -0.5F, 2.0F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.3054F, -0.7854F, 0.0F));

        ModelPartData ml = body.addChild("ml", ModelPartBuilder.create().uv(36, 2).cuboid(-7.0F, -39.0F, -7.0F, 1.0F, 37.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 2).cuboid(6.0F, -39.0F, -7.0F, 1.0F, 37.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 2).cuboid(6.0F, -39.0F, 6.0F, 1.0F, 37.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 2).cuboid(-7.0F, -39.0F, 6.0F, 1.0F, 37.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 31).cuboid(-7.5F, -17.0F, 5.5F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F))
                .uv(40, 31).cuboid(-7.5F, -17.0F, -7.5F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F))
                .uv(40, 31).cuboid(5.5F, -17.0F, -7.5F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F))
                .uv(40, 31).cuboid(5.5F, -17.0F, 5.5F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fin = main.addChild("fin", ModelPartBuilder.create(), ModelTransform.pivot(-6.5F, -19.0F, 6.5F));

        ModelPartData f1 = fin.addChild("f1", ModelPartBuilder.create().uv(40, 5).cuboid(-16.5F, 0.0F, 12.5F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F))
                .uv(40, 1).cuboid(-16.0F, 12.0F, 13.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 35).cuboid(-16.0F, -2.0F, 13.0F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-15.5F, -3.0F, 13.5F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(6.5F, 0.0F, -6.5F));

        ModelPartData fbe_r1 = f1.addChild("fbe_r1", ModelPartBuilder.create().uv(40, 21).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-10.0F, 0.0F, 10.0F, -0.5236F, -0.7854F, 0.0F));

        ModelPartData fb_r1 = f1.addChild("fb_r1", ModelPartBuilder.create().uv(40, 33).cuboid(-1.0F, -2.5F, -3.5F, 2.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-7.5F, -1.5F, 7.5F, -0.5236F, -0.7854F, 0.0F));

        ModelPartData boostdown1 = f1.addChild("boostdown1", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-14.5F, 13.0F, 14.5F));

        ModelPartData l_r17 = boostdown1.addChild("l_r17", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r1 = boostdown1.addChild("face1_r1", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData boostdown2 = f1.addChild("boostdown2", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, 13.0F, 14.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData l_r18 = boostdown2.addChild("l_r18", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r2 = boostdown2.addChild("face1_r2", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData boostdown3 = f1.addChild("boostdown3", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, 13.0F, 14.5F, 0.0F, 3.1416F, 0.0F));

        ModelPartData l_r19 = boostdown3.addChild("l_r19", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r3 = boostdown3.addChild("face1_r3", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData boostdown4 = f1.addChild("boostdown4", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, 13.0F, 14.5F, 0.0F, 1.5708F, 0.0F));

        ModelPartData l_r20 = boostdown4.addChild("l_r20", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r4 = boostdown4.addChild("face1_r4", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData f2 = fin.addChild("f2", ModelPartBuilder.create().uv(40, 5).cuboid(-16.5F, 0.0F, 12.5F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F))
                .uv(40, 1).cuboid(-16.0F, 12.0F, 13.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 35).cuboid(-16.0F, -2.0F, 13.0F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-15.5F, -3.0F, 13.5F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.5F, 0.0F, -6.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData fbe_r2 = f2.addChild("fbe_r2", ModelPartBuilder.create().uv(40, 21).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-10.0F, 0.0F, 10.0F, -0.5236F, -0.7854F, 0.0F));

        ModelPartData fb_r2 = f2.addChild("fb_r2", ModelPartBuilder.create().uv(40, 33).cuboid(-1.0F, -2.5F, -3.5F, 2.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-7.5F, -1.5F, 7.5F, -0.5236F, -0.7854F, 0.0F));

        ModelPartData boostdown5 = f2.addChild("boostdown5", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-14.5F, 13.0F, 14.5F));

        ModelPartData l_r21 = boostdown5.addChild("l_r21", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r5 = boostdown5.addChild("face1_r5", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData boostdown6 = f2.addChild("boostdown6", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, 13.0F, 14.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData l_r22 = boostdown6.addChild("l_r22", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r6 = boostdown6.addChild("face1_r6", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData boostdown7 = f2.addChild("boostdown7", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, 13.0F, 14.5F, 0.0F, 3.1416F, 0.0F));

        ModelPartData l_r23 = boostdown7.addChild("l_r23", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r7 = boostdown7.addChild("face1_r7", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData boostdown8 = f2.addChild("boostdown8", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, 13.0F, 14.5F, 0.0F, 1.5708F, 0.0F));

        ModelPartData l_r24 = boostdown8.addChild("l_r24", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r8 = boostdown8.addChild("face1_r8", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData f3 = fin.addChild("f3", ModelPartBuilder.create().uv(40, 5).cuboid(-16.5F, 0.0F, 12.5F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F))
                .uv(40, 1).cuboid(-16.0F, 12.0F, 13.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 35).cuboid(-16.0F, -2.0F, 13.0F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-15.5F, -3.0F, 13.5F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.5F, 0.0F, -6.5F, 0.0F, 3.1416F, 0.0F));

        ModelPartData fbe_r3 = f3.addChild("fbe_r3", ModelPartBuilder.create().uv(40, 21).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-10.0F, 0.0F, 10.0F, -0.5236F, -0.7854F, 0.0F));

        ModelPartData fb_r3 = f3.addChild("fb_r3", ModelPartBuilder.create().uv(40, 33).cuboid(-1.0F, -2.5F, -3.5F, 2.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-7.5F, -1.5F, 7.5F, -0.5236F, -0.7854F, 0.0F));

        ModelPartData boostdown9 = f3.addChild("boostdown9", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-14.5F, 13.0F, 14.5F));

        ModelPartData l_r25 = boostdown9.addChild("l_r25", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r9 = boostdown9.addChild("face1_r9", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData boostdown10 = f3.addChild("boostdown10", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, 13.0F, 14.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData l_r26 = boostdown10.addChild("l_r26", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r10 = boostdown10.addChild("face1_r10", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData boostdown11 = f3.addChild("boostdown11", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, 13.0F, 14.5F, 0.0F, 3.1416F, 0.0F));

        ModelPartData l_r27 = boostdown11.addChild("l_r27", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r11 = boostdown11.addChild("face1_r11", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData boostdown12 = f3.addChild("boostdown12", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, 13.0F, 14.5F, 0.0F, 1.5708F, 0.0F));

        ModelPartData l_r28 = boostdown12.addChild("l_r28", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r12 = boostdown12.addChild("face1_r12", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData f4 = fin.addChild("f4", ModelPartBuilder.create().uv(40, 5).cuboid(-16.5F, 0.0F, 12.5F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F))
                .uv(40, 1).cuboid(-16.0F, 12.0F, 13.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 35).cuboid(-16.0F, -2.0F, 13.0F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-15.5F, -3.0F, 13.5F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.5F, 0.0F, -6.5F, 0.0F, 1.5708F, 0.0F));

        ModelPartData fbe_r4 = f4.addChild("fbe_r4", ModelPartBuilder.create().uv(40, 21).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-10.0F, 0.0F, 10.0F, -0.5236F, -0.7854F, 0.0F));

        ModelPartData fb_r4 = f4.addChild("fb_r4", ModelPartBuilder.create().uv(40, 33).cuboid(-1.0F, -2.5F, -3.5F, 2.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-7.5F, -1.5F, 7.5F, -0.5236F, -0.7854F, 0.0F));

        ModelPartData boostdown13 = f4.addChild("boostdown13", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-14.5F, 13.0F, 14.5F));

        ModelPartData l_r29 = boostdown13.addChild("l_r29", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r13 = boostdown13.addChild("face1_r13", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData boostdown14 = f4.addChild("boostdown14", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, 13.0F, 14.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData l_r30 = boostdown14.addChild("l_r30", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r14 = boostdown14.addChild("face1_r14", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData boostdown15 = f4.addChild("boostdown15", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, 13.0F, 14.5F, 0.0F, 3.1416F, 0.0F));

        ModelPartData l_r31 = boostdown15.addChild("l_r31", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r15 = boostdown15.addChild("face1_r15", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData boostdown16 = f4.addChild("boostdown16", ModelPartBuilder.create().uv(24, 0).cuboid(-2.5F, 5.75F, 2.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-14.5F, 13.0F, 14.5F, 0.0F, 1.5708F, 0.0F));

        ModelPartData l_r32 = boostdown16.addChild("l_r32", ModelPartBuilder.create().uv(56, 6).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 6.0F, 2.5F, 0.2618F, 0.0F, 0.2618F));

        ModelPartData face1_r16 = boostdown16.addChild("face1_r16", ModelPartBuilder.create().uv(52, 0).cuboid(-3.0F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 0.0F, 0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData top = main.addChild("top", ModelPartBuilder.create().uv(0, 89).cuboid(-7.5F, -2.0F, -7.5F, 15.0F, 2.0F, 15.0F, new Dilation(0.0F))
                .uv(0, 116).cuboid(-5.0F, -12.5F, -5.0F, 10.0F, 1.0F, 10.0F, new Dilation(0.0F))
                .uv(0, 116).cuboid(-5.0F, -19.5F, -5.0F, 10.0F, 1.0F, 10.0F, new Dilation(0.0F))
                .uv(60, 0).cuboid(-4.5F, -18.5F, -4.5F, 9.0F, 6.0F, 9.0F, new Dilation(0.0F))
                .uv(34, 121).cuboid(-1.5F, -26.5F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -46.0F, 0.0F));

        ModelPartData topu_r1 = top.addChild("topu_r1", ModelPartBuilder.create().uv(44, 124).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(43, 118).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
                .uv(34, 116).cuboid(-1.0F, 5.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -34.5F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData down1 = top.addChild("down1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData downl_r1 = down1.addChild("downl_r1", ModelPartBuilder.create().uv(0, 89).cuboid(-0.5F, -11.0F, -0.5F, 1.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(7.0F, -1.5F, -7.0F, -0.2533F, -0.067F, -0.2533F));

        ModelPartData dface_r1 = down1.addChild("dface_r1", ModelPartBuilder.create().uv(0, 106).cuboid(-7.0F, -8.1883F, -6.7615F, 14.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData down2 = top.addChild("down2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData downl_r2 = down2.addChild("downl_r2", ModelPartBuilder.create().uv(0, 89).cuboid(-0.5F, -11.0F, -0.5F, 1.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(7.0F, -1.5F, -7.0F, -0.2533F, -0.067F, -0.2533F));

        ModelPartData dface_r2 = down2.addChild("dface_r2", ModelPartBuilder.create().uv(0, 106).cuboid(-7.0F, -8.1883F, -6.7615F, 14.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData down3 = top.addChild("down3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData downl_r3 = down3.addChild("downl_r3", ModelPartBuilder.create().uv(0, 89).cuboid(-0.5F, -11.0F, -0.5F, 1.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(7.0F, -1.5F, -7.0F, -0.2533F, -0.067F, -0.2533F));

        ModelPartData dface_r3 = down3.addChild("dface_r3", ModelPartBuilder.create().uv(0, 106).cuboid(-7.0F, -8.1883F, -6.7615F, 14.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData down4 = top.addChild("down4", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData downl_r4 = down4.addChild("downl_r4", ModelPartBuilder.create().uv(0, 89).cuboid(-0.5F, -11.0F, -0.5F, 1.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(7.0F, -1.5F, -7.0F, -0.2533F, -0.067F, -0.2533F));

        ModelPartData dface_r4 = down4.addChild("dface_r4", ModelPartBuilder.create().uv(0, 106).cuboid(-7.0F, -8.1883F, -6.7615F, 14.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData middle1 = top.addChild("middle1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -19.5F, 0.0F));

        ModelPartData middlel_r1 = middle1.addChild("middlel_r1", ModelPartBuilder.create().uv(30, 117).cuboid(-0.5F, -8.0F, -0.5F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.25F, 0.5F, 4.25F, 0.4363F, 0.0F, -0.5236F));

        ModelPartData middleface_r1 = middle1.addChild("middleface_r1", ModelPartBuilder.create().uv(56, 7).cuboid(0.0F, -7.0F, -4.0F, 0.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(4.5F, 0.5F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData middle2 = top.addChild("middle2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -19.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData middlel_r2 = middle2.addChild("middlel_r2", ModelPartBuilder.create().uv(30, 117).cuboid(-0.5F, -8.0F, -0.5F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.25F, 0.5F, 4.25F, 0.4363F, 0.0F, -0.5236F));

        ModelPartData middleface_r2 = middle2.addChild("middleface_r2", ModelPartBuilder.create().uv(56, 7).cuboid(0.0F, -7.0F, -4.0F, 0.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(4.5F, 0.5F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData middle3 = top.addChild("middle3", ModelPartBuilder.create(), ModelTransform.of(0.0F, -19.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData middlel_r3 = middle3.addChild("middlel_r3", ModelPartBuilder.create().uv(30, 117).cuboid(-0.5F, -8.0F, -0.5F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.25F, 0.5F, 4.25F, 0.4363F, 0.0F, -0.5236F));

        ModelPartData middleface_r3 = middle3.addChild("middleface_r3", ModelPartBuilder.create().uv(56, 7).cuboid(0.0F, -7.0F, -4.0F, 0.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(4.5F, 0.5F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData middle4 = top.addChild("middle4", ModelPartBuilder.create(), ModelTransform.of(0.0F, -19.5F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData middlel_r4 = middle4.addChild("middlel_r4", ModelPartBuilder.create().uv(30, 117).cuboid(-0.5F, -8.0F, -0.5F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.25F, 0.5F, 4.25F, 0.4363F, 0.0F, -0.5236F));

        ModelPartData middleface_r4 = middle4.addChild("middleface_r4", ModelPartBuilder.create().uv(56, 7).cuboid(0.0F, -7.0F, -4.0F, 0.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(4.5F, 0.5F, 0.0F, 0.0F, 0.0F, -0.5236F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void setAngles(AdvanceRocketEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}