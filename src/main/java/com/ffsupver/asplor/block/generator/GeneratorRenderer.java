package com.ffsupver.asplor.block.generator;

import com.ffsupver.asplor.AllPartialModels;
import com.jozufozu.flywheel.backend.Backend;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

import static net.minecraft.state.property.Properties.FACING;

public class GeneratorRenderer extends KineticBlockEntityRenderer<GeneratorEntity> {
    public GeneratorRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(GeneratorEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
        if (Backend.canUseInstancing(be.getWorld())) return;

        Direction direction = be.getCachedState()
                .get(FACING);
        VertexConsumer vb = buffer.getBuffer(RenderLayer.getCutoutMipped());

        int lightHere = WorldRenderer.getLightmapCoordinates(be.getWorld(), be.getPos());

        SuperByteBuffer model =
                CachedBufferer.partialFacing(AllPartialModels.GENERATOR_ROTATING_MODEL, be.getCachedState(), direction.getOpposite());

        standardKineticRotationTransform(model, be, lightHere).renderInto(ms, vb);
    }
}
