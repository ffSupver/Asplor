package com.ffsupver.asplor.block.alloyMechanicalPress;

import com.jozufozu.flywheel.backend.Backend;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class AlloyMechanicalPressRenderer extends KineticBlockEntityRenderer<AlloyMechanicalPressEntity> {

	public AlloyMechanicalPressRenderer(BlockEntityRendererFactory.Context context) {
		super(context);
	}


//	@Override
//	public boolean shouldRenderOffScreen(AlloyMechanicalPressEntity be) {
//		return true;
//	}

	@Override
	protected void renderSafe(AlloyMechanicalPressEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
		int light, int overlay) {
		super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

		if (Backend.canUseInstancing(be.getWorld()))
			return;

		BlockState blockState = be.getCachedState();
		PressingBehaviour pressingBehaviour = be.getPressingBehaviour();
		float renderedHeadOffset =
			pressingBehaviour.getRenderedHeadOffset(partialTicks) * pressingBehaviour.mode.headOffset;

		SuperByteBuffer headRender = CachedBufferer.partialFacing(AllPartialModels.MECHANICAL_PRESS_HEAD, blockState,
			blockState.get(HORIZONTAL_FACING));
		headRender.translate(0, -renderedHeadOffset, 0)
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderLayer.getSolid()));
	}

	@Override
	protected BlockState getRenderedBlockState(AlloyMechanicalPressEntity be) {
		return shaft(getRotationAxisOf(be));
	}

}
