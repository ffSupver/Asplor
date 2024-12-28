package com.ffsupver.asplor.planet;

import com.ffsupver.asplor.ModDamages;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;


public class EnvironmentTicks {
    public static void allTicks(LivingEntity entity, ServerWorld world){
        if (PlanetData.isCharged(world.getRegistryKey())){
            chargeTick(entity,world);
        }
    }
    private static void chargeTick(LivingEntity entity, ServerWorld world){
        if (entity.age % 20 == 0){
            entity.damage(ModDamages.charge(world), 2.0f);
        }
    }
}
