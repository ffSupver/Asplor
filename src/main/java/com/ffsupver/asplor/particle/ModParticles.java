package com.ffsupver.asplor.particle;

import com.ffsupver.asplor.Asplor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.impl.client.particle.ParticleFactoryRegistryImpl;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final DefaultParticleType LARGE_SMOKE = Registry.register(Registries.PARTICLE_TYPE, new Identifier(Asplor.MOD_ID,"large_smoke"),FabricParticleTypes.simple(true));
    public static final DefaultParticleType LARGE_FLAME = Registry.register(Registries.PARTICLE_TYPE, new Identifier(Asplor.MOD_ID,"large_flame"),FabricParticleTypes.simple(true));
    @Environment(EnvType.CLIENT)
    public static void register(){
        ParticleFactoryRegistryImpl.INSTANCE.register(LARGE_SMOKE,  NoGravityParticle.Provider::new);
        ParticleFactoryRegistryImpl.INSTANCE.register(LARGE_FLAME,  NoGravityParticle.Provider::new);
    }


}
