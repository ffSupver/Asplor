package com.ffsupver.asplor.mixin.lightningAbsorber;

import com.ffsupver.asplor.block.lightningAbsorber.LightningAbsorber;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin extends Entity {
    public LightningEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow protected abstract BlockPos getAffectedBlockPos();

    @Inject(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/LightningEntity;powerLightningRod()V"))
    public void tick(CallbackInfo ci){
        BlockPos blockPos = this.getAffectedBlockPos();
        BlockState blockState = this.getWorld().getBlockState(blockPos);
        if (blockState.getBlock() instanceof LightningAbsorber lightningAbsorber) {
            lightningAbsorber.onLightningStuck(this.getWorld(),blockPos);
        }
    }
}
