package com.ffsupver.asplor.particle;

import net.minecraft.client.particle.ExplosionSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class NoGravityParticle extends ExplosionSmokeParticle {
    protected NoGravityParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
        this.gravityStrength = 0f;
    }


    public static class Provider implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Provider(SpriteProvider sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(DefaultParticleType type, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new NoGravityParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
