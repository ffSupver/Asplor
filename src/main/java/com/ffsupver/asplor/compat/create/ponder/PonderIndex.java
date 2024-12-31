package com.ffsupver.asplor.compat.create.ponder;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.compat.create.ponder.scenes.*;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import com.simibubi.create.infrastructure.ponder.scenes.BearingScenes;
import com.simibubi.create.infrastructure.ponder.scenes.ProcessingScenes;

import static com.ffsupver.asplor.AllBlocks.*;
import static com.ffsupver.asplor.item.ModItems.*;

public class PonderIndex {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(Asplor.MOD_ID);
    static final PonderRegistrationHelper CREATE_HELPER = new PonderRegistrationHelper(Create.ID);

    public static void register() {
        HELPER.forComponents(TIME_INJECTOR)
                .addStoryBoard("time_injector/work", TimeInjectorScenes::TimeInjector);
        HELPER.forComponents(PACKER)
                .addStoryBoard("packer/packer", PackerScenes::Packer);
        HELPER.forComponents(SPACE_TELEPORTER)
                .addStoryBoard("space_teleporter/teleport", SpaceTeleporterScenes::teleport);
        HELPER.forComponents(MELTING_FURNACE)
                .addStoryBoard("melting_furnace/melting_furnace", MeltingFurnaceScenes::meltFurnace)
                .addStoryBoard("melting_furnace/heat_level", MeltingFurnaceScenes::heatLevel);
        HELPER.forComponents(REFINERY_CONTROLLER,REFINERY_OUTPUT,REFINERY_INPUT)
                .addStoryBoard("refinery/build", RefineryScenes::build)
                .addStoryBoard("refinery/use", RefineryScenes::use)
                .addStoryBoard("refinery/output_count", RefineryScenes::outputCount);
        HELPER.forComponents(SMART_MECHANICAL_ARM,TOOL_GEAR,ALLOY_DEPOT)
                .addStoryBoard("smart_mechanical_arm/smart_mechanical_arm",SmartMechanicalArmScenes::use)
                        .addStoryBoard("smart_mechanical_arm/schematic",SmartMechanicalArmScenes::schematic);
        HELPER.forComponents(CHUNK_LOADER)
                .addStoryBoard("chunk_loader/chunk_loader",ChunkLoaderScenes::chunkLoader);
        HELPER.forComponents(LARGE_MELTING_FURNACE_CONTROLLER,LARGE_MELTING_FURNACE_FLUID_PORT,LARGE_MELTING_FURNACE_ITEM_PORT)
                .addStoryBoard("melting_furnace/large_melting_furnace",MeltingFurnaceScenes::largeMeltingFurnace);
        HELPER.forComponents(ROCKET_CARGO_LOADER,ROCKET_FUEL_LOADER)
                .addStoryBoard("cargo_rocket/use", CargoRocketScenes::use);


        CREATE_HELPER.forComponents(AllBlocks.ALLOY_MECHANICAL_PRESS)
                .addStoryBoard("mechanical_press/pressing", ProcessingScenes::pressing)
                .addStoryBoard("mechanical_press/compacting", ProcessingScenes::compacting);
        CREATE_HELPER.forComponents(AllBlocks.WINDMILL_BEARING)
                .addStoryBoard("windmill_bearing/source", BearingScenes::windmillsAsSource, AllPonderTags.KINETIC_SOURCES)
                .addStoryBoard("windmill_bearing/structure", BearingScenes::windmillsAnyStructure,
                        AllPonderTags.MOVEMENT_ANCHOR);

    }
}
