package com.ffsupver.asplor.block.liquid_blaze_burner;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class LiquidBlazeBurnerRenderer extends SafeBlockEntityRenderer<LiquidBlazeBurnerEntity> {
    public LiquidBlazeBurnerRenderer(BlockEntityRendererFactory.Context context) {}


    @Override
    protected void renderSafe(LiquidBlazeBurnerEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider bufferSource,
                              int light, int overlay) {
        BlazeBurnerBlock.HeatLevel heatLevel = be.getHeatLevelFromBlock();
        if (heatLevel == BlazeBurnerBlock.HeatLevel.NONE)
            return;

        World level = be.getWorld();
        BlockState blockState = be.getCachedState();
        float animation = be.headAnimation.getValue(partialTicks) * .175f;
        float horizontalAngle = AngleHelper.rad(be.headAngle.getValue(partialTicks));
        boolean canDrawFlame = heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING);
        boolean drawGoggles = be.goggles;
        boolean drawHat = be.hat;
        int hashCode = be.hashCode();

        renderShared(ms, null, bufferSource,
                level, blockState, heatLevel, animation, horizontalAngle,
                canDrawFlame, drawGoggles, drawHat, hashCode);
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                           ContraptionMatrices matrices, VertexConsumerProvider bufferSource, LerpedFloat headAngle, boolean conductor) {
        BlockState state = context.state;
        BlazeBurnerBlock.HeatLevel heatLevel = LiquidBlazeBurner.getHeatLevelOf(state);
        if (heatLevel == BlazeBurnerBlock.HeatLevel.NONE)
            return;

        if (!heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
            heatLevel = BlazeBurnerBlock.HeatLevel.FADING;
        }

        World level = context.world;
        float horizontalAngle = AngleHelper.rad(headAngle.getValue(AnimationTickHolder.getPartialTicks(level)));
        boolean drawGoggles = context.blockEntityData.contains("Goggles");
        boolean drawHat = conductor || context.blockEntityData.contains("TrainHat");
        int hashCode = context.hashCode();

        renderShared(matrices.getViewProjection(), matrices.getModel(), bufferSource,
                level, state, heatLevel, 0, horizontalAngle,
                false, drawGoggles, drawHat, hashCode);
    }

    private static void renderShared(MatrixStack ms, @Nullable MatrixStack modelTransform, VertexConsumerProvider bufferSource,
                                     World level, BlockState blockState, BlazeBurnerBlock.HeatLevel heatLevel, float animation, float horizontalAngle,
                                     boolean canDrawFlame, boolean drawGoggles, boolean drawHat, int hashCode) {

        boolean blockAbove = animation > 0.125f;
        float time = AnimationTickHolder.getRenderTime(level);
        float renderTick = time + (hashCode % 13) * 16f;
        float offsetMult = heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING) ? 64 : 16;
        float offset = MathHelper.sin((float) ((renderTick / 16f) % (2 * Math.PI))) / offsetMult;
        float offset1 = MathHelper.sin((float) ((renderTick / 16f + Math.PI) % (2 * Math.PI))) / offsetMult;
        float offset2 = MathHelper.sin((float) ((renderTick / 16f + Math.PI / 2) % (2 * Math.PI))) / offsetMult;
        float headY = offset - (animation * .75f)+0.1f;

        VertexConsumer solid = bufferSource.getBuffer(RenderLayer.getSolid());
        VertexConsumer cutout = bufferSource.getBuffer(RenderLayer.getCutoutMipped());

        ms.push();

        if (canDrawFlame && blockAbove) {
            SpriteShiftEntry spriteShift =
                    heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllSpriteShifts.SUPER_BURNER_FLAME : AllSpriteShifts.BURNER_FLAME;

            float spriteWidth = spriteShift.getTarget()
                    .getMaxU()
                    - spriteShift.getTarget()
                    .getMinU();

            float spriteHeight = spriteShift.getTarget()
                    .getMaxV()
                    - spriteShift.getTarget()
                    .getMinV();

            float speed = 1 / 32f + 1 / 64f * heatLevel.ordinal();

            double vScroll = speed * time;
            vScroll = vScroll - Math.floor(vScroll);
            vScroll = vScroll * spriteHeight / 2;

            double uScroll = speed * time / 2;
            uScroll = uScroll - Math.floor(uScroll);
            uScroll = uScroll * spriteWidth / 2;

            SuperByteBuffer flameBuffer = CachedBufferer.partial(AllPartialModels.BLAZE_BURNER_FLAME, blockState);
            if (modelTransform != null)
                flameBuffer.transform(modelTransform);
            flameBuffer.shiftUVScrolling(spriteShift, (float) uScroll, (float) vScroll);
            draw(flameBuffer, horizontalAngle, ms, cutout);
        }

        PartialModel blazeModel;
        if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)) {
            blazeModel = blockAbove ? com.ffsupver.asplor.AllPartialModels.BLAZE_SUPER_ACTIVE : com.ffsupver.asplor.AllPartialModels.BLAZE_SUPER;
        } else if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
            blazeModel = blockAbove && heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.KINDLED) ? com.ffsupver.asplor.AllPartialModels.BLAZE_ACTIVE
                    : com.ffsupver.asplor.AllPartialModels.BLAZE_IDLE;
        } else {
            blazeModel = com.ffsupver.asplor.AllPartialModels.BLAZE_INERT;
        }

        SuperByteBuffer blazeBuffer = CachedBufferer.partial(blazeModel, blockState);
        if (modelTransform != null)
            blazeBuffer.transform(modelTransform);
        blazeBuffer.translate(0, headY, 0);
        draw(blazeBuffer, horizontalAngle, ms, solid);

        if (drawGoggles) {
            PartialModel gogglesModel = blazeModel == com.ffsupver.asplor.AllPartialModels.BLAZE_INERT
                    ? AllPartialModels.BLAZE_GOGGLES_SMALL : AllPartialModels.BLAZE_GOGGLES;

            SuperByteBuffer gogglesBuffer = CachedBufferer.partial(gogglesModel, blockState);
            if (modelTransform != null)
                gogglesBuffer.transform(modelTransform);
            gogglesBuffer.translate(0, headY + 8 / 16f, 0);
            draw(gogglesBuffer, horizontalAngle, ms, solid);
        }

        if (drawHat) {
            SuperByteBuffer hatBuffer = CachedBufferer.partial(AllPartialModels.TRAIN_HAT, blockState);
            if (modelTransform != null)
                hatBuffer.transform(modelTransform);
            hatBuffer.translate(0, headY, 0);
            if (blazeModel == com.ffsupver.asplor.AllPartialModels.BLAZE_INERT) {
                hatBuffer.translateY(0.5f)
                        .centre()
                        .scale(0.75f)
                        .unCentre();
            } else {
                hatBuffer.translateY(0.75f);
            }
            hatBuffer
                    .rotateCentered(Direction.UP, horizontalAngle + MathHelper.PI)
                    .translate(0.5f, 0, 0.5f)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .renderInto(ms, solid);
        }

        if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
            PartialModel rodsModel = heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllPartialModels.BLAZE_BURNER_SUPER_RODS
                    : AllPartialModels.BLAZE_BURNER_RODS;
            PartialModel rodsModel2 = heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING ? AllPartialModels.BLAZE_BURNER_SUPER_RODS_2
                    : AllPartialModels.BLAZE_BURNER_RODS_2;

            SuperByteBuffer rodsBuffer = CachedBufferer.partial(rodsModel, blockState);
            if (modelTransform != null)
                rodsBuffer.transform(modelTransform);
            rodsBuffer.translate(0, offset1 + animation + .125f, 0)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .renderInto(ms, solid);

            SuperByteBuffer rodsBuffer2 = CachedBufferer.partial(rodsModel2, blockState);
            if (modelTransform != null)
                rodsBuffer2.transform(modelTransform);
            rodsBuffer2.translate(0, offset2 + animation - 3 / 16f, 0)
                    .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                    .renderInto(ms, solid);
        }

        ms.pop();
    }

    private static void draw(SuperByteBuffer buffer, float horizontalAngle, MatrixStack ms, VertexConsumer vc) {
        buffer.rotateCentered(Direction.UP, horizontalAngle)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .renderInto(ms, vc);
    }
}
