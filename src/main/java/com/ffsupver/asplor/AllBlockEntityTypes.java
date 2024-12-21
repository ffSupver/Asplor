package com.ffsupver.asplor;

import com.ffsupver.asplor.block.airlockSwitch.AirlockSwitchEntity;
import com.ffsupver.asplor.block.alloyChest.AlloyChestEntity;
import com.ffsupver.asplor.block.alloyMechanicalPress.AlloyMechanicalPressEntity;
import com.ffsupver.asplor.block.alloyMechanicalPress.AlloyMechanicalPressInstance;
import com.ffsupver.asplor.block.alloyMechanicalPress.AlloyMechanicalPressRenderer;
import com.ffsupver.asplor.block.battery.BatteryEntity;
import com.ffsupver.asplor.block.chunkLoader.ChunkLoaderEntity;
import com.ffsupver.asplor.block.chunkLoader.ChunkLoaderRenderer;
import com.ffsupver.asplor.block.divider.DividerEntity;
import com.ffsupver.asplor.block.divider.DividerInstance;
import com.ffsupver.asplor.block.divider.DividerRenderer;
import com.ffsupver.asplor.block.electrolyzer.ElectrolyzerEntity;
import com.ffsupver.asplor.block.electrolyzer.ElectrolyzerRenderer;
import com.ffsupver.asplor.block.energyOutputer.EnergyOutputEntity;
import com.ffsupver.asplor.block.generator.GeneratorEntity;
import com.ffsupver.asplor.block.generator.GeneratorInstance;
import com.ffsupver.asplor.block.generator.GeneratorRenderer;
import com.ffsupver.asplor.block.liquid_blaze_burner.LiquidBlazeBurnerEntity;
import com.ffsupver.asplor.block.liquid_blaze_burner.LiquidBlazeBurnerRenderer;
import com.ffsupver.asplor.block.mechanicalPump.MechanicalPumpEntity;
import com.ffsupver.asplor.block.mechanicalPump.MechanicalPumpInstance;
import com.ffsupver.asplor.block.meltingFurnace.*;
import com.ffsupver.asplor.block.motor.MotorEntity;
import com.ffsupver.asplor.block.motor.MotorInstance;
import com.ffsupver.asplor.block.motor.MotorRenderer;
import com.ffsupver.asplor.block.refinery.RefineryControllerEntity;
import com.ffsupver.asplor.block.refinery.RefineryControllerRenderer;
import com.ffsupver.asplor.block.refinery.RefineryInputEntity;
import com.ffsupver.asplor.block.refinery.RefineryOutputEntity;
import com.ffsupver.asplor.block.rocketCargoLoader.RocketCargoLoaderEntity;
import com.ffsupver.asplor.block.rocketFuelLoader.RocketFuelLoaderEntity;
import com.ffsupver.asplor.block.rocketFuelLoader.RocketFuelLoaderRenderer;
import com.ffsupver.asplor.block.smartMechanicalArm.*;
import com.ffsupver.asplor.block.spaceTeleporter.SpaceTeleporterEntity;
import com.ffsupver.asplor.block.spaceTeleporter.SpaceTeleporterRenderer;
import com.ffsupver.asplor.block.theNetherReturner.TheNetherReturnerEntity;
import com.ffsupver.asplor.block.theNetherReturner.TheNetherReturnerRenderer;
import com.ffsupver.asplor.block.timeInjector.TimeInjectorEntity;
import com.ffsupver.asplor.block.timeInjector.TimeInjectorRenderer;
import com.ffsupver.asplor.block.windmill.WindmillBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.BearingInstance;
import com.simibubi.create.content.contraptions.bearing.BearingRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.ffsupver.asplor.AllBlocks.*;
import static com.ffsupver.asplor.Asplor.REGISTRATE;

public class AllBlockEntityTypes {
    public static final BlockEntityEntry<DividerEntity> DIVIDER_ENTITY = REGISTRATE
            .blockEntity("divider", DividerEntity::new)
            .instance(() -> DividerInstance::new, false)
            .validBlocks(DIVIDER)
            .renderer(() -> DividerRenderer::new)
            .register();
    public static final BlockEntityEntry<AlloyMechanicalPressEntity> ALLOY_MECHANICAL_PRESS_ENTITY = REGISTRATE
            .blockEntity("alloy_mechanical_press", AlloyMechanicalPressEntity::new)
            .instance(() -> AlloyMechanicalPressInstance::new)
            .validBlocks(ALLOY_MECHANICAL_PRESS)
            .renderer(() -> AlloyMechanicalPressRenderer::new)
            .register();

