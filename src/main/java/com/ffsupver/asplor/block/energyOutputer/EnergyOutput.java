package com.ffsupver.asplor.block.energyOutputer;

import appeng.block.AEBaseEntityBlock;
import com.ffsupver.asplor.AllBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnergyOutput extends AEBaseEntityBlock<EnergyOutputEntity> {
    private @Nullable BlockEntityTicker<EnergyOutputEntity> serverTicker;
    private @Nullable BlockEntityTicker<EnergyOutputEntity> clientTicker;
    public EnergyOutput() {
        super(metalProps());
    }

    @Override
    public void setBlockEntity(Class<EnergyOutputEntity> blockEntityClass, BlockEntityType<EnergyOutputEntity> blockEntityType, BlockEntityTicker<EnergyOutputEntity> clientTicker, BlockEntityTicker<EnergyOutputEntity> serverTicker) {
        super.setBlockEntity(EnergyOutputEntity.class, AllBlockEntityTypes.ENERGY_OUTPUT_ENTITY, clientTicker, serverTicker);
    }



    @Override
    public BlockEntityType<EnergyOutputEntity> getBlockEntityType() {
        return AllBlockEntityTypes.ENERGY_OUTPUT_ENTITY;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return AllBlockEntityTypes.ENERGY_OUTPUT_ENTITY.instantiate(pos, state);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return false;
    }
}
