package com.ffsupver.asplor.entity.custom;

import com.ffsupver.asplor.AllKeys;
import com.ffsupver.asplor.networking.packet.ranger.RangerInputC2SPacket;
import com.ffsupver.asplor.screen.ranger.RangerHandler;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.entities.vehicles.Vehicle;
import earth.terrarium.adastra.common.tags.ModFluidTags;
import earth.terrarium.adastra.common.utils.FluidUtils;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidContainer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class Ranger extends Vehicle {
    private float angle;
    private float speed;
    private ControllerInput controllerInput;
    private int startFlyingTick;
    public static final TrackedData<Long> FUEL;
    public static final TrackedData<String> FUEL_TYPE;
    public static final TrackedData<Boolean> FLYING;
    private final SimpleFluidContainer fluidContainer = new SimpleFluidContainer(FluidConstants.fromMillibuckets(15000L), 1, (amount, fluid) -> fluid.is(ModFluidTags.TIER_2_ROCKET_FUEL));
    public Ranger(EntityType<?> type, World level) {
        super(type, level);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FUEL, 0L);
        this.dataTracker.startTracking(FUEL_TYPE, "air");
        this.dataTracker.startTracking(FLYING, false);
    }



    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (this.hasPassengers()) {
            float damageAmount = Math.abs(getVelocity().getY()) < 1f ? 0 : Math.min(5.0f,damageMultiplier);
            for (Entity entity : this.getPassengerList()) {
                entity.handleFallDamage(fallDistance, damageAmount, damageSource);
            }
        }
        return false;
    }

    @Override
    public boolean isSafeToDismount(PlayerEntity player) {
        return this.getVelocity().length() < 0.1f;
    }

    @Override
    public void tick() {
        super.tick();

        checkIsFlying();

        if (getWorld().isClient()){
            sendControl();
        }

        if (controllerInput != null){
            controllerInput.control();
        }

        handleMovement();

        if (!this.getWorld().isClient()) {
            FluidUtils.moveItemToContainer(this.inventory, this.fluidContainer, 0, 1, 0);
            FluidUtils.moveContainerToItem(this.inventory, this.fluidContainer, 0, 1, 0);
            FluidHolder fluidHolder = this.fluidContainer.getFirstFluid();
            this.dataTracker.set(FUEL, fluidHolder.getFluidAmount());
            this.dataTracker.set(FUEL_TYPE, Registries.FLUID.getId(fluidHolder.getFluid()).toString());
        }

        tryTeleportToSpace();
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (FLYING.equals(data)){
            boolean flying = dataTracker.get(FLYING);
            this.setFlying(flying);
        }
    }

    private void tryTeleportToSpace(){
        if (getWorld().isClient()){
            return;
        }
        boolean heightEnough = isSpace() ? getY() < getWorld().getBottomY() - 32 : getY() > AdAstraConfig.atmosphereLeave;
        if (heightEnough && PlanetApi.API.isPlanet(getWorld())) {
            Planet planet = PlanetApi.API.getPlanet(getWorld());
            Optional<RegistryKey<World>> destination = isSpace() ? planet.getOrbitPlanet() : planet.orbit();
            if (getWorld() instanceof ServerWorld serverWorld) {
                if (destination.isPresent()) {
                    ServerWorld destinationWorld = serverWorld.getServer().getWorld(destination.get());
                    Vec3d targetPos = getPos().add(0,
                            -getPos().y + (isSpace() ? AdAstraConfig.atmosphereLeave : getWorld().getBottomY() - 16),
                            0);
                    TeleportTarget teleportTarget = new TeleportTarget(targetPos,getVelocity(),getYaw(),getPitch());
                    List<Entity> passengers = getPassengerList();
                    boolean flying = this.dataTracker.get(FLYING);
                    Ranger teleported = FabricDimensions.teleport(this,destinationWorld ,teleportTarget);
                    teleported.setFlying(flying);
                    for(Entity entity : passengers){
                        Entity teleportedEntity = FabricDimensions.teleport(entity,destinationWorld,teleportTarget);
                        if (teleportedEntity != null) {
                            teleportedEntity.startRiding(teleported);
                        }
                    }
                }
            }
        }
    }




    @Override
    protected void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putFloat("speed",speed);
        compound.putFloat("angle",angle);
        compound.putBoolean("flying",this.dataTracker.get(FLYING));
        this.fluidContainer.serialize(compound);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        speed = compound.getFloat("speed");
        angle = compound.getFloat("angle");
        this.dataTracker.set(FLYING,compound.getBoolean("flying"));
        this.fluidContainer.deserialize(compound);
    }

    private void checkIsFlying(){
        if (isOnGround()){
            setFlying(false);
        }
        boolean flying = this.dataTracker.get(FLYING);
        if (flying && !getWorld().isClient() && !hasEnoughFuel()){
            setFlying(false);
        }
    }



    private void handleMovement(){
        boolean flying = this.dataTracker.get(FLYING);

        boolean noPassenger = this.getControllingPassenger() == null;


        float xxa = -this.xxa();
        float zza = this.zza();


        if (!flying){
            zza=0;
            xxa=0;
        }
        if (flying && hasEnoughFuel()){
            if (Math.abs(zza) > 0 || !isSpace()) {
                consumeFuel();
            }
        }


        if (zza != 0.0F) {
            this.speed += 0.04F * zza;
        } else {
            this.speed *= isSpace()? 1 : noPassenger ? 0.82F : 0.8F;
        }

        if (!isSpace() && noPassenger && this.speed < 0.1F && this.speed > -0.1F) {
            this.speed *= isOnGround() ? 0.9F : 0.99F;
        }

        // 垂直速度调整
//        float maxVerticalSpeed = 0.5F;
//        float verticalSpeed = MathHelper.clamp(yya * 0.5F, -maxVerticalSpeed, maxVerticalSpeed);

        float maxBlocksPerTick = 2.6944445F;
        this.speed = MathHelper.clamp(this.speed, -maxBlocksPerTick / 2.0F, maxBlocksPerTick);
        if (xxa != 0.0F && (this.speed > 0.05F || this.speed < -0.05F)) {
            this.angle += xxa * Math.signum(this.speed) * Math.abs(this.speed);
        } else {
            this.angle *= noPassenger ? 0.95F : 0.75F;
        }

        this.angle = MathHelper.clamp(this.angle, -3.0F, 3.0F);
        this.setYaw(this.getYaw() + this.angle);
        float yRot = this.getYaw() * 0.017453292F;
        this.setVelocity(MathHelper.sin(-yRot) * this.speed,
                this.getVelocity().y,
                MathHelper.cos(yRot) * this.speed);
    }

    private boolean isSpace(){
        return PlanetApi.API.isSpace(getWorld());
    }

    public void setFlying(boolean flying) {
        this.dataTracker.set(FLYING,flying);
    }

    @Override
    public void tickGravity() {
        boolean flying = this.dataTracker.get(FLYING);
        if (!flying) {
            super.tickGravity();
        }
    }



    @Override
    protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater callback) {
        if (this.hasPassenger(passenger)) {
            this.clampRotation(passenger);
            passenger.setYaw(passenger.getYaw() + this.angle);
            passenger.setHeadYaw(passenger.getHeadYaw() + this.angle);
            callback.accept(passenger, this.getX(), this.getY(), this.getZ());
        }
    }

    @Override
    public void onPassengerLookAround(Entity passenger) {
        clampRotation(passenger);
    }
    private void clampRotation(Entity entityToUpdate) {
        entityToUpdate.setBodyYaw(this.getYaw());
        float degrees = MathHelper.wrapDegrees(entityToUpdate.getYaw() - this.getYaw());
        float lookAngle = MathHelper.clamp(degrees, -135.0F, 135.0F);
        entityToUpdate.prevYaw += lookAngle - degrees;
        entityToUpdate.setYaw(entityToUpdate.getYaw() + lookAngle - degrees);
        entityToUpdate.setHeadYaw(entityToUpdate.getYaw());
    }

    private void sendControl(){
        if (getControllingPassenger() instanceof ClientPlayerEntity clientPlayerEntity){
            Input input = clientPlayerEntity.input;

            boolean down = AllKeys.DOWN.wasPressed();

            boolean jumping = input.jumping;
            this.controllerInput = new ControllerInput(jumping, down);


            RangerInputC2SPacket.send(getUuid(), getWorld().getRegistryKey().getValue(),jumping,down);

        }
    }

    public void setControl(boolean jump,boolean down){
        this.controllerInput = new ControllerInput(jump, down);
    }

    public void consumeFuel() {
        if (!this.getWorld().isClient() && this.age % 10 == 0) {
            this.fluidContainer.extractFluid(this.fluidContainer.getFirstFluid().copyWithAmount(FluidConstants.fromMillibuckets(1L)), false);
        }
    }

    public boolean hasEnoughFuel() {
        if (this.getWorld().isClient()) {
            return this.dataTracker.get(FUEL) > 0L;
        } else {
            return this.fluidContainer.getFirstFluid().getFluidAmount() > 0L;
        }
    }




    @Override
    public ItemStack getDropStack() {
        return null;
    }

    @Override
    public int getInventorySize() {
        return 18;
    }

    public FluidHolder fluid() {
        return FluidHolder.of((Fluid) Registries.FLUID.get(new Identifier((String)this.dataTracker.get(FUEL_TYPE))), (Long)this.dataTracker.get(FUEL), (NbtCompound)null);
    }

    public FluidContainer fluidContainer() {
        return this.fluidContainer;
    }


    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new RangerHandler(syncId,playerInventory,this);
    }


    private class ControllerInput {

        private final boolean jump;
        private final boolean down;
        private ControllerInput( boolean jump, boolean down) {
            this.jump = jump;

            this.down = down;
        }

        public void control(){
            if (!hasEnoughFuel()){
                return;
            }

            boolean flying = dataTracker.get(FLYING);
            if (jump){
                if (flying){
                    setVelocity(getVelocity().add(0, Math.abs(getVelocity().y) < 1.5f ? 0.5 : 0,0));
                }else {
                    if (startFlyingTick < 10) {
                        startFlyingTick++;
                    }else {
                        setFlying(true);
                        startFlyingTick = 0;
                    }
                }

            }else if (down){
                setVelocity(getVelocity().add(0, Math.abs(getVelocity().y) < 1.5f ? -0.5 : 0,0));

            }else {
                setVelocity(getVelocity().add(0, -0.2 * getVelocity().y,0));
            }
        }
    }

    static {
        FUEL = DataTracker.registerData(Ranger.class, TrackedDataHandlerRegistry.LONG);
        FUEL_TYPE = DataTracker.registerData(Ranger.class, TrackedDataHandlerRegistry.STRING);
        FLYING = DataTracker.registerData(Ranger.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
