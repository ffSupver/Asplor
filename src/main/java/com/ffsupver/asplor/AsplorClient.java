package com.ffsupver.asplor;

import com.ffsupver.asplor.block.spaceTeleporter.SpaceTeleporterRenderer;
import com.ffsupver.asplor.compat.create.ponder.PonderIndex;
import com.ffsupver.asplor.entity.client.ModModelLayers;
import com.ffsupver.asplor.fluid.ModFluids;
import com.ffsupver.asplor.networking.ModPackets;
import com.ffsupver.asplor.screen.ModScreenHandlers;
import com.ffsupver.asplor.screen.alloyChest.AlloyChestScreen;
import com.ffsupver.asplor.screen.assembler.AssemblerScreen;
import com.ffsupver.asplor.screen.backpack.BackpackLargeScreen;
import com.ffsupver.asplor.screen.backpack.BackpackSmallScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

public class AsplorClient implements ClientModInitializer {
//    public static final SuperByteBufferCache BUFFER_CACHE = new SuperByteBufferCache();

    @Override
    public void onInitializeClient() {




        ModScreenHandlers.registerModScreens();

        ModFluids.registerRenders();

        ModModelLayers.register();

//        BlockEntityRendererFactories.register(AllBlockEntityTypes.DIVIDER_ENTITY.get(), DividerRenderer2::new);
        BlockEntityRendererFactories.register(AllBlockEntityTypes.SPACE_TELEPORTER_ENTITY.get(), SpaceTeleporterRenderer::new);
//        BUFFER_CACHE.registerCompartment(KineticBlockEntityRenderer.KINETIC_BLOCK);
//        BUFFER_CACHE.registerCompartment(DividerRenderer.DIVIDER);

        AllPartialModels.init();
        AllKeys.register();
        ModPackets.registerS2CPack();
        PonderIndex.register();
    }

}
