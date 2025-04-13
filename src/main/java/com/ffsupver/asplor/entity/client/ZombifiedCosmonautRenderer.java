package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.entity.custom.ZombifiedCosmonaut;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import static earth.terrarium.adastra.client.models.armor.SpaceSuitModel.SPACE_SUIT_LAYER;

public class ZombifiedCosmonautRenderer extends ZombieBaseEntityRenderer<ZombifiedCosmonaut, ZombieEntityModel<ZombifiedCosmonaut>> {
    public static final Identifier ZOMBIFIED_COSMONAUT_TEXTURE = new Identifier(Asplor.MOD_ID,"textures/entity/zombified_cosmonaut/zombified_cosmonaut.png");
        public static final Identifier ZOMBIFIED_COSMONAUT_SPACE_SUIT_TEXTURE = new Identifier(Asplor.MOD_ID,"textures/entity/zombified_cosmonaut/zombified_cosmonaut_space_suit.png");
    public static EntityModelLayer ZOMBIFIED_COSMONAUT_SPACE_SUIT = ModModelLayers.createEntityModelLayer("zombified_cosmonaut","zombified_cosmonaut_space_suit");
    private final BipedEntityModel<ZombifiedCosmonaut> spaceSuitModel;


    public ZombifiedCosmonautRenderer(EntityRendererFactory.Context ctx){
        this(ctx, EntityModelLayers.ZOMBIE, EntityModelLayers.ZOMBIE_INNER_ARMOR, EntityModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    protected ZombifiedCosmonautRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer legsArmorLayer, EntityModelLayer bodyArmorLayer) {
        super(ctx,new ZombieEntityModel<>(ctx.getPart(layer)),new ZombieEntityModel<>(ctx.getPart(legsArmorLayer)),new ZombieEntityModel<>(ctx.getPart(bodyArmorLayer)));
        this.spaceSuitModel = new BipedEntityModel<>(ctx.getPart(SPACE_SUIT_LAYER));
    }

    @Override
    public Identifier getTexture(ZombifiedCosmonaut entity) {
        return ZOMBIFIED_COSMONAUT_TEXTURE;
    }

    @Override
    public void render(ZombifiedCosmonaut mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);

        model.copyBipedStateTo(spaceSuitModel);
        VertexConsumer consumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(ZOMBIFIED_COSMONAUT_SPACE_SUIT_TEXTURE));
        int p = getOverlay(mobEntity, this.getAnimationCounter(mobEntity, g));
        boolean bl = this.isVisible(mobEntity);
        boolean bl2 = !bl && !mobEntity.isInvisibleTo(MinecraftClient.getInstance().player);

        matrixStack.push();

        setUpTranslate(mobEntity,g,matrixStack);

        spaceSuitModel.render(matrixStack,consumer,i,p,1.0F, 1.0F, 1.0F,bl2 ? 0.15F : 1.0F);


        matrixStack.pop();
    }

    private void setUpTranslate(ZombifiedCosmonaut livingEntity, float g, MatrixStack matrixStack){

        float h = MathHelper.lerpAngleDegrees(g, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
        float j = MathHelper.lerpAngleDegrees(g, livingEntity.prevHeadYaw, livingEntity.headYaw);
        float k = j - h;
        if (livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)livingEntity.getVehicle();
            h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
            k = j - h;
            float l = MathHelper.wrapDegrees(k);
            if (l < -85.0F) {
                l = -85.0F;
            }

            if (l >= 85.0F) {
                l = 85.0F;
            }

            h = j - l;
            if (l * l > 2500.0F) {
                h += l * 0.2F;
            }

            k = j - h;
        }

        float m = MathHelper.lerp(g, livingEntity.prevPitch, livingEntity.getPitch());
        if (shouldFlipUpsideDown(livingEntity)) {
            m *= -1.0F;
            k *= -1.0F;
        }

        if (livingEntity.isInPose(EntityPose.SLEEPING)) {
            Direction direction = livingEntity.getSleepingDirection();
            if (direction != null) {
                float n = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
                matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0F, (float)(-direction.getOffsetZ()) * n);
            }
        }

        float lx = this.getAnimationProgress(livingEntity, g);
        this.setupTransforms(livingEntity, matrixStack, lx, h, g);


        float scale = 1.16f;
        float offsetY = livingEntity.getHeight() * (livingEntity.isBaby() ? 55 : 51) /64;
        matrixStack.translate(0,offsetY,0);
        matrixStack.scale(scale,scale,scale);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));
    }
}

