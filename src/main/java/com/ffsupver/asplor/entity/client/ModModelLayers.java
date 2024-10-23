package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.entity.ModEntities;
import earth.terrarium.adastra.client.renderers.entities.vehicles.RocketRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ModModelLayers {

    public static void register(){
        Tier0RocketModelLayer.register();

        EntityRendererRegistry.register(ModEntities.ALLOY_CHEST,AlloyChestRenderer::new);
        EntityRendererRegistry.register(ModEntities.TIER_0_ROCKET, (c)->new RocketRenderer(c, Tier0RocketModelLayer.TIER_0_ROCKET_MODEL, Tier0RocketModelLayer.TIER_0_ROCKET_TEXTURE));

    }
}
