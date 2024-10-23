package com.ffsupver.asplor.entity.client;

import com.ffsupver.asplor.Asplor;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class Tier0RocketModelLayer {
    public static final Identifier TIER_0_ROCKET_TEXTURE = new Identifier(Asplor.MOD_ID,"textures/entity/rocket/tier_0_rocket.png");
    public static final EntityModelLayer TIER_0_ROCKET_MODEL  = new EntityModelLayer(new Identifier(Asplor.MOD_ID,"tier_0_rocket"),"main");

    public static void register(){
        EntityModelLayerRegistry.registerModelLayer(TIER_0_ROCKET_MODEL, Tier0RocketModel::getTexturedModelData);

    }

}
