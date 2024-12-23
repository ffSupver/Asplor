package com.ffsupver.asplor.entity.custom.cargoRocket;

import earth.terrarium.adastra.common.registry.ModSoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
@Environment(EnvType.CLIENT)
public class CargoRocketSound {
    public static void play(CargoRocketEntity cargoRocketEntity){
        MinecraftClient.getInstance().getSoundManager().play(new CargoRocketSoundInstance(cargoRocketEntity));
    }
    private static class CargoRocketSoundInstance extends MovingSoundInstance {
        private CargoRocketEntity cargoRocketEntity;
        protected boolean canPlay = true;
         public CargoRocketSoundInstance(CargoRocketEntity cargoRocketEntity) {
            super(ModSoundEvents.ROCKET.get(), SoundCategory.AMBIENT, SoundInstance.createRandom());
            this.cargoRocketEntity = cargoRocketEntity;
            this.repeat = true;
        }

        public float getVolume () {
            return this.canPlay ? 10.0F : 0.0F;
        }

        @Override
        public void tick () {
            if (this.cargoRocketEntity.isLaunching() || this.cargoRocketEntity.hasLaunched()) {
                this.x = this.cargoRocketEntity.getX();
                this.y = this.cargoRocketEntity.getY();
                this.z = this.cargoRocketEntity.getZ();
            } else {
                this.setDone();
            }
        }
    }
}
