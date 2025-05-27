package com.ffsupver.asplor.entity.custom;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.ModDamages;
import com.ffsupver.asplor.entity.ModEntities;
import com.ffsupver.asplor.particle.ModParticles;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.api.systems.GravityApi;
import earth.terrarium.adastra.common.planets.AdAstraData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.List;
import java.util.UUID;

public class Meteorite extends Entity implements Ownable {
    private static final TrackedData<Float> ROTATION_ANGLE;
    private float rotationAxis;
    private float rotationSpeed;
    private static final TrackedData<Integer> HIT_TIMES;
    private static final int MAX_HIT_TIMES = 3;
    private UUID ownerUuid;
    private final boolean destroyBlock;
    public Meteorite(World world){
        this(ModEntities.METEORITE,world);
    }
    public Meteorite(EntityType<?> type, World world){
        this(type,world,true);
    }
    public Meteorite(EntityType<?> type, World world,boolean destroyBlock) {
        super(type, world);
        this.noClip = false;
        this.rotationAxis = world.getRandom().nextFloat() * 360f;
        this.rotationSpeed = world.getRandom().nextFloat() * 5f;
        this.destroyBlock = destroyBlock;
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


        if ((getVelocity().getY() == 0 || getVelocity().isInRange(Vec3d.ZERO,0.01f)) && hasBlockBelow() || getHitTimes() <= -MAX_HIT_TIMES){
            explosion();
        }

        if (getWorld().isClient()){
            Planet planet = AdAstraData.getPlanet(getWorld().getRegistryKey());
            if (planet.oxygen()) {
                spawnParticle();
            }
        }

        rotate();
        tryHitEntity();
    }

    private void tryHitEntity(){
       List<Entity> otherEntities = getWorld().getOtherEntities(this,this.getBoundingBox(),entity ->
               entity.canHit() && (
                       (entity instanceof PlayerEntity player && !player.isCreative() && !player.isSpectator())
                       || !(entity instanceof PlayerEntity)
               )
       );
       for (Entity entity : otherEntities){
           if (getWorld() instanceof ServerWorld serverWorld){
               entity.damage(ModDamages.meteoriteExplosion(serverWorld,this,getOwner()), 10f);
               this.setHitTimes(getHitTimes() - 1);
           }
       }

    }

    private void rotate(){
        float r = getRotation();
        r = r < 360 ? r + rotationSpeed : 0;
        setRotation(r);
    }

    private void spawnParticle(){
        double spread = this.getWorld().random.nextDouble() * getWidth() / 2;
        spawnParticle(ModParticles.LARGE_FLAME,-0.18f, 0.18f,40,spread * 0.8);
        spawnParticle(ModParticles.LARGE_SMOKE,-0.25f,0.25f,40,spread);
        spawnParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,-0.01f,0.01f,8,spread);

    }
    private void spawnParticle(ParticleEffect particleEffect,float min,float max,int count,double spreed){
        for(int i = 0; i < count; ++i){
            this.getWorld().addParticle(particleEffect,
                    this.getX() + spreed, this.getY() + getWidth()/2  + spreed, this.getZ() + spreed,
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
        if (getWorld() instanceof ServerWorld serverWorld){
            Entity owner = getOwner();
            float explosionPowerBonus = - Math.min(0,getHitTimes() + MAX_HIT_TIMES) * 0.5f;
            Explosion explosion = getWorld().createExplosion(owner,
                    ModDamages.meteoriteExplosion(serverWorld,this,getOwner()), new MeteoriteExplosionBehavior(destroyBlock),
                    getX(), getY(), getZ(),
                    (float) (getVelocity().length() + 0.5f) * getBlockCount() + explosionPowerBonus, true,
                    World.ExplosionSourceType.MOB
            );
        }
        discard();
    }

    public Entity getOwner(){
        if (getWorld() instanceof ServerWorld serverWorld){
           Entity entity = serverWorld.getEntity(ownerUuid);
            if (entity != null && !entity.isRemoved()) {
                return entity;
            }
        }
        return null;
    }


    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        if (!this.isRemoved() && !state.isReplaceable()){
            explosion();
        }
    }



    private boolean hasBlockBelow(){
        float width = getWidth();
        int checkRadius = (int) (width/2 + 1);
        for (int offsetX = -checkRadius;offsetX <= checkRadius;offsetX++){
            for (int offsetZ = -checkRadius;offsetZ <= checkRadius;offsetZ++){
                BlockPos checkPos = getBlockPos().add(offsetX,-1,offsetZ);
                if (!getWorld().getBlockState(checkPos).isReplaceable()){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (amount >= 10){
            setHitTimes(Math.min(getHitTimes() - (int) (amount / 10),-MAX_HIT_TIMES * 15));
        }
        return super.damage(source, amount);
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
    public int getHitTimes(){
        return this.dataTracker.get(HIT_TIMES);
    }
    public void setHitTimes(int hitTimes){
        this.dataTracker.set(HIT_TIMES,hitTimes);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("death.attack.meteorite_explosion.no_owner");
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(ROTATION_ANGLE,0f);
        this.dataTracker.startTracking(HIT_TIMES,0);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(ROTATION_ANGLE,nbt.getFloat("rotation"));
        this.rotationAxis = nbt.getFloat("rotation_axis");
        this.rotationSpeed = nbt.getFloat("rotation_speed");
        this.dataTracker.set(HIT_TIMES,nbt.getInt("hit_times"));
        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getUuid("Owner");
        }

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("rotation",this.dataTracker.get(ROTATION_ANGLE));
        nbt.putFloat("rotation_axis",rotationAxis);
        nbt.putFloat("rotation_speed",rotationSpeed);
        nbt.putInt("hit_times",this.dataTracker.get(HIT_TIMES));
        if (this.ownerUuid != null) {
            nbt.putUuid("Owner", this.ownerUuid);
        }
    }
    static {
        ROTATION_ANGLE = DataTracker.registerData(Meteorite.class, TrackedDataHandlerRegistry.FLOAT);
        HIT_TIMES = DataTracker.registerData(Meteorite.class, TrackedDataHandlerRegistry.INTEGER);
    }

    public class MeteoriteExplosionBehavior extends ExplosionBehavior{
        private final boolean destroyBlock;
        public MeteoriteExplosionBehavior(boolean destroyBlock){
            this.destroyBlock = destroyBlock;
        }
        @Override
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
            return (destroyBlock || state.isOf(AllBlocks.METEORITE)) && super.canDestroyBlock(explosion, world, pos, state, power);
        }
    }
}
