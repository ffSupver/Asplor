package com.ffsupver.asplor.mixin;

import com.ffsupver.asplor.ModTags;
import com.ffsupver.asplor.planet.EnvironmentTicks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.StreamSupport;


@Mixin({LivingEntity.class})
public abstract class EnvironmentTick extends Entity {
    public EnvironmentTick(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick",
            at = {@At("TAIL")})
    public void environmentTick(CallbackInfo ci) {
        Entity entity = this;
        World world = getWorld();
        if (world instanceof ServerWorld serverWorld) {
            if (entity instanceof LivingEntity livingEntity){
                if(!StreamSupport.stream(entity.getArmorItems().spliterator(), false).allMatch((stack) ->
                        stack.isIn(ModTags.Items.CHARGE_PROOF))){
                    EnvironmentTicks.allTicks(livingEntity, serverWorld);
                }
            }
        }
    }
}
