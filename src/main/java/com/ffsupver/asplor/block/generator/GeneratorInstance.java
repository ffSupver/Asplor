package com.ffsupver.asplor.block.generator;

import com.ffsupver.asplor.block.divider.DividerEntity;
import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

import static com.ffsupver.asplor.AllPartialModels.Divider_ROTATING_MODEL;
import static com.ffsupver.asplor.AllPartialModels.GENERATOR_ROTATING_MODEL;
import static net.minecraft.state.property.Properties.FACING;

public class GeneratorInstance extends KineticBlockEntityInstance<GeneratorEntity> {
    protected final RotatingData generatorModel;
    final Direction direction;
    private final Direction opposite;
    public GeneratorInstance(MaterialManager materialManager, GeneratorEntity blockEntity) {
        super(materialManager, blockEntity);
        direction = blockState.get(FACING);

        opposite = direction.getOpposite();
        generatorModel=materialManager.defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(GENERATOR_ROTATING_MODEL,blockState,opposite)
                .createInstance();
        setup(generatorModel);
    }

    @Override
    public void update() {
        updateRotation(generatorModel);
    }

    @Override
    public void updateLight() {
        BlockPos behind = pos.offset(opposite);

        BlockPos inFront = pos.offset(direction);
        relight(inFront, generatorModel);
    }

    @Override
    protected void remove() {
        generatorModel.delete();
    }

}
