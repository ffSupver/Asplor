package com.ffsupver.asplor.compat.create.ponder;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.compat.create.ponder.scenes.*;
import com.ffsupver.asplor.item.ModItems;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import com.simibubi.create.infrastructure.ponder.scenes.BearingScenes;
import com.simibubi.create.infrastructure.ponder.scenes.ProcessingScenes;

public class PonderIndex {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(Asplor.MOD_ID);
    static final PonderRegistrationHelper CREATE_HELPER = new PonderRegistrationHelper(Create.ID);

    public static void register() {
        HELPER.forComponents(AllBlocks.TIME_INJECTOR)
                .addStoryBoard("time_injector/work", TimeInjectorScenes::TimeInjector);
        HELPER.forComponents(ModItems.PACKER)
                .addStoryBoard("packer/packer", PackerScenes::Packer);
        HELPER.forComponents(AllBlocks.SPACE_TELEPORTER)
                .addStoryBoard("space_teleporter/teleport", SpaceTeleporterScenes::teleport);
        HELPER.forComponents(AllBlocks.MELTING_FURNACE)
                .addStoryBoard("melting_furnace/melting_furnace", MeltingFurnaceScenes::meltFurnace)
                .addStoryBoard("melting_furnace/heat_level", MeltingFurnaceScenes::heatLevel);
        HELPER.forComponents(AllBlocks.REFINERY_CONTROLLER,AllBlocks.REFINERY_OUTPUT,AllBlocks.REFINERY_INPUT)
                .addStoryBoard("refinery/build", RefineryScenes::build)
                .addStoryBoard("refinery/use", RefineryScenes::use)
                .addStoryBoard("refinery/output_count", RefineryScenes::outputCount);
        HELPER.forComponents(AllBlocks.SMART_MECHANICAL_ARM,AllBlocks.TOOL_GEAR)
                        .addStoryBoard("smart_mechanical_arm/smart_mechanical_arm",SmartMechanicalArmScenes::use);
        HELPER.forComponents(AllBlocks.CHUNK_LOADER)
                .addStoryBoard("chunk_loader/chunk_loader",ChunkLoaderScenes::chunkLoader);


        CREATE_HELPER.forComponents(AllBlocks.ALLOY_MECHANICAL_PRESS)
                .addStoryBoard("mechanical_press/pressing", ProcessingScenes::pressing)
                .addStoryBoard("mechanical_press/compacting", ProcessingScenes::compacting);
        CREATE_HELPER.forComponents(AllBlocks.WINDMILL_BEARING)
                .addStoryBoard("windmill_bearing/source", BearingScenes::windmillsAsSource, AllPonderTags.KINETIC_SOURCES)
                .addStoryBoard("windmill_bearing/structure", BearingScenes::windmillsAnyStructure,
                        AllPonderTags.MOVEMENT_ANCHOR);

    }
}
