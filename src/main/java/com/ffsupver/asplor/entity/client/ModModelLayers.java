package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.entity.ModEntities;
import earth.terrarium.adastra.client.models.armor.SpaceSuitModel;
import earth.terrarium.adastra.client.renderers.entities.vehicles.RocketRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static com.ffsupver.asplor.entity.ModEntities.ZOMBIFIED_COSMONAUT;
import static com.ffsupver.asplor.entity.client.AdvanceRocketRenderer.ADVANCE_ROCKET_MODEL;
import static com.ffsupver.asplor.entity.client.CargoRocketRenderer.CARGO_ROCKET_MODEL;
import static com.ffsupver.asplor.entity.client.ZombifiedCosmonautRenderer.ZOMBIFIED_COSMONAUT_SPACE_SUIT;

public class ModModelLayers {

    public static void register(){
        Tier0RocketModelLayer.register();
        EntityModelLayerRegistry.registerModelLayer(CARGO_ROCKET_MODEL, CargoRocketModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ADVANCE_ROCKET_MODEL, AdvanceRocketModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ZOMBIFIED_COSMONAUT_SPACE_SUIT, SpaceSuitModel::createSpaceSuitLayer);

        EntityRendererRegistry.register(ModEntities.ALLOY_CHEST,AlloyChestRenderer::new);
        EntityRendererRegistry.register(ModEntities.TIER_0_ROCKET, (c)->new RocketRenderer(c, Tier0RocketModelLayer.TIER_0_ROCKET_MODEL, Tier0RocketModelLayer.TIER_0_ROCKET_TEXTURE));
        EntityRendererRegistry.register(ModEntities.CARGO_ROCKET, CargoRocketRenderer::new);

        EntityRendererRegistry.register(ModEntities.RANGER,RangerRenderer::new);
        EntityRendererRegistry.register(ModEntities.ADVANCE_ROCKET, AdvanceRocketRenderer::new);

        EntityRendererRegistry.register(ModEntities.METEORITE,MeteoriteRenderer::new);

        EntityRendererRegistry.register(ModEntities.ASTRA_MOB, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(ZOMBIFIED_COSMONAUT, ZombifiedCosmonautRenderer::new);
    }

    public static EntityModelLayer createEntityModelLayer(String id,String name){
       return new EntityModelLayer(new Identifier(Asplor.MOD_ID,id),name);
    }
}
