package com.ffsupver.asplor.block.timeInjector;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

import static com.ffsupver.asplor.AllBlockEntityTypes.TIME_INJECTOR_ENTITY;

public class TimeInjector extends Block implements IBE<TimeInjectorEntity> {
    public TimeInjector(Settings settings) {
        super(settings);
    }

    @Override
    public Class<TimeInjectorEntity> getBlockEntityClass() {
        return TimeInjectorEntity.class;
    }

    @Override
    public BlockEntityType<? extends TimeInjectorEntity> getBlockEntityType() {
        return TIME_INJECTOR_ENTITY.get();
    }
}
