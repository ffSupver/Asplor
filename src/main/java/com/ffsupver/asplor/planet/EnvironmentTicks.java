package com.ffsupver.asplor.planet;

import com.ffsupver.asplor.ModDamages;
import earth.terrarium.adastra.api.systems.OxygenApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;


public class EnvironmentTicks {
    public static void allTicks(LivingEntity entity, ServerWorld world){
        if (PlanetData.isCharged(world.getRegistryKey())){
            chargeTick(entity,world);
        }
    }
    private static void chargeTick(LivingEntity entity, ServerWorld world){
        if (entity.age % 20 == 0 && !hasOxygen(entity,world)){
            entity.damage(ModDamages.charge(world), 2.0f);
        }
    }

    private static boolean hasOxygen(LivingEntity entity,ServerWorld world){
        return OxygenApi.API.hasOxygen(world,entity.getBlockPos());
    }
}
