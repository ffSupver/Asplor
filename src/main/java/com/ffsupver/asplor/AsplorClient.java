package com.ffsupver.asplor;

import com.ffsupver.asplor.block.spaceTeleporter.SpaceTeleporterRenderer;
import com.ffsupver.asplor.compat.create.ponder.PonderIndex;
import com.ffsupver.asplor.entity.client.ModModelLayers;
import com.ffsupver.asplor.fluid.ModFluids;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.networking.ModPackets;
import com.ffsupver.asplor.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class AsplorClient implements ClientModInitializer {
//    public static final SuperByteBufferCache BUFFER_CACHE = new SuperByteBufferCache();

    @Override
    public void onInitializeClient() {


        AllBlocks.registerRenderLayer();

        ModScreenHandlers.registerModScreens();

        ModFluids.registerRenders();

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
