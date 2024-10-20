package com.ffsupver.asplor.mixin.moltenMetal;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.ModTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    @Shadow protected abstract boolean shouldSwimInFluids();

    @Shadow public abstract boolean canWalkOnFluid(FluidState state);

    @Shadow public abstract Vec3d applyFluidMovingSpeed(double gravity, boolean falling, Vec3d motion);

    @Shadow protected boolean jumping;

    @Shadow private int jumpingCooldown;

    @Shadow protected abstract void jump();

    @Shadow protected abstract void swimUpward(TagKey<Fluid> fluid);

    @Shadow public abstract void setJumping(boolean jumping);

    @Inject(method = "travel",at = @At(value = "HEAD"), cancellable = true)
    private void travelInMoltenMetal(Vec3d movementInput, CallbackInfo ci){
        FluidState fluidState = this.getWorld().getFluidState(this.getBlockPos());
        if(this.isLogicalSideForUpdatingMovement()){
            double d = 0.08;
            boolean falling = this.getVelocity().y <= 0.0;
            if (this.isInMoltenMetal()&&this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState)) {
                double e = this.getY();
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                Vec3d vec3d3;
                if (this.getFluidHeight(ModTags.Fluids.MOLTEN_METAL) <= this.getSwimHeight()) {
                    this.setVelocity(this.getVelocity().multiply(0.5, 0.800000011920929, 0.5));
                    vec3d3 = this.applyFluidMovingSpeed(d, falling, this.getVelocity());
                    this.setVelocity(vec3d3);
                } else {
                    this.setVelocity(this.getVelocity().multiply(0.5));

                }

                if (!this.hasNoGravity()) {
                    this.setVelocity(this.getVelocity().add(0.0, -d / 4.0, 0.0));
                }

                vec3d3 = this.getVelocity();
                if (this.horizontalCollision && this.doesNotCollide(vec3d3.x, vec3d3.y + 0.6000000238418579 - this.getY() + e, vec3d3.z)) {
                    this.setVelocity(vec3d3.x, 0.30000001192092896, vec3d3.z);
                }
                ci.cancel();
            }
        }
    }

    @Inject(method = "tickMovement",at = @At(value = "INVOKE",target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",ordinal = 2,shift = At.Shift.AFTER))
    private void tickMovementMixin(CallbackInfo ci){
        if (this.jumping && this.shouldSwimInFluids()) {
            double k;
            if (this.isInMoltenMetal()) {
                k = this.getFluidHeight(ModTags.Fluids.MOLTEN_METAL);
            } else {
                k = this.getFluidHeight(FluidTags.WATER);
            }

            boolean bl = this.isTouchingWater() && k > 0.0;
            double l = this.getSwimHeight();
            if (!bl || this.isOnGround() && !(k > l)) {
                if (this.isInMoltenMetal() && (!this.isOnGround() || k > l)) {
                    this.swimUpward(ModTags.Fluids.MOLTEN_METAL);
                    this.setJumping(false);
                }
            }
        }
    }

}
