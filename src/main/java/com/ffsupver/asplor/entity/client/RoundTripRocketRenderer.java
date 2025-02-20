package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.entity.custom.rocket.RoundTripRocketEntity;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public abstract class RoundTripRocketRenderer<T extends RoundTripRocketEntity> extends EntityRenderer<T> {

    protected final EntityModel<T> model;
    private final float scale;

    public RoundTripRocketRenderer(EntityRendererFactory.Context context, EntityModel<T> model, float scale) {
        super(context);
        this.model = model;
        this.scale = scale;
        this.shadowRadius = 0.5F;
    }
    public RoundTripRocketRenderer(EntityRendererFactory.Context context, EntityModel<T> model) {
        this(context,model,1);
    }



    public void render(T entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight) {
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
            pose.scale(-1.0F * scale, -1.0F * scale, 1.0F * scale);
            pose.translate(0, (1 - scale),0);
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
    public abstract Identifier getTexture(T entity);

    public Identifier getTextureLocation(T entity) {
        return getTexture(entity);
    }
}
