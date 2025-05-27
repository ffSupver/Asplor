package com.ffsupver.asplor.entity.custom;

import com.ffsupver.asplor.ModDamages;
import com.ffsupver.asplor.entity.ModEntities;
import com.ffsupver.asplor.entity.ai.GlacioVillagerShamanAttackGoal;
import com.ffsupver.asplor.entity.ai.GlacioVillagerShamanCastingSpellGoal;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.item.item.StaffOfShootingMeteorite;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GlacioVillagerShaman extends HostileEntity {
    private static final TrackedData<Boolean> ATTACKING = DataTracker.registerData(GlacioVillagerShaman.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> METEORITE_ATTACK_COOL_DOWN = DataTracker.registerData(GlacioVillagerShaman.class, TrackedDataHandlerRegistry.INTEGER);

    private static final TrackedData<Boolean> METEORITE_ATTACKING = DataTracker.registerData(GlacioVillagerShaman.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HIGH_RESISTANCE = DataTracker.registerData(GlacioVillagerShaman.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2,ItemStack.EMPTY);
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeOut = 0;
    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeOut = 0;
    public final AnimationState meteoriteAttackAnimationState = new AnimationState();
    public int meteoriteAttackAnimationTimeOut = 0;

    private static final int METEORITE_ATTACK_DELAY = 100;
    private final ServerBossBar bossBar = new ServerBossBar(getDisplayName(), BossBar.Color.BLUE, BossBar.Style.PROGRESS);


    public GlacioVillagerShaman( World world) {
        super(ModEntities.GLACIO_VILLAGER_SHAMAN, world);
    }

    public GlacioVillagerShaman(EntityType<GlacioVillagerShaman> glacioVillagerShamanEntityType, World world) {
        super(glacioVillagerShamanEntityType,world);
        this.handDropChances[0] = 0.085f;
        this.handDropChances[1] = 0f;
        this.experiencePoints = 50;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0,new SwimGoal(this));
        this.goalSelector.add(4,new WanderAroundFarGoal(this,0.2f));
        this.goalSelector.add(5,new LookAroundGoal(this));

        this.goalSelector.add(1,new GlacioVillagerShamanAttackGoal(this,0.6f,true));
        this.goalSelector.add(1,new GlacioVillagerShamanCastingSpellGoal(this,0.6f,7));

        this.targetSelector.add(1,new RevengeGoal(this));
        this.targetSelector.add(2,new ActiveTargetGoal<>(this, IronGolemEntity.class,true));
    }

    public static DefaultAttributeContainer.Builder createAttributes(){
       return createHostileAttributes()
               .add(EntityAttributes.GENERIC_FOLLOW_RANGE,40)
               .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,4)
               .add(EntityAttributes.GENERIC_MAX_HEALTH,120);
    }

    @Override
    protected float getJumpVelocity() {
        return super.getJumpVelocity() + 0.1f;
    }


    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        Inventories.writeNbt(nbt,inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt,inventory);
        if (hasCustomName()){
            this.bossBar.setName(getDisplayName());
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        this.bossBar.setName(getDisplayName());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()){
            setUpAnimationState();
            if (isMeteoriteAttacking()){
                addParticle();
            }
        }
        countMeteoriteCoolDown();

        if (!getWorld().isClient()){
            updateHandItem();
        }

        this.bossBar.setPercent(getHealth() / getMaxHealth());
    }

    private void updateHandItem(){
        if (isMeteoriteAttacking()){
            this.setStackInHand(Hand.OFF_HAND,inventory.get(0));
        }else {
            this.setStackInHand(Hand.OFF_HAND,ItemStack.EMPTY);
        }

        if (getStackInHand(Hand.MAIN_HAND).isEmpty()){
            this.setStackInHand(Hand.MAIN_HAND, inventory.get(1));
        }
    }

    private void countMeteoriteCoolDown(){
        int old = getMeteoriteCoolDown();
        setMeteoriteCoolDown(Math.max(old - 1,0));
    }

    private void resetMeteoriteCoolDown(){
        setMeteoriteCoolDown(METEORITE_ATTACK_DELAY);
    }

    public void attackWithMeteorite(LivingEntity target){
        StaffOfShootingMeteorite.meteoriteHitAt(target.getPos(),getWorld(),this,false);
    }


    public void tryAttackWithMeteorite(LivingEntity target){
        if (getMeteoriteCoolDown() <= 0){
            attackWithMeteorite(target);
            resetMeteoriteCoolDown();
        }
    }


    @Override
    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6 , 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f,0.2f);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.9F;
    }

    private void setUpAnimationState(){
        if (this.idleAnimationTimeOut <= 0){
            this.idleAnimationTimeOut = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        }else {
            if (!isAttacking() && !isMeteoriteAttacking()){
                this.idleAnimationTimeOut--;
            }else {
                this.idleAnimationTimeOut = Math.max(10,this.idleAnimationTimeOut);
            }
        }

        if (isAttacking() && this.attackAnimationTimeOut <= 0){
            attackAnimationTimeOut = 20;
            attackAnimationState.start(this.age);
        }else {
            this.attackAnimationTimeOut--;
        }


        if (isMeteoriteAttacking() && meteoriteAttackAnimationTimeOut <= 0){
            meteoriteAttackAnimationTimeOut = 40;
            meteoriteAttackAnimationState.start(this.age);
        }else {
            meteoriteAttackAnimationTimeOut--;
        }

        if (isAttacking() || isMeteoriteAttacking()) {
            this.idleAnimationState.stop();
        }
        if (!isMeteoriteAttacking()){
            this.meteoriteAttackAnimationState.stop();
        }
        if (!isAttacking()){
            this.attackAnimationState.stop();
        }
    }

    public Vec3d getCastingSpellsParticlePos(){
        Vec3d hPos = new Vec3d(-0.5,0,0);
        return hPos.rotateY(-(float) Math.toRadians(getHeadYaw()));
    }

    private void addParticle(){
        Vec3d hPos = getCastingSpellsParticlePos();
        for (int i = 0; i < 10; i++) {
            getWorld().addParticle(ParticleTypes.EFFECT, false, getX() + hPos.getX(), getY() + 2.15f, getZ() + hPos.getZ(), this.random.nextFloat() * 0.01f, 0.01f, this.random.nextFloat() * 0.01f);
        }
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        EntityData result = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        this.setLeftHanded(false);
        this.setItemInInventory(ModItems.STAFF_OF_SHOOTING_METEORITE.getDefaultStack(), 0);
        ItemStack sword = (this.random.nextBoolean() ? Items.DIAMOND_SWORD : ModItems.ALLOY_SWORD).getDefaultStack();
        if (this.random.nextInt(4) == 0){
            EnchantmentHelper.enchant(this.random,sword,15,true);
        }
        this.setItemInInventory(sword,1);
        return result;
    }

    public void setItemInInventory(ItemStack itemStack,int slot){
        this.inventory.set(slot,itemStack);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(ModDamages.METEORITE_EXPLOSION_TYPE)){
            return false;
        }
        if (isHighResistance()){
            return super.damage(source,amount / 2);
        }
        return super.damage(source, amount);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING,false);
        this.dataTracker.startTracking(METEORITE_ATTACK_COOL_DOWN,METEORITE_ATTACK_DELAY);
        this.dataTracker.startTracking(METEORITE_ATTACKING,false);
        this.dataTracker.startTracking(HIGH_RESISTANCE,false);
    }

    private int getMeteoriteCoolDown(){
        return this.dataTracker.get(METEORITE_ATTACK_COOL_DOWN);
    }

    private void setMeteoriteCoolDown(int s){
        this.dataTracker.set(METEORITE_ATTACK_COOL_DOWN,s);
    }

    @Override
    public void setAttacking(boolean attacking) {
        this.dataTracker.set(ATTACKING,attacking);
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    public void setMeteoriteAttacking(boolean meteoriteAttacking){
        this.dataTracker.set(METEORITE_ATTACKING,meteoriteAttacking);
    }

    public boolean isMeteoriteAttacking(){
        return this.dataTracker.get(METEORITE_ATTACKING);
    }
    public void setHighResistance(boolean h){
        this.dataTracker.set(HIGH_RESISTANCE,h);
    }

    public boolean isHighResistance(){
        return this.dataTracker.get(HIGH_RESISTANCE);
    }
}
