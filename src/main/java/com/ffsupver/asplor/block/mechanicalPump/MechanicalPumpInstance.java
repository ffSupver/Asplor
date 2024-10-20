package com.ffsupver.asplor.block.mechanicalPump;

import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static com.ffsupver.asplor.AllPartialModels.MOTOR_ROTATING_MODEL;
import static net.minecraft.state.property.Properties.FACING;

public class MechanicalPumpInstance extends KineticBlockEntityInstance<MechanicalPumpEntity> {
    protected final RotatingData generatorModel;
    final Direction direction;
    private final Direction opposite;
    public MechanicalPumpInstance(MaterialManager materialManager, MechanicalPumpEntity blockEntity) {
        super(materialManager, blockEntity);
        direction = Direction.UP;
        opposite = direction;
        generatorModel = materialManager.defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(AllPartialModels.SHAFT_HALF, blockState, opposite)
                .createInstance();
        if (blockEntity.getCachedState().get(MechanicalPump.HALF).equals(DoubleBlockHalf.UPPER)){
            setup(generatorModel);
        }
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