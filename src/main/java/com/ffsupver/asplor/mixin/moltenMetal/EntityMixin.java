package com.ffsupver.asplor.mixin.moltenMetal;

import com.ffsupver.asplor.ModTags;
import com.ffsupver.asplor.ModDamages;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract World getWorld();

    @Shadow protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

    @Shadow public abstract boolean updateMovementInFluid(TagKey<Fluid> tag, double speed);

    @Shadow protected boolean firstUpdate;


    @Shadow public abstract boolean isFireImmune();

    @Shadow public abstract void setOnFireFor(int seconds);

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract World getEntityWorld();

    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Shadow @Final protected Random random;

    @Shadow public abstract boolean isLogicalSideForUpdatingMovement();

    @Shadow public abstract BlockPos getBlockPos();

    @Shadow public abstract Vec3d getVelocity();

    @Shadow public abstract double getY();

    @Shadow public abstract void updateVelocity(float speed, Vec3d movementInput);

    @Shadow public abstract void move(MovementType movementType, Vec3d movement);

    @Shadow public abstract double getFluidHeight(TagKey<Fluid> fluid);

    @Shadow public abstract double getSwimHeight();

    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow public abstract boolean hasNoGravity();

    @Shadow public boolean horizontalCollision;

    @Shadow public abstract boolean doesNotCollide(double offsetX, double offsetY, double offsetZ);

    @Shadow public abstract void setVelocity(double x, double y, double z);

    @Shadow public abstract boolean isTouchingWater();

    @Shadow public abstract boolean isOnGround();

    @Inject(method = "baseTick",at = @At(value = "TAIL"))
    private void baseTick$moltenDamage(CallbackInfo ci){
        if (!this.getWorld().isClient) {
            if(isInMoltenMetal()){
                if (!this.isFireImmune()) {
                    this.setOnFireFor(15);
                    if (this.damage(ModDamages.molten((ServerWorld) this.getEntityWorld()), 6.0F)) {
                        this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
                    }

                }
            }
        }
    }

    @Inject(method = "baseTick",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/Entity;updateWaterState()Z",shift = At.Shift.AFTER))
    private void baseTick$updateMoltenFluid(CallbackInfo ci){
        double speed = this.getWorld().getDimension().ultrawarm() ? 0.01 : 0.004;
        boolean b = this.updateMovementInFluid(ModTags.Fluids.MOLTEN_METAL, speed);
    }

    @Unique
    public boolean isInMoltenMetal(){
        return  !this.firstUpdate && this.fluidHeight.getDouble(ModTags.Fluids.MOLTEN_METAL) > 0.0;
    }
}
