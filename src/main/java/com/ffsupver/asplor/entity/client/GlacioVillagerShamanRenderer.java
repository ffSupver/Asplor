package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.entity.custom.GlacioVillagerShaman;
import com.ffsupver.asplor.entity.custom.Meteorite;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class GlacioVillagerShamanRenderer extends MobEntityRenderer<GlacioVillagerShaman,GlacioVillagerShamanModel<GlacioVillagerShaman>> {
    public static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID,"textures/entity/glacio_villager_shaman.png");
    public static EntityModelLayer GLACIO_VILLAGER_SHAMAN_LAYER = new EntityModelLayer(new Identifier(Asplor.MOD_ID,"glacio_villager_shaman"),"main");
    protected GlacioVillagerShamanRenderer(EntityRendererFactory.Context ctx) {
        super(ctx,new GlacioVillagerShamanModel<>(ctx.getPart(GLACIO_VILLAGER_SHAMAN_LAYER)),0.5f);
        this.addFeature(new GlacioVillagerShamanHeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()));
    }

    @Override
    public void render(GlacioVillagerShaman mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
        if (mobEntity.isMeteoriteAttacking()){
            Meteorite meteoriteRenderEntity = new Meteorite(mobEntity.getWorld());
            Vec3d mPH = mobEntity.getCastingSpellsParticlePos();
            matrixStack.push();
            matrixStack.translate(mPH.getX(),2.2f,mPH.getZ());
            matrixStack.scale(0.2f,0.2f,0.2f);
            MinecraftClient.getInstance().getEntityRenderDispatcher().render(meteoriteRenderEntity,0,0,0,0,0,matrixStack,vertexConsumerProvider,i);
            matrixStack.pop();
        }
    }


    @Override
    public Identifier getTexture(GlacioVillagerShaman entity) {
        return TEXTURE;
    }

    private static class GlacioVillagerShamanHeldItemFeatureRenderer<T extends GlacioVillagerShaman,M extends EntityModel<T> & ModelWithArms> extends HeldItemFeatureRenderer<T,M>{

        public GlacioVillagerShamanHeldItemFeatureRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
            super(context, heldItemRenderer);
        }

        @Override
        public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
            matrixStack.translate(0,1.5,0);
            super.render(matrixStack, vertexConsumerProvider, i, livingEntity, f, g, h, j, k, l);
        }
    }
}
