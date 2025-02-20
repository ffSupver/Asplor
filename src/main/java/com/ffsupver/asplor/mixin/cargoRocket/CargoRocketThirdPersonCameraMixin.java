package com.ffsupver.asplor.mixin.cargoRocket;

import com.ffsupver.asplor.entity.custom.rocket.CargoRocketEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Camera.class})

public abstract class CargoRocketThirdPersonCameraMixin {
    @Shadow protected abstract void moveBy(double x, double y, double z);

    @Shadow protected abstract double clipToSpace(double desiredCameraDistance);

    @Inject(
            method = {"update"},
            at = {@At("TAIL")}
    )
    public void zoomInThirdPerson(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (thirdPerson) {
            Entity var8 = focusedEntity.getVehicle();
            if (var8 instanceof CargoRocketEntity) {

                    this.moveBy(-this.clipToSpace(12.0), 0.0, 0.0);

            }
        }

    }
}
