package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.entity.custom.cargoRocket.CargoRocketEntity;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class CargoRocketRenderer extends EntityRenderer<CargoRocketEntity> {

    protected final EntityModel<CargoRocketEntity> model;
    public static final Identifier CARGO_ROCKET_TEXTURE = new Identifier(Asplor.MOD_ID,"textures/entity/rocket/cargo_rocket.png");
    public static final EntityModelLayer CARGO_ROCKET_MODEL  = new EntityModelLayer(new Identifier(Asplor.MOD_ID,"cargo_rocket"),"main");

    public CargoRocketRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.model = new CargoRocketModel(context.getPart(CARGO_ROCKET_MODEL));
    }

    public void render(CargoRocketEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        CloseablePoseStack pose = new CloseablePoseStack(poseStack);

        try {
            if (!MinecraftClient.getInstance().isPaused() && (entity.isLaunching() || entity.hasLaunched())) {
                entityYaw += (float)(entity.getWorld().random.nextGaussian() * 0.3);
            }

            pose.translate(0.0F, 1.55F, 0.0F);
            pose.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - entityYaw));
            float xRot = MathHelper.lerp(partialTick, entity.prevPitch, entity.getPitch());
            pose.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-xRot));
            pose.scale(-1.0F, -1.0F, 1.0F);
            this.model.setAngles(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            VertexConsumer consumer = buffer.getBuffer(this.model.getLayer(this.getTextureLocation(entity)));
            this.model.render(pose, consumer, packedLight, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        } catch (Throwable var11) {
            try {
                pose.close();
            } catch (Throwable var10) {
                var11.addSuppressed(var10);
            }

            throw var11;
        }

        pose.close();
    }

    @Override
    public Identifier getTexture(CargoRocketEntity entity) {
        return CARGO_ROCKET_TEXTURE;
    }

    public Identifier getTextureLocation(CargoRocketEntity entity) {
        return CARGO_ROCKET_TEXTURE;
    }

    public static class ItemRenderer extends BuiltinModelItemRenderer {
        private final EntityModelLayer layer;
        private final Identifier texture;
        private EntityModel<?> model;

        public ItemRenderer(EntityModelLayer layer, Identifier texture) {
            super(MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader());
            this.layer = layer;
            this.texture = texture;
        }

        public void render(ItemStack stack, ModelTransformationMode displayContext, MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight, int packedOverlay) {
            if (this.model == null) {
                this.model = new CargoRocketModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(this.layer));
            }

            VertexConsumer consumer = buffer.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(this.texture));
            CloseablePoseStack pose = new CloseablePoseStack(poseStack);

            try {
                pose.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
                pose.translate(0.0, -1.501, 0.0);
                this.model.render(pose, consumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            } catch (Throwable var12) {
                try {
                    pose.close();
                } catch (Throwable var11) {
                    var12.addSuppressed(var11);
                }

                throw var12;
            }

            pose.close();
        }
    }
}
