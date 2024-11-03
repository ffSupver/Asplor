package com.ffsupver.asplor.block.smartMechanicalArm;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.util.math.Direction;

public class SmartMechanicalArmInstance extends SingleRotatingInstance<SmartMechanicalArmEntity> {


    public SmartMechanicalArmInstance(MaterialManager materialManager, SmartMechanicalArmEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        Direction dir = Direction.DOWN;
        return getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockState, dir);
    }

    @Override
    public void onLightPacket(int chunkX, int chunkZ) {
        super.onLightPacket(chunkX, chunkZ);
    }
}
