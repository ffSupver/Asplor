package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.entity.ModEntities;
import earth.terrarium.adastra.client.renderers.entities.vehicles.RocketRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import static com.ffsupver.asplor.entity.client.CargoRocketRenderer.CARGO_ROCKET_MODEL;

public class ModModelLayers {

    public static void register(){
        Tier0RocketModelLayer.register();
        EntityModelLayerRegistry.registerModelLayer(CARGO_ROCKET_MODEL, CargoRocketModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.ALLOY_CHEST,AlloyChestRenderer::new);
        EntityRendererRegistry.register(ModEntities.TIER_0_ROCKET, (c)->new RocketRenderer(c, Tier0RocketModelLayer.TIER_0_ROCKET_MODEL, Tier0RocketModelLayer.TIER_0_ROCKET_TEXTURE));
        EntityRendererRegistry.register(ModEntities.CARGO_ROCKET, CargoRocketRenderer::new);


    }
}
