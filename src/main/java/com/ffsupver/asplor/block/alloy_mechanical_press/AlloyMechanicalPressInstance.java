package com.ffsupver.asplor.block.alloy_mechanical_press;

import com.ffsupver.asplor.AllPartialModels;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class AlloyMechanicalPressInstance extends ShaftInstance<AlloyMechanicalPressEntity> implements DynamicInstance {

	private final OrientedData pressHead;

	public AlloyMechanicalPressInstance(MaterialManager materialManager, AlloyMechanicalPressEntity blockEntity) {
		super(materialManager, blockEntity);

		pressHead = materialManager.defaultSolid()
				.material(Materials.ORIENTED)
				.getModel(AllPartialModels.ALLOY_MECHANICAL_PRESS_HEAD, blockState)
				.createInstance();

		Quaternionf q = RotationAxis.POSITIVE_Y
			.rotationDegrees(AngleHelper.horizontalAngle(blockState.get(MechanicalPressBlock.HORIZONTAL_FACING)));

		pressHead.setRotation(q);

		transformModels();
	}

	@Override
	public void beginFrame() {
		transformModels();
	}

	private void transformModels() {
		float renderedHeadOffset = getRenderedHeadOffset(blockEntity);

		pressHead.setPosition(getInstancePosition())
			.nudge(0, -renderedHeadOffset, 0);
	}

	private float getRenderedHeadOffset(AlloyMechanicalPressEntity press) {
		PressingBehaviour pressingBehaviour = press.getPressingBehaviour();
		return pressingBehaviour.getRenderedHeadOffset(AnimationTickHolder.getPartialTicks())
			* pressingBehaviour.mode.headOffset;
	}

	@Override
	public void updateLight() {
		super.updateLight();

		relight(pos, pressHead);
	}

	@Override
	public void remove() {
		super.remove();
		pressHead.delete();
	}
}
