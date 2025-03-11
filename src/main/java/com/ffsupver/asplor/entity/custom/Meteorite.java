package com.ffsupver.asplor.entity.custom;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.particle.ModParticles;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.api.systems.GravityApi;
import earth.terrarium.adastra.common.planets.AdAstraData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class Meteorite extends Entity {
    private static TrackedData<Float> ROTATION_ANGLE;
    private float rotationAxis;
    private float rotationSpeed;
    public Meteorite(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = false;
        this.rotationAxis = world.getRandom().nextFloat() * 360f;
        this.rotationSpeed = world.getRandom().nextFloat() * 5f;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasNoGravity()){
            tickGravity();
        }

        this.move(MovementType.SELF, this.getVelocity());

        if (getVelocity().isInRange(Vec3d.ZERO,0.01f) && hasBlockBelow()){
            explosion();
            discard();
        }

        if (getWorld().isClient()){
            Planet planet = AdAstraData.getPlanet(getWorld().getRegistryKey());
            if (planet.oxygen()) {
                spawnParticle();
            }
        }

        rotate();
    }

    private void rotate(){
        float r = getRotation();
        r = r < 360 ? r + rotationSpeed : 0;
        setRotation(r);
    }

    private void spawnParticle(){
        spawnParticle(ModParticles.LARGE_FLAME,-0.03f, 0.03f,20);
        spawnParticle(ModParticles.LARGE_SMOKE,-0.05f,0.05f,10);
        spawnParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,-0.01f,0.01f,8);

    }
    private void spawnParticle(ParticleEffect particleEffect,float min,float max,int count){
        for(int i = 0; i < count; ++i){
            this.getWorld().addParticle(particleEffect,
                    this.getX(), this.getY() + getWidth()/2, this.getZ(),
                    MathHelper.nextDouble(this.getWorld().random, min, max),
                    MathHelper.nextDouble(this.getWorld().random, min, max),
                    MathHelper.nextDouble(this.getWorld().random, min, max)
            );
        }
    }

    private void tickGravity(){
        double gravity = 0.04 * (double) GravityApi.API.getGravity(this);
        Vec3d velocity = this.getVelocity();
        this.setVelocity(velocity.getX(), velocity.getY() - gravity, velocity.getZ());
    }

    private void explosion(){
        for (int i = 0;i < getBlockCount();i++){
            FallingBlockEntity meteorite = FallingBlockEntity.spawnFromBlock(getWorld(), getBlockPos(), AllBlocks.METEORITE.getDefaultState());
            meteorite.teleport(getX() +(random.nextFloat() - 0.5f) * getWidth() / 2, getY() + random.nextFloat() * getWidth() / 2, getZ() + (random.nextFloat() - 0.5f) * getWidth() / 2);
        }
        Explosion explosion = getWorld().createExplosion(this,
                getWorld().getDamageSources().explosion(this,this), new ExplosionBehavior(),
                getX(), getY(), getZ(),
                (float) (getVelocity().length() + 0.5f) * getBlockCount(), true,
                World.ExplosionSourceType.MOB
        );
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        if (!this.isRemoved() && !state.isAir()){
            explosion();
            this.discard();
        }
    }

    private boolean hasBlockBelow(){
        float width = getWidth();
        int checkRadius = (int) (width/2 + 1);
        for (int offsetX = -checkRadius;offsetX <= checkRadius;offsetX++){
            for (int offsetZ = -checkRadius;offsetZ <= checkRadius;offsetZ++){
                BlockPos checkPos = getBlockPos().add(offsetX,-1,offsetZ);
                if (!getWorld().getBlockState(checkPos).isAir()){
                    return true;
                }
            }
        }
        return false;
    }

    private int getBlockCount(){
        return (int) (getWidth() * getWidth() * getHeight());
    }

    private void setRotation(float rotation){
        this.dataTracker.set(ROTATION_ANGLE,rotation);
    }

    public float getRotation(){
        return this.dataTracker.get(ROTATION_ANGLE);
    }

    public float getRotationAxis(){
        return rotationAxis;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(ROTATION_ANGLE,0f);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(ROTATION_ANGLE,nbt.getFloat("rotation"));
        this.rotationAxis = nbt.getFloat("rotation_axis");
        this.rotationSpeed = nbt.getFloat("rotation_speed");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("rotation",this.dataTracker.get(ROTATION_ANGLE));
        nbt.putFloat("rotation_axis",rotationAxis);
        nbt.putFloat("rotation_speed",rotationSpeed);
    }
    static {
        ROTATION_ANGLE = DataTracker.registerData(Meteorite.class, TrackedDataHandlerRegistry.FLOAT);
    }
}
