package com.ffsupver.asplor.entity.custom;

import com.ffsupver.asplor.planet.PlanetData;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.planets.AdAstraData;
import earth.terrarium.adastra.common.registry.ModEntityTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import java.util.Set;

import static net.minecraft.entity.mob.HostileEntity.isSpawnDark;

public class AstraMob extends MobEntity {
    public AstraMob(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (getWorld().isClient()){
            return;
        }

        Pair<PlanetData.PlanetEnvironment,Planet> dataPair = getPlanet();
        Planet planet = dataPair.getRight();
        PlanetData.PlanetEnvironment planetEnvironment = dataPair.getLeft();
        ServerWorld serverWorld = (ServerWorld) getWorld();

        if (planetEnvironment == null){
            planetEnvironment = PlanetData.PlanetEnvironment.DEFAULT;
        }

        if (planet != null){
            boolean charged = planetEnvironment.charged();
            if (!charged){
                if (planet.oxygen()) {
                    spawnNewEntity(EntityType.ZOMBIE,serverWorld);
                }else {
                    spawnNewEntity(ModEntityTypes.CORRUPTED_LUNARIAN.get(),serverWorld);
                }
            }
        }

        this.discard();
    }

    private Pair<PlanetData.PlanetEnvironment,Planet> getPlanet(){
        RegistryKey<World> worldKey = getWorld().getRegistryKey();
       Planet planet = AdAstraData.getPlanet(worldKey);
        PlanetData.PlanetEnvironment planetEnvironment = PlanetData.getPlanetEnvironment(worldKey);
       return new Pair<>(planetEnvironment,planet);
    }

    private <T extends Entity> void spawnNewEntity(EntityType<T> entityType,ServerWorld serverWorld){
        T t = entityType.spawn(serverWorld,getBlockPos(), SpawnReason.NATURAL);
//        serverWorld.spawnEntity(t);
        if (t != null) {
            t.teleport(serverWorld, getX(), getY(), getZ(), Set.of(), getYaw(), getPitch());
        }
    }

    public static boolean canSpawnInDark(EntityType<? extends MobEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && isSpawnDark(world, pos, random) && canMobSpawn(type, world, spawnReason, pos, random);
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }


}
