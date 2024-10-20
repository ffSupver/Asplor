package com.ffsupver.asplor.block.divider;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;

import static com.ffsupver.asplor.AllPartialModels.Divider_ROTATING_MODEL;

public class DividerInstance extends SingleRotatingInstance<DividerEntity> {
    public DividerInstance(MaterialManager materialManager, DividerEntity blockEntity) {
        super(materialManager, blockEntity);
    }
    @Override
    protected Instancer<RotatingData> getModel() {
        return getRotatingMaterial().getModel(Divider_ROTATING_MODEL, blockEntity.getCachedState());
    }
}
