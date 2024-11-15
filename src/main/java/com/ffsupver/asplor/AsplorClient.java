package com.ffsupver.asplor;

import com.ffsupver.asplor.block.spaceTeleporter.SpaceTeleporterRenderer;
import com.ffsupver.asplor.compat.create.ponder.PonderIndex;
import com.ffsupver.asplor.entity.client.ModModelLayers;
import com.ffsupver.asplor.fluid.ModFluids;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.networking.ModPackets;
import com.ffsupver.asplor.screen.ModScreenHandlers;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.foundation.utility.ModelSwapper;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class AsplorClient implements ClientModInitializer {
public static final ModelSwapper MODEL_SWAPPER = new ModelSwapper();
    @Override
    public void onInitializeClient() {

//        MODEL_SWAPPER.registerListeners();


        AllBlocks.registerRender();

        ModScreenHandlers.registerModScreens();

        ModFluids.registerRenders();
        Mods.SODIUM.executeIfInstalled(()-> ModFluids::registerRenders);

        ModModelLayers.register();

        ModItems.registerRocketItemRender();

//        BlockEntityRendererFactories.register(AllBlockEntityTypes.DIVIDER_ENTITY.get(), DividerRenderer2::new);
        BlockEntityRendererFactories.register(AllBlockEntityTypes.SPACE_TELEPORTER_ENTITY.get(), SpaceTeleporterRenderer::new);
//        BUFFER_CACHE.registerCompartment(KineticBlockEntityRenderer.KINETIC_BLOCK);
//        BUFFER_CACHE.registerCompartment(DividerRenderer.DIVIDER);

        AllPartialModels.init();
        AllKeys.register();
        PonderIndex.register();
        ModPackets.registerS2CPack();
    }

}
