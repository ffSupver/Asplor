package com.ffsupver.asplor.planet;

import com.ffsupver.asplor.ModDamages;
import com.ffsupver.asplor.sound.ModSounds;
import earth.terrarium.adastra.api.systems.OxygenApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;


public class EnvironmentTicks {
    public static void allTicks(LivingEntity entity, ServerWorld world){
        if (PlanetData.isCharged(world.getRegistryKey())){
            chargeTick(entity,world);
        }
    }
    private static void chargeTick(LivingEntity entity, ServerWorld world){
        if (entity.age % 20 == 0 && hasOxygen(entity,world)){
            boolean damaged = entity.damage(ModDamages.charge(world), 2.0f);
            if (damaged && entity instanceof PlayerEntity player && !(player.isCreative() && player.isSpectator())) {
                world.playSoundFromEntity(null,entity,ModSounds.ELECTRICITY_WORK,SoundCategory.AMBIENT,10f,1f);
            }
        }
    }

    private static boolean hasOxygen(LivingEntity entity,ServerWorld world){
        return OxygenApi.API.hasOxygen(world,entity.getBlockPos());
    }
}