    public static final BlockEntityEntry<LiquidBlazeBurnerEntity> LIQUID_BLAZE_BURNER_ENTITY =REGISTRATE
            .blockEntity("liquid_blaze_burner", LiquidBlazeBurnerEntity::new)
            .validBlocks(AllBlocks.LIQUID_BLAZE_BURNER)
            .renderer(() -> LiquidBlazeBurnerRenderer::new)
            .register();

    public static final BlockEntityEntry<GeneratorEntity> GENERATOR_ENTITY = REGISTRATE
            .blockEntity("generator", GeneratorEntity::new)
            .instance(() -> GeneratorInstance::new, false)
            .validBlocks(GENERATOR)
            .renderer(() -> GeneratorRenderer::new)
            .register();

public static final BlockEntityEntry<BatteryEntity> BATTERY_ENTITY= REGISTRATE
        .blockEntity("battery",BatteryEntity::new)
        .validBlocks(BATTERY)
        .register();

    public static final BlockEntityEntry<TimeInjectorEntity> TIME_INJECTOR_ENTITY= REGISTRATE
            .blockEntity("time_injector",TimeInjectorEntity::new)
            .validBlocks(TIME_INJECTOR)
            .renderer(()->TimeInjectorRenderer::new)
            .register();

    public static final BlockEntityEntry<SpaceTeleporterEntity> SPACE_TELEPORTER_ENTITY = REGISTRATE
            .blockEntity("space_teleporter",SpaceTeleporterEntity::new)
            .validBlocks(SPACE_TELEPORTER)
            .renderer(()->SpaceTeleporterRenderer::new)
            .register();



    public static final BlockEntityEntry<MotorEntity> MOTOR_ENTITY = REGISTRATE
            .blockEntity("motor", MotorEntity::new)
            .instance(() -> MotorInstance::new, false)
            .validBlocks(MOTOR)
            .renderer(() -> MotorRenderer::new)
            .register();

    public static final BlockEntityEntry<MechanicalPumpEntity> MECHANICAL_PUMP_ENTITY = REGISTRATE
            .blockEntity("mechanical_pump", MechanicalPumpEntity::new)
            .instance(() -> MechanicalPumpInstance::new, false)
            .validBlocks(MECHANICAL_PUMP)
            .register();

    public static final BlockEntityEntry<MeltingFurnaceEntity> MELTING_FURNACE_ENTITY = REGISTRATE
            .blockEntity("melting_furnace", MeltingFurnaceEntity::new)
            .validBlocks(MELTING_FURNACE)
            .renderer(()-> MeltingFurnaceRenderer::new)
            .register();

    public static final BlockEntityEntry<ElectrolyzerEntity> ELECTROLYZER_ENTITY = REGISTRATE
            .blockEntity("electrolyzer",ElectrolyzerEntity::new)
            .validBlocks(ELECTROLYZER)
            .renderer(()-> ElectrolyzerRenderer::new)
            .register();
    public static final BlockEntityEntry<WindmillBearingBlockEntity> WINDMILL_BEARING = REGISTRATE
            .blockEntity("windmill_bearing", WindmillBearingBlockEntity::new)
            .instance(() -> BearingInstance::new)
            .validBlocks(AllBlocks.WINDMILL_BEARING)
		    .renderer(() -> BearingRenderer::new)
            .register();

    public static final BlockEntityEntry<TheNetherReturnerEntity> THE_NETHER_RETURNER_ENTITY = REGISTRATE
            .blockEntity("the_nether_returner", TheNetherReturnerEntity::new)
            .validBlocks(THE_NETHER_RETURNER)
            .renderer(()-> TheNetherReturnerRenderer::new)
            .register();
    public static final BlockEntityEntry<RefineryControllerEntity> REFINERY_CONTROLLER_ENTITY = REGISTRATE
            .blockEntity("refinery_controller", RefineryControllerEntity::new)
            .validBlocks(REFINERY_CONTROLLER)
            .renderer(()-> RefineryControllerRenderer::new)
            .register();
    public static final BlockEntityEntry<RefineryOutputEntity> REFINERY_OUTPUT_ENTITY = REGISTRATE
            .blockEntity("refinery_output", RefineryOutputEntity::new)
            .validBlocks(REFINERY_OUTPUT)
            .register();
    public static final BlockEntityEntry<RefineryInputEntity> REFINERY_INPUT_ENTITY = REGISTRATE
            .blockEntity("refinery_input", RefineryInputEntity::new)
            .validBlocks(REFINERY_INPUT)
            .register();
    public static final BlockEntityEntry<SmartMechanicalArmEntity> SMART_MECHANICAL_ARM_ENTITY = REGISTRATE
            .blockEntity("smart_mechanical_arm", SmartMechanicalArmEntity::new)
            .instance(() -> SmartMechanicalArmInstance::new)
            .renderer(()-> SmartMechanicalArmRenderer::new)
            .validBlocks(SMART_MECHANICAL_ARM)
            .register();
    public static final BlockEntityEntry<ToolGearEntity> TOOL_GEAR_ENTITY = REGISTRATE
            .blockEntity("tool_gear",ToolGearEntity::new)
            .validBlocks(TOOL_GEAR)
            .renderer(()-> ToolGearRenderer::new)
            .register();

