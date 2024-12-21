package com.ffsupver.asplor.mixin.cargoRocket;

import com.ffsupver.asplor.entity.custom.CargoRocketEntity;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BipedEntityModel.class})
public abstract class SittingModelMixin<T extends LivingEntity> extends AnimalModel<T> {
    public SittingModelMixin() {
    }

    @Inject(
            method = {"setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V"},
            at = {@At("HEAD")}
    )
    private void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        Entity var10 = entity.getVehicle();
        if (var10 instanceof CargoRocketEntity) {
            this.riding = false;
        }
    }
}
