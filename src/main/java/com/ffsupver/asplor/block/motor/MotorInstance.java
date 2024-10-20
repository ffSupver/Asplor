package com.ffsupver.asplor.block.motor;

import com.ffsupver.asplor.block.generator.GeneratorEntity;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static com.ffsupver.asplor.AllPartialModels.GENERATOR_ROTATING_MODEL;
import static com.ffsupver.asplor.AllPartialModels.MOTOR_ROTATING_MODEL;
import static net.minecraft.state.property.Properties.FACING;

public class MotorInstance extends KineticBlockEntityInstance<MotorEntity> {
    protected final RotatingData generatorModel;
    final Direction direction;
    private final Direction opposite;
    public MotorInstance(MaterialManager materialManager, MotorEntity blockEntity) {
        super(materialManager, blockEntity);
        direction = blockState.get(FACING);

        opposite = direction.getOpposite();
        generatorModel=materialManager.defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(MOTOR_ROTATING_MODEL,blockState,opposite)
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