    public static final BlockEntityEntry<ChunkLoaderEntity> CHUNK_LOADER_ENTITY = REGISTRATE
            .blockEntity("chunk_loader",ChunkLoaderEntity::new)
            .validBlocks(CHUNK_LOADER)
            .renderer(()-> ChunkLoaderRenderer::new)
            .register();
    public static final BlockEntityEntry<LargeMeltingFurnaceControllerEntity> LARGE_MELTING_FURNACE_CONTROLLER_ENTITY = REGISTRATE
            .blockEntity("large_melting_furnace_controller", LargeMeltingFurnaceControllerEntity::new)
            .validBlocks(LARGE_MELTING_FURNACE_CONTROLLER)
            .renderer(()-> LargeMeltingFurnaceControllerRenderer::new)
            .register();
    public static final BlockEntityEntry<LargeMeltingFurnaceFluidPortEntity> LARGE_MELTING_FURNACE_FLUID_PORT_ENTITY = REGISTRATE
            .blockEntity("large_melting_furnace_fluid_port", LargeMeltingFurnaceFluidPortEntity::new)
            .validBlocks(LARGE_MELTING_FURNACE_FLUID_PORT)
            .register();
    public static final BlockEntityEntry<LargeMeltingFurnaceItemPortEntity> LARGE_MELTING_FURNACE_ITEM_PORT_ENTITY = REGISTRATE
            .blockEntity("large_melting_item_fluid_port", LargeMeltingFurnaceItemPortEntity::new)
            .validBlocks(LARGE_MELTING_FURNACE_ITEM_PORT)
            .register();

    public static final BlockEntityEntry<RocketFuelLoaderEntity> ROCKET_FUEL_LOADER_ENTITY = REGISTRATE
            .blockEntity("rocket_fuel_loader", RocketFuelLoaderEntity::new)
            .validBlocks(ROCKET_FUEL_LOADER)
            .renderer(()-> RocketFuelLoaderRenderer::new)
            .register();
    public static final BlockEntityEntry<RocketCargoLoaderEntity> ROCKET_CARGO_LOADER_ENTITY = REGISTRATE
            .blockEntity("rocket_cargo_loader", RocketCargoLoaderEntity::new)
            .validBlocks(ROCKET_CARGO_LOADER)
//            .renderer(()-> RocketCargoLoaderEntity::new)
            .register();

    //注册普通方块实体
    public static final BlockEntityType<AlloyChestEntity> ALLOY_CHEST_BLOCK_ENTITY=
            Registry.register(Registries.BLOCK_ENTITY_TYPE,new Identifier(Asplor.MOD_ID,"alloy_chest_block_entity"),
                    FabricBlockEntityTypeBuilder.create(AlloyChestEntity::new, AllBlocks.ALLOY_CHEST).build());

    public static final BlockEntityType<EnergyOutputEntity> ENERGY_OUTPUT_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,new Identifier(Asplor.MOD_ID,"energy_output"),
                    FabricBlockEntityTypeBuilder.create(EnergyOutputEntity::new, ENERGY_OUTPUT).build());
    public static final BlockEntityType<AirlockSwitchEntity> AIRLOCK_SWITCH_ENTITY = registerBaseBlockEntityType("airlock_switch", AirlockSwitchEntity::new);



    public static void register(){
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBaseBlockEntityType(String name, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block){
        return Registry.register(Registries.BLOCK_ENTITY_TYPE,new Identifier(Asplor.MOD_ID,name),
                FabricBlockEntityTypeBuilder.create(factory, block).build());
    }
    public static <T extends BlockEntity> BlockEntityType<T> registerBaseBlockEntityType(String name, FabricBlockEntityTypeBuilder.Factory<T> factory){
        return Registry.register(Registries.BLOCK_ENTITY_TYPE,new Identifier(Asplor.MOD_ID,name),
                FabricBlockEntityTypeBuilder.create(factory).build());
    }
}
