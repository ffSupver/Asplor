package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.entity.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ModModelLayers {

    public static void register(){

        EntityRendererRegistry.register(ModEntities.ALLOY_CHEST,AlloyChestRenderer::new);
    }
}
