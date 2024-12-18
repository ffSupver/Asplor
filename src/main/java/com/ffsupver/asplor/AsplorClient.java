package com.ffsupver.asplor;

import com.ffsupver.asplor.block.spaceTeleporter.SpaceTeleporterRenderer;
import com.ffsupver.asplor.compat.create.ponder.PonderIndex;
import com.ffsupver.asplor.entity.client.ModModelLayers;
import com.ffsupver.asplor.fluid.ModFluids;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.networking.ModPackets;
import com.ffsupver.asplor.screen.ModScreenHandlers;
import com.simibubi.create.compat.Mods;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class AsplorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {




        AllBlocks.registerRender();

        ModScreenHandlers.registerModScreens();

        ModFluids.registerRenders();
        Mods.SODIUM.executeIfInstalled(()-> ModFluids::registerRenders);

        ModModelLayers.register();

        ModItems.registerItemClient();


        BlockEntityRendererFactories.register(AllBlockEntityTypes.SPACE_TELEPORTER_ENTITY.get(), SpaceTeleporterRenderer::new);



        AllPartialModels.init();
        AllKeys.register();
        PonderIndex.register();
        ModPackets.registerS2CPack();

    }

}
