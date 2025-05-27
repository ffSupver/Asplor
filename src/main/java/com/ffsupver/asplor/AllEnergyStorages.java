package com.ffsupver.asplor;

import com.ffsupver.asplor.block.laserDrill.LaserDrillBatteryEntity;
import com.ffsupver.asplor.block.planetLocator.PlanetLocatorEntity;
import com.ffsupver.asplor.block.smartMechanicalArm.BeltSmartProcessorEntity;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.BiFunction;

import static com.ffsupver.asplor.AllBlockEntityTypes.*;

public class AllEnergyStorages {


    //注册方块能量存储
    public static void registerEnergyStorages(){
        registerEnergyStorage(
                (blockEntity, direction) -> blockEntity.canTransferEnergy(direction)? blockEntity.energyInventory:null,
                GENERATOR_ENTITY.get());
        registerEnergyStorage(
                (blockEntity, direction) -> blockEntity.canTransferEnergy(direction)?  blockEntity.getExposeStorage():null,
                BATTERY_ENTITY.get()
        );
       registerEnergyStorage(
                (blockEntity,direction)->blockEntity.getEnergyStorage(),
                TIME_INJECTOR_ENTITY.get()
        );

        registerEnergyStorage(
                (blockEntity, direction) -> blockEntity.getEnergyStorage(),
                SPACE_TELEPORTER_ENTITY.get()
        );

        registerEnergyStorage(
                (blockEntity, direction)-> blockEntity.getEnergyStorage(),
                MOTOR_ENTITY.get()
        );
        registerEnergyStorage(
                (blockEntity, direction) -> blockEntity.getEnergyStorage(),
                ELECTROLYZER_ENTITY.get()
        );
        registerEnergyStorage(
                PlanetLocatorEntity::getEnergyStorage,
                PLANET_LOCATOR_ENTITY.get()
        );
        registerEnergyStorage(
                BeltSmartProcessorEntity::getEnergyStorage,
                BELT_SMART_PROCESSOR_ENTITY.get()
        );
        registerEnergyStorage(
                LaserDrillBatteryEntity::getEnergyStorage,
                LASER_DRILL_BATTERY_ENTITY.get()
        );
    }

    private static <T extends BlockEntity> void  registerEnergyStorage(BiFunction<? super T, Direction,@Nullable EnergyStorage> provider, BlockEntityType<T> blockEntityType){
        EnergyStorage.SIDED.registerForBlockEntity(
                provider,
                blockEntityType
        );
    }
    private static <T extends BlockEntity> void  registerEnergyStorage(BlockApiLookup.BlockApiProvider<EnergyStorage, Direction> provider, Block block){
        EnergyStorage.SIDED.registerForBlocks(
                provider,
                block
        );
    }
}
