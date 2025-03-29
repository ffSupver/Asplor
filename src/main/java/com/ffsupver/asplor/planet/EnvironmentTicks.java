package com.ffsupver.asplor.planet;

import earth.terrarium.adastra.api.systems.OxygenApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;


public class EnvironmentTicks {
    public static void allTicks(LivingEntity entity, ServerWorld world){
        if (PlanetData.isCharged(world.getRegistryKey())){
            Charged.tick(entity,world);
        }
    }


    public static boolean hasOxygen(LivingEntity entity,ServerWorld world){
        return OxygenApi.API.hasOxygen(world,entity.getBlockPos());
    }
}
