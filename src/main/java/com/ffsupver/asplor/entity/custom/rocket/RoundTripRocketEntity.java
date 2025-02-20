package com.ffsupver.asplor.entity.custom.rocket;

import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.api.systems.GravityApi;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.container.VehicleContainer;
import earth.terrarium.adastra.common.planets.AdAstraData;
import earth.terrarium.adastra.common.registry.ModDamageSources;
import earth.terrarium.adastra.common.registry.ModParticleTypes;
import earth.terrarium.adastra.common.registry.ModSoundEvents;
import earth.terrarium.adastra.common.tags.ModFluidTags;
import earth.terrarium.adastra.common.utils.FluidUtils;
import earth.terrarium.adastra.mixins.common.LivingEntityAccessor;
import earth.terrarium.botarium.common.fluid.FluidApi;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.ItemFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidContainer;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import earth.terrarium.botarium.common.menu.ExtraDataMenuProvider;
import earth.terrarium.botarium.common.menu.MenuHooks;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class RoundTripRocketEntity extends Entity implements ExtraDataMenuProvider , Mount , RideableInventory {
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    private boolean launchpadBound;
    public boolean startedRocketSound;
    private int height;
    public static final TrackedData<Boolean> IS_LAUNCHING;
    public static final TrackedData<Integer> LAUNCH_TICKS;
    public static final TrackedData<Boolean> HAS_LAUNCHED;
    public static final TrackedData<Boolean> IS_IN_VALID_DIMENSION;
    public static final TrackedData<Long> FUEL;
    public static final TrackedData<String> FUEL_TYPE;
    public static final TrackedData<Boolean> IS_LANDING;

    protected final VehicleContainer inventory = new VehicleContainer(this.getInventorySize());



    private final SimpleFluidContainer fluidContainer;

    private float speed;
    public RoundTripRocketEntity(EntityType<?> type,World world, Vec3d pos, Vec3d velocity){
        this(type,world);
        this.setPosition(pos.x, pos.y, pos.z);
        this.setVelocity(velocity.x, velocity.y, velocity.z);
    }

    public RoundTripRocketEntity(EntityType<?> type, World world) {
        super(type, world);
        this.speed = 0.005f;
        this.fluidContainer = new SimpleFluidContainer(FluidConstants.fromMillibuckets(6000L), 1,
                (amount, fluid) -> fluid.is(ModFluidTags.TIER_1_ROCKET_FUEL));
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(IS_LAUNCHING, false);
        this.dataTracker.startTracking(LAUNCH_TICKS, -1);
        this.dataTracker.startTracking(HAS_LAUNCHED, false);
        this.dataTracker.startTracking(IS_IN_VALID_DIMENSION, AdAstraConfig.launchFromAnywhere || AdAstraData.canLaunchFrom(this.getWorld().getRegistryKey()) || AdAstraData.isPlanet(this.getWorld().getRegistryKey()));
        this.dataTracker.startTracking(FUEL, 0L);
        this.dataTracker.startTracking(FUEL_TYPE, "air");
        this.dataTracker.startTracking(IS_LANDING,false);
    }
    protected abstract int getInventorySize();
    @Override
    public boolean collidesWith(Entity entity) {
        return BoatEntity.canCollide(this, entity);
    }
    public boolean isCollidable() {
        return true;
    }
    public @NotNull ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient()) {
            if (player.shouldCancelInteraction()) {
                this.openInventory(player);
                return ActionResult.SUCCESS;
            } else {
                return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
            }
        } else {
            return ActionResult.SUCCESS;
        }
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
    }

    public void openInventory(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            MenuHooks.openMenu(serverPlayer, this);
        }
    }

    public void updateTrackedPositionAndAngles(double x, double y, double z, float yRot, float xRot, int lerpSteps, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yRot;
        this.lerpXRot = xRot;
        this.lerpSteps = lerpSteps;
    }

    @Override
    public void tick() {
        super.tick();
        tickLerp();

        if (!this.hasNoGravity()) {
            this.tickGravity();
        }

        this.setPosition(this.getX(), this.getY(), this.getZ());
        if (this.isLogicalSideForUpdatingMovement()) {
            this.move(MovementType.SELF, this.getVelocity());
            this.tickFriction();
        }


        launchPadTick();

        if (canLaunch()){
            this.initiateLaunchSequence();
        }

        if (this.isLaunching()) {
            this.dataTracker.set(LAUNCH_TICKS, this.launchTicks() - 1);
            if (this.launchTicks() <= 0) {
                this.launch();
            }

            this.spawnSmokeParticles();
        } else if (this.hasLaunched()) {
            this.flightTick();
        }else if (isLanding()){
            this.landingTick();
        }

        if (!this.getWorld().isClient()) {
            FluidUtils.moveItemToContainer(this.inventory, this.fluidContainer, 0, 1, 0);
            FluidUtils.moveContainerToItem(this.inventory, this.fluidContainer, 0, 1, 0);
            FluidHolder fluidHolder = this.fluidContainer.getFirstFluid();
            this.dataTracker.set(FUEL, fluidHolder.getFluidAmount());
            this.dataTracker.set(FUEL_TYPE, Registries.FLUID.getId(fluidHolder.getFluid()).toString());
        }
    }



    private void landingTick(){
        speed = (float) this.getVelocity().y;
        float minSpeed = -0.6f;
        if (!this.getWorld().isClient()){
            if (this.getWorld().getHeight() + 16 > this.getY()){
                if (speed < minSpeed) {
                    speed += speed < -2.0f ? 0.2f : 0.1f;
                    this.setVelocity(this.getVelocity().x, speed, this.getVelocity().z);
                    fallDistance *= 0.9f;
                }else {
                    checkHeightWhenLanding();
                    if (height < 16 && speed < minSpeed/3){
                        speed += speed < minSpeed/2 ? 0.1f : 0.15f;
                        this.setVelocity(this.getVelocity().x, speed, this.getVelocity().z);
                        fallDistance *= 0.5f;
                    }else if (height < 8){
                        speed = minSpeed/5;
                        this.setVelocity(this.getVelocity().x, speed, this.getVelocity().z);
                        fallDistance *= 0.3f;
                    }
                }
            }
            if (!this.getWorld().getBlockState(this.getBlockPos().down(1)).isAir()){
                this.dataTracker.set(IS_LANDING,false);
                speed = 0f;
            }
        }
        if (speed < minSpeed/3){
            spawnLandingParticles();
        }
    }

    private void checkHeightWhenLanding(){
        BlockPos checkPos = getBlockPos();
        while (getWorld().getBlockState(checkPos).isAir() && checkPos.getY() >= getWorld().getBottomY()){
            checkPos = checkPos.down();
        }
        height = getBlockY()-checkPos.getY();
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return !this.getType().isIn(EntityTypeTags.FALL_DAMAGE_IMMUNE);
    }

    public void spawnLandingParticles() {
        if (this.getWorld().isClient()) {
            int i;
            for(i = 0; i < 10; ++i) {
                this.getWorld().addParticle(ModParticleTypes.LARGE_FLAME.get(), this.getX(), this.getY() - 0.2, this.getZ(), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05));
            }

            for(i = 0; i < 10; ++i) {
                this.getWorld().addParticle(ModParticleTypes.LARGE_SMOKE.get(), this.getX(), this.getY() - 0.2, this.getZ(), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05));
            }

        }
    }

    private void flightTick() {
        if (!this.getWorld().isClient() && this.getY() >= (double)AdAstraConfig.atmosphereLeave) {
            this.startedRocketSound = false;
            this.teleport();
        } else {
            if (this.speed < 1.5F) {
                this.speed += 0.005F;
            }


            Vec3d delta = this.getVelocity();
            this.setVelocity(delta.getX(), this.speed, delta.getZ());
            if (this.getWorld().isClient() && !this.startedRocketSound) {
                this.startedRocketSound = true;
               RocketSound.play(this);
            }

            this.spawnRocketParticles();
            this.burnEntitiesUnderRocket();

        }
    }

    protected abstract Optional<RegistryKey<World>> getTargetWorldKeyOptional(Planet planet);

    private void teleport(){
        this.launchpadBound = false;
        this.dataTracker.set(HAS_LAUNCHED,false);
        this.dataTracker.set(LAUNCH_TICKS,0);
        this.dataTracker.set(IS_LANDING,true);
        Planet planet = PlanetApi.API.getPlanet(getWorld());
        if (planet != null){
          Optional<RegistryKey<World>> destinationWorldKey = getTargetWorldKeyOptional(planet);
          if (destinationWorldKey.isPresent()){
              TeleportTarget teleportTarget = new TeleportTarget(this.getPos(),this.getVelocity(),this.getYaw(),this.getPitch());
              ServerWorld targetWorld = getWorld().getServer().getWorld(destinationWorldKey.get());
              if (targetWorld != null) {
                  List<Entity> passengers = this.getPassengerList();
                  RoundTripRocketEntity teleportedCargoRocketEntity = FabricDimensions.teleport(this, targetWorld, teleportTarget);
                  passengers.forEach(entity -> {
                      List<Entity> secondPassengerList = entity.getPassengerList();
                      Entity teleportedEntity = FabricDimensions.teleport(entity, targetWorld, teleportTarget);
                      if (teleportedEntity != null) {
                          teleportedEntity.startRiding(teleportedCargoRocketEntity);
                      }
                      secondPassengerList.forEach(secondEntity ->{
                          Entity teleportedSecondEntity = FabricDimensions.teleport(secondEntity,targetWorld,teleportTarget);
                          if (teleportedSecondEntity != null){
                              teleportedSecondEntity.startRiding(teleportedEntity);
                          }
                      });
                  });
              }
          }
        }
    }

    public void spawnRocketParticles() {
        if (this.getWorld().isClient()) {
            int i;
            for(i = 0; i < 20; ++i) {
                this.getWorld().addParticle(ModParticleTypes.LARGE_FLAME.get(), this.getX(), this.getY() - 0.75, this.getZ(), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05));
            }

            for(i = 0; i < 5; ++i) {
                this.getWorld().addParticle(ModParticleTypes.LARGE_SMOKE.get(), this.getX(), this.getY() - 0.75, this.getZ(), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05));
            }

        }
    }

    public void burnEntitiesUnderRocket() {
        if (!this.getWorld().isClient()) {
            Iterator var1 = this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(2.0, 30.0, 2.0).offset(0.0, -37.0, 0.0), (e) -> {
                return true;
            }).iterator();

            while(var1.hasNext()) {
                LivingEntity entity = (LivingEntity)var1.next();
                if (!entity.equals(this.getControllingPassenger())) {
                    entity.setOnFireFor(10);
                    entity.damage(ModDamageSources.create(this.getWorld(), ModDamageSources.ROCKET_FLAMES), 10.0F);
                }
            }

        }
    }

    private void launchPadTick() {
        if (!this.getWorld().isClient() && this.age % 5 == 0) {
            if (!this.isLaunching() && !this.hasLaunched()) {
                BlockState state = this.getWorld().getBlockState(this.getBlockPos());
                if (!state.contains(LaunchPadBlock.PART)) {
                    if (this.launchpadBound) {
                        this.drop();
                        this.playSoundIfNotSilent(SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK);
                        this.discard();
                    }
                } else {
                    this.launchpadBound = true;
                    if (state.get(LaunchPadBlock.POWERED) && this.hasEnoughFuel()) {
                        this.initiateLaunchSequence();
                    }
                }

            }
        }
    }

    public @Nullable LivingEntity getControllingPassenger() {
        Entity firstPassenger = this.getFirstPassenger();
        LivingEntity controller;
        if (firstPassenger instanceof LivingEntity e) {
            controller = e;
        } else {
            controller = null;
        }

        return controller;
    }

    public boolean canLaunch() {
        if (!this.isLaunching() && !this.hasLaunched() && !this.isLanding()) {
            if (!AdAstraConfig.launchFromAnywhere && !(Boolean)this.dataTracker.get(IS_IN_VALID_DIMENSION)) {
                LivingEntity passenger = this.getControllingPassenger();
                if (passenger instanceof ServerPlayerEntity player) {
                    player.sendMessage(ConstantComponents.INVALID_LAUNCHING_DIMENSION, true);
                }

                return false;
            } else {
                return this.hasEnoughFuel() && this.passengerHasSpaceDown();
            }
        } else {
            return false;
        }
    }

    public boolean passengerHasSpaceDown() {
        LivingEntity controllingPassenger = this.getControllingPassenger();
        if (controllingPassenger instanceof LivingEntityAccessor entity) {
            return entity.isJumping();
        } else {
            return false;
        }
    }

    public void initiateLaunchSequence() {
        this.dataTracker.set(IS_LAUNCHING, true);
        this.dataTracker.set(LAUNCH_TICKS, 200);
        this.getWorld().playSound(null, this.getBlockPos(), ModSoundEvents.ROCKET_LAUNCH.get(), SoundCategory.AMBIENT, 10.0F, 1.0F);
        this.consumeFuel(false);
    }

    public boolean hasEnoughFuel() {
        return this.consumeFuel(true);
    }

    public boolean consumeFuel(boolean simulate) {
        if (this.getWorld().isClient()) {
            return false;
        } else {
            long buckets = FluidConstants.fromMillibuckets(this.fluidContainer.getFirstFluid().is(ModFluidTags.EFFICIENT_FUEL) ? 1000L : 3000L);
            return this.fluidContainer.extractFluid(this.fluidContainer.getFirstFluid().copyWithAmount(buckets), simulate).getFluidAmount() >= buckets;
        }
    }



    public void launch() {
        this.dataTracker.set(HAS_LAUNCHED, true);
        this.dataTracker.set(IS_LAUNCHING, false);
        this.dataTracker.set(LAUNCH_TICKS, -1);
    }

    public void spawnSmokeParticles() {
        if (this.getWorld().isClient()) {
            for(int i = 0; i < 6; ++i) {
                this.getWorld().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY(), this.getZ(), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05), MathHelper.nextDouble(this.getWorld().random, -0.05, 0.05));
            }

        }
    }

    public void tickLerp() {
        if (this.isLogicalSideForUpdatingMovement()) {
            this.lerpSteps = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double x = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double y = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double z = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            double g = MathHelper.wrapDegrees(this.lerpYRot - (double)this.getYaw());
            this.setYaw(this.getYaw() + (float)g / (float)this.lerpSteps);
            this.setPitch(this.getPitch() + (float)(this.lerpXRot - (double)this.getPitch()) / (float)this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(x, y, z);
            this.setRotation(this.getYaw(), this.getPitch());
        }
    }

    public void tickGravity() {
        Vec3d velocity = this.getVelocity();
        double gravity = 0.05 * (double) GravityApi.API.getGravity(this);
        gravity = isLanding() ? Math.max(0.05f,gravity) : gravity;
        this.setVelocity(velocity.x, velocity.y - gravity, velocity.z);
    }
    public void tickFriction() {
        float friction = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness();
        float speed = this.isOnGround() ? friction * 0.91F : 0.91F;
        Vec3d deltaMovement = this.getVelocity();
        this.setVelocity(deltaMovement.x * (double)speed, deltaMovement.y, deltaMovement.z * (double)speed);
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    public double getMountedHeightOffset() {
        return 1.7f;
    }


    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.isIn(DamageTypeTags.IS_PROJECTILE)) {
            return false;
        } else {
            if (amount >= 0.0F) {
                Entity var4 = source.getAttacker();
                if (var4 instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity)var4;
                    if (player.getVehicle() == null || !player.getVehicle().equals(this)) {
                        this.playSoundIfNotSilent(SoundEvents.BLOCK_NETHERITE_BLOCK_BREAK);
                        if (!player.getAbilities().creativeMode) {
                            this.drop();
                        }

                        this.discard();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public void drop() {
        ItemScatterer.spawn(this.getWorld(), this.getX(), this.getY(), this.getZ(), this.getDropStack());
        if (!this.inventory.isEmpty()) {
            ItemScatterer.spawn(this.getWorld(), this.getBlockPos(), this.inventory);
        }

    }

    @NotNull
    protected abstract ItemStack asItemStack();

    public ItemStack getDropStack(){
        ItemStackHolder stack = new ItemStackHolder(asItemStack());
        ItemFluidContainer container = FluidContainer.of(stack);
        if (container == null) {
            return stack.getStack();
        } else {
            FluidApi.moveFluid(this.fluidContainer, container, this.fluidContainer.getFirstFluid(), false);
            return stack.getStack();
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(IS_LAUNCHING, nbt.getBoolean("Launching"));
        this.dataTracker.set(LAUNCH_TICKS, nbt.getInt("launchTicks"));
        this.dataTracker.set(HAS_LAUNCHED, nbt.getBoolean("HasLaunched"));
        this.speed = nbt.getFloat("Speed");
        this.fluidContainer.deserialize(nbt);
        this.inventory.readNbtList(nbt.getList("Inventory", 10));
        this.dataTracker.set(IS_LANDING,nbt.getBoolean("is_landing"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("Launching", this.isLaunching());
        nbt.putInt("LaunchTicks", this.launchTicks());
        nbt.putBoolean("HasLaunched", this.hasLaunched());
        nbt.putFloat("Speed", this.speed);
        this.fluidContainer.serialize(nbt);
        nbt.put("Inventory", this.inventory.toNbtList());
        nbt.putBoolean("is_landing",this.dataTracker.get(IS_LANDING));
    }

    @Override
    public void writeExtraData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf buffer) {
        buffer.writeVarInt(this.getId());
    }
    public boolean isLaunching() {
        return this.dataTracker.get(IS_LAUNCHING);
    }

    public int launchTicks() {
        return this.dataTracker.get(LAUNCH_TICKS);
    }

    public boolean hasLaunched() {
        return this.dataTracker.get(HAS_LAUNCHED);
    }
    public void setHasLaunched(boolean hasLaunched){this.dataTracker.set(HAS_LAUNCHED,hasLaunched);}

    public boolean isLanding(){
        return this.dataTracker.get(IS_LANDING);
    }

    @Nullable
    @Override
    public abstract ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player);




    public Inventory inventory() {
        return inventory;
    }

    public FluidHolder fluid() {
        return FluidHolder.of(Registries.FLUID.get(new Identifier(this.dataTracker.get(FUEL_TYPE))), this.dataTracker.get(FUEL));
    }

    public FluidContainer fluidContainer(){return this.fluidContainer;}




    static {
        IS_LAUNCHING = DataTracker.registerData(RoundTripRocketEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        LAUNCH_TICKS = DataTracker.registerData(RoundTripRocketEntity.class, TrackedDataHandlerRegistry.INTEGER);
        HAS_LAUNCHED = DataTracker.registerData(RoundTripRocketEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        IS_IN_VALID_DIMENSION = DataTracker.registerData(RoundTripRocketEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        FUEL = DataTracker.registerData(RoundTripRocketEntity.class, TrackedDataHandlerRegistry.LONG);
        FUEL_TYPE = DataTracker.registerData(RoundTripRocketEntity.class, TrackedDataHandlerRegistry.STRING);
        IS_LANDING = DataTracker.registerData(RoundTripRocketEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
