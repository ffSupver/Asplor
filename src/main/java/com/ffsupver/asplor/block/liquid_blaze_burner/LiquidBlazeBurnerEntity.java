package com.ffsupver.asplor.block.liquid_blaze_burner;

import com.ffsupver.asplor.recipe.FluidInventory;
import com.ffsupver.asplor.recipe.LiquidBlazeBurnerRecipe;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.ffsupver.asplor.block.liquid_blaze_burner.LiquidBlazeBurner.HEAT_LEVEL;
import static com.ffsupver.asplor.block.liquid_blaze_burner.LiquidBlazeBurner.getHeatLevelOf;


public class LiquidBlazeBurnerEntity extends SmartBlockEntity implements IMultiBlockEntityContainer.Fluid , SidedStorageBlockEntity, IHaveGoggleInformation {
private final int AMOUNT_TO_MB = 81;
    public static final int MAX_HEAT_CAPACITY = 10000;
    public static final int LIQUID_CHECK_THRESHOLD = 20;
    public static final int TICKS_AFTER_SUPER_HEAT=100;
    public static final int CAPACITY = 4 ;

    protected FuelType activeFuel;
    protected int remainingBurnTime;
    protected LerpedFloat headAnimation;
    protected LerpedFloat headAngle;
    protected boolean isCreative;
    protected boolean goggles;
    protected boolean hat;

    protected BlockPos controller;
    protected FluidTank exposedTank;
    protected SmartFluidTank tankInventory;
    protected int width;
    protected int height;
    private final int MAX_WIDTH =5;
    private final int MAX_HEIGHT=1;
    protected boolean updateConnectivity;
    protected boolean forceFluidLevelUpdate;
    protected BlockPos lastKnownPos;
    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    // For rendering purposes only
    private LerpedFloat fluidLevel;


    public LiquidBlazeBurnerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        activeFuel = FuelType.NONE;
        remainingBurnTime = 0;
        headAnimation = LerpedFloat.linear();
        headAngle = LerpedFloat.angular();
        isCreative = false;
        goggles = false;

        height = 1;
        width = 1;
        tankInventory=createInventory();
        updateConnectivity=false;
        forceFluidLevelUpdate=true;

        headAngle.startWithValue((AngleHelper.horizontalAngle(state.getOrEmpty(LiquidBlazeBurner.FACING)
                .orElse(Direction.SOUTH)) + 180) % 360);
    }

    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(getCapacityMultiplier(), this::onFluidStackChanged);
    }

    private void onFluidStackChanged(FluidStack newFluidStack) {
        if (!hasWorld())
            return;

        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = this.pos.add(xOffset, yOffset, zOffset);
                    LiquidBlazeBurnerEntity tankAt = ConnectivityHandler.partAt(getType(), world, pos);
                    if (tankAt == null)
                        continue;
                    world.updateComparators(pos, tankAt.getCachedState()
                            .getBlock());
                }
            }
        }

        if (!world.isClient) {
            markDirty();
            sendData();
        }

        if (isVirtual()) {
            if (fluidLevel == null)
                fluidLevel = LerpedFloat.linear()
                        .startWithValue(getFillState());
            fluidLevel.chase(getFillState(), .5f, LerpedFloat.Chaser.EXP);
        }
    }
    public float getFillState() {
        return (float) tankInventory.getFluidAmount() / tankInventory.getCapacity();
    }

    public static long getCapacityMultiplier() {
        return CAPACITY * FluidConstants.BUCKET;
    }

    protected void updateConnectivity() {
        updateConnectivity = false;
        if (world.isClient)
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);
    }
    public FuelType getActiveFuel() {
        return activeFuel;
    }

    public int getRemainingBurnTime() {
        return remainingBurnTime;
    }

    public boolean isCreative() {
        return isCreative;
    }

    private int getMB(int amount){
        return amount*AMOUNT_TO_MB;
    }
    private long toMB(long amount){return amount/AMOUNT_TO_MB;}

    private Optional<LiquidBlazeBurnerRecipe> getCurrentRecipe(){
        LiquidBlazeBurnerEntity controllerBE = getControllerBE();
        SmartFluidTank controllerTank = controllerBE.tankInventory;
       FluidInventory fluidInventory = new FluidInventory(0,(toMB(controllerTank.getCapacity())));
        fluidInventory.insertFluid(0,controllerTank.getFluid().getType(),toMB(controllerTank.getFluidAmount()));
        return getWorld().getRecipeManager().getFirstMatch(LiquidBlazeBurnerRecipe.Type.INSTANCE,fluidInventory,getWorld());
    }


    @Override
    public void tick() {
        super.tick();
        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                sendData();
        }
        if (lastKnownPos == null)
            lastKnownPos = getPos();
        else if (!lastKnownPos.equals(pos) && pos != null) {
            onPositionChanged();
            return;
        }
        if (updateConnectivity)
            updateConnectivity();
        if (fluidLevel != null)
            fluidLevel.tickChaser();

        if (world.isClient) {
            tickAnimation();
            if (!isVirtual())
                spawnParticles(getHeatLevelFromBlock(), 1);
            return;
        }



        if (isCreative)
            return;

        if (remainingBurnTime > 0)
            remainingBurnTime--;
        if (getCurrentRecipe().isPresent()){
            LiquidBlazeBurnerEntity controllerBE = getControllerBE();
            SmartFluidTank controllerTank = controllerBE.tankInventory;
            LiquidBlazeBurnerRecipe recipe = getCurrentRecipe().get();

            FuelType newFuelType = switch (recipe.getHeatType()){
                case "normal" -> FuelType.NORMAL;
                case "super" -> FuelType.SPECIAL;
                default -> FuelType.NORMAL;
            };
        if(remainingBurnTime<LIQUID_CHECK_THRESHOLD||(newFuelType==FuelType.SPECIAL&&activeFuel==FuelType.NORMAL)){
                try (Transaction t = TransferUtil.getTransaction()) {
                    int newTime = recipe.getOutput(null).getCount();
                    controllerTank.extract(recipe.getInputFluid(),getMB((int) recipe.getRequiredAmount()),t);
                       if (activeFuel==newFuelType) {
                           remainingBurnTime += newTime;
                       }else {
                           remainingBurnTime = newTime;
                       }
                       if (activeFuel != FuelType.NORMAL && activeFuel!=FuelType.SPECIAL) {
                        playSound();
                    }
                    activeFuel =newFuelType;
                    t.commit();
                }
                updateBlockState();
            }


        }


        if (remainingBurnTime > 0)
            return;

        if (activeFuel == FuelType.SPECIAL) {
            activeFuel = FuelType.NORMAL;
            remainingBurnTime = TICKS_AFTER_SUPER_HEAT;
        } else
            activeFuel = FuelType.NONE;

        updateBlockState();
    }

    @Environment(EnvType.CLIENT)
    private void tickAnimation() {
        boolean active = getHeatLevelFromBlock().isAtLeast(BlazeBurnerBlock.HeatLevel.FADING) && isValidBlockAbove();

        if (!active) {
            float target = 0;
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && !player.isInvisible()) {
                double x;
                double z;
                if (isVirtual()) {
                    x = -4;
                    z = -10;
                } else {
                    x = player.getX();
                    z = player.getZ();
                }
                double dx = x - (getPos().getX() + 0.5);
                double dz = z - (getPos().getZ() + 0.5);
                target = AngleHelper.deg(-MathHelper.atan2(dz, dx)) - 90;
            }
            target = headAngle.getValue() + AngleHelper.getShortestAngleDiff(headAngle.getValue(), target);
            headAngle.chase(target, .25f, LerpedFloat.Chaser.exp(5));
            headAngle.tickChaser();
        } else {
            headAngle.chase((AngleHelper.horizontalAngle(getCachedState().getOrEmpty(LiquidBlazeBurner.FACING)
                    .orElse(Direction.SOUTH)) + 180) % 360, .125f, LerpedFloat.Chaser.EXP);
            headAngle.tickChaser();
        }

        headAnimation.chase(active ? 1 : 0, .25f, LerpedFloat.Chaser.exp(.25f));
        headAnimation.tickChaser();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public void initialize() {
        super.initialize();
        sendData();
        if (world.isClient)
            invalidateRenderBoundingBox();
    }

    @Override
    public void write(NbtCompound compound, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);
        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtHelper.fromBlockPos(lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtHelper.fromBlockPos(controller));
        if (isController()) {
            compound.put("TankContent", tankInventory.writeToNBT(new NbtCompound()));
            compound.putInt("Size", width);
            compound.putInt("Height", height);
        }

        if (!clientPacket)
            return;
        if (forceFluidLevelUpdate)
            compound.putBoolean("ForceFluidLevel", true);
        if (queuedSync)
            compound.putBoolean("LazySync", true);
        forceFluidLevelUpdate = false;


        if (!isCreative) {
            compound.putInt("fuelLevel", activeFuel.ordinal());
            compound.putInt("burnTimeRemaining", remainingBurnTime);
        } else
            compound.putBoolean("isCreative", true);
        if (goggles)
            compound.putBoolean("Goggles", true);
        if (hat)
            compound.putBoolean("TrainHat", true);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(NbtCompound compound, boolean clientPacket) {
        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;

        updateConnectivity = compound.contains("Uninitialized");
        controller = null;
        lastKnownPos = null;

        if (compound.contains("LastKnownPos"))
            lastKnownPos = NbtHelper.toBlockPos(compound.getCompound("LastKnownPos"));
        if (compound.contains("Controller"))
            controller = NbtHelper.toBlockPos(compound.getCompound("Controller"));

        if (isController()) {
            width = compound.getInt("Size");
            height = compound.getInt("Height");
            tankInventory.setCapacity(getTotalTankSize() * getCapacityMultiplier());
            tankInventory.readFromNBT(compound.getCompound("TankContent"));
            if (tankInventory.getSpace() < 0) {
                try (Transaction t = TransferUtil.getTransaction()) {
                    tankInventory.extract(tankInventory.variant, -tankInventory.getSpace(), t);
                    t.commit();
                }
            }
        }

        if (compound.contains("ForceFluidLevel") || fluidLevel == null)
            fluidLevel = LerpedFloat.linear()
                    .startWithValue(getFillState());

        if (!clientPacket)
            return;

        boolean changeOfController =
                controllerBefore == null ? controller != null : !controllerBefore.equals(controller);
        if (changeOfController || prevSize != width || prevHeight != height) {
            if (hasWorld())
                world.updateListeners(getPos(), getCachedState(), getCachedState(), 16);
            if (isController())
                tankInventory.setCapacity(getCapacityMultiplier() * getTotalTankSize());
            invalidateRenderBoundingBox();
        }
        if (isController()) {
            float fillState = getFillState();
            if (compound.contains("ForceFluidLevel") || fluidLevel == null)
                fluidLevel = LerpedFloat.linear()
                        .startWithValue(fillState);
            fluidLevel.chase(fillState, 0.5f, LerpedFloat.Chaser.EXP);
        }

        if (compound.contains("LazySync"))
            fluidLevel.chase(fluidLevel.getChaseTarget(), 0.125f, LerpedFloat.Chaser.EXP);


        activeFuel = FuelType.values()[compound.getInt("fuelLevel")];
        remainingBurnTime = compound.getInt("burnTimeRemaining");
        isCreative = compound.getBoolean("isCreative");
        goggles = compound.contains("Goggles");
        hat = compound.contains("TrainHat");
        super.read(compound, clientPacket);
    }

    private int getTotalTankSize() {
        return width*width*height;
    }

    @Override
    public void sendData() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        super.sendData();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    public BlazeBurnerBlock.HeatLevel getHeatLevelFromBlock() {
        return getHeatLevelOf(getCachedState());
    }

    public void updateBlockState() {
        setBlockHeat(getHeatLevel());
    }

    protected void setBlockHeat(BlazeBurnerBlock.HeatLevel heat) {
        BlazeBurnerBlock.HeatLevel inBlockState = getHeatLevelFromBlock();
        if (inBlockState == heat)
            return;
        world.setBlockState(pos, getCachedState().with(HEAT_LEVEL, heat));
        notifyUpdate();
    }

    protected void applyCreativeFuel() {
        activeFuel = FuelType.NONE;
        remainingBurnTime = 0;
        isCreative = true;

        BlazeBurnerBlock.HeatLevel next = getHeatLevelFromBlock().nextActiveLevel();

        if (world.isClient) {
            spawnParticleBurst(next.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING));
            return;
        }

        playSound();
        if (next == BlazeBurnerBlock.HeatLevel.FADING)
            next = next.nextActiveLevel();
        setBlockHeat(next);
    }

    public boolean isCreativeFuel(ItemStack stack) {
        return AllItems.CREATIVE_BLAZE_CAKE.isIn(stack);
    }

    public boolean isValidBlockAbove() {
        if (isVirtual())
            return false;
        BlockState blockState = world.getBlockState(pos.up());
        return AllBlocks.BASIN.has(blockState) || blockState.getBlock() instanceof FluidTankBlock;
    }

    protected void playSound() {
        world.playSound(null, pos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS,
                .125f + world.random.nextFloat() * .125f, .75f - world.random.nextFloat() * .25f);
    }

    protected BlazeBurnerBlock.HeatLevel getHeatLevel() {
        BlazeBurnerBlock.HeatLevel level = BlazeBurnerBlock.HeatLevel.SMOULDERING;
        switch (activeFuel) {
            case SPECIAL:
                level = BlazeBurnerBlock.HeatLevel.SEETHING;
                break;
            case NORMAL:
                boolean lowPercent = (double) remainingBurnTime / MAX_HEAT_CAPACITY < 0.0125;
                level = lowPercent ? BlazeBurnerBlock.HeatLevel.FADING : BlazeBurnerBlock.HeatLevel.KINDLED;
                break;
            default:
            case NONE:
                break;
        }
        return level;
    }

    protected void spawnParticles(BlazeBurnerBlock.HeatLevel heatLevel, double burstMult) {
        if (world == null)
            return;
        if (heatLevel == BlazeBurnerBlock.HeatLevel.NONE)
            return;

        Random r = world.getRandom();

        Vec3d c = VecHelper.getCenterOf(pos);
        Vec3d v = c.add(VecHelper.offsetRandomly(Vec3d.ZERO, r, .125f)
                .multiply(1, 0, 1));

        if (r.nextInt(4) != 0)
            return;

        boolean empty = world.getBlockState(pos.up())
                .getCollisionShape(world, pos.up())
                .isEmpty();

        if (empty || r.nextInt(8) == 0)
            world.addParticle(ParticleTypes.LARGE_SMOKE, v.x, v.y, v.z, 0, 0, 0);

        double yMotion = empty ? .0625f : r.nextDouble() * .0125f;
        Vec3d v2 = c.add(VecHelper.offsetRandomly(Vec3d.ZERO, r, .5f)
                        .multiply(1, .25f, 1)
                        .normalize()
                        .multiply((empty ? .25f : .5) + r.nextDouble() * .125f))
                .add(0, .5, 0);

        if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)) {
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, v2.x, v2.y, v2.z, 0, yMotion, 0);
        } else if (heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
            world.addParticle(ParticleTypes.FLAME, v2.x, v2.y, v2.z, 0, yMotion, 0);
        }
        return;
    }

    public void spawnParticleBurst(boolean soulFlame) {
        Vec3d c = VecHelper.getCenterOf(pos);
        Random r = world.random;
        for (int i = 0; i < 20; i++) {
            Vec3d offset = VecHelper.offsetRandomly(Vec3d.ZERO, r, .5f)
                    .multiply(1, .25f, 1)
                    .normalize();
            Vec3d v = c.add(offset.multiply(.5 + r.nextDouble() * .125f))
                    .add(0, .125, 0);
            Vec3d m = offset.multiply(1 / 32f);

            world.addParticle(soulFlame ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, v.x, v.y, v.z, m.x, m.y,
                    m.z);
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        LiquidBlazeBurnerEntity controllerBE = getControllerBE();
        if (controllerBE == null)
            return false;
        return containedFluidTooltip(tooltip, isPlayerSneaking,
                controllerBE.getFluidStorage(null));
    }

    //液体


    @Nullable
    public LiquidBlazeBurnerEntity getOtherFluidTankBlockEntity(Direction direction) {
        BlockEntity otherBE = world.getBlockEntity(pos.offset(direction));
        if (otherBE instanceof LiquidBlazeBurnerEntity)
            return (LiquidBlazeBurnerEntity) otherBE;
        return null;
    }
    private void refreshCapability() {
        exposedTank = handlerForCapability();
    }

    private FluidTank handlerForCapability() {
        return isController() ?  tankInventory
                : getControllerBE() != null ? ((LiquidBlazeBurnerEntity)getControllerBE()).handlerForCapability() : new FluidTank(0);
    }
    @Override
    public BlockPos getController() {
        return isController() ? pos : controller;
    }

    @Override
    public <T extends BlockEntity & IMultiBlockEntityContainer> T getControllerBE() {
        if (isController())
            return (T) this;
        BlockEntity blockEntity = world.getBlockEntity(controller);
        if (blockEntity instanceof LiquidBlazeBurnerEntity)
            return (T)(LiquidBlazeBurnerEntity) blockEntity;
        return null;
    }

    @Override
    public boolean isController() {
        return controller==null||pos.getX()==controller.getX()&& pos.getY() == controller.getY() && pos.getZ() == controller.getZ();
    }

    @Override
    public void setController(BlockPos controller) {
        if (world.isClient && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        refreshCapability();
        markDirty();
        sendData();
    }

    public FluidTank getTankInventory() {
        return tankInventory;
    }

    public LerpedFloat getFluidLevel() {
        return fluidLevel;
    }

    public void setFluidLevel(LerpedFloat fluidLevel) {
        this.fluidLevel = fluidLevel;
    }
    public void applyFluidTankSize(int blocks) {
        tankInventory.setCapacity(blocks * getCapacityMultiplier());
        long overflow = tankInventory.getFluidAmount() - tankInventory.getCapacity();
        if (overflow > 0)
            TransferUtil.extract(tankInventory, tankInventory.variant, overflow);
        forceFluidLevelUpdate = true;
    }
    @Override
    public void removeController(boolean keepFluids) {
        if (world.isClient)
            return;
        updateConnectivity = true;
        if (!keepFluids)
            applyFluidTankSize(1);
        controller = null;
        width = 1;
        height = 1;
        onFluidStackChanged(tankInventory.getFluid());


        refreshCapability();
        markDirty();
        sendData();
    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }
    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = pos;
    }
    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity=false;
    }
    public void queueConnectivityUpdate() {
        updateConnectivity = true;
    }

    @Override
    public void notifyMultiUpdated() {
        onFluidStackChanged(tankInventory.getFluid());
        markDirty();
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if(longAxis==Direction.Axis.Y){
            return getMaxHeight();
        }
        return getMaxWidth();
    }

    @Override
    public int getMaxWidth() {
        return MAX_WIDTH;
    }
    public int getMaxHeight(){return MAX_HEIGHT;}

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height=height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width=width;
    }

    @Override
    public boolean hasTank() {
        return true;
    }

    @Override
    public long getTankSize(int tank) {
        return getCapacityMultiplier();
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        applyFluidTankSize(blocks);
    }

    @Override
    public FluidTank getTank(int tank) {
        return tankInventory;
    }

    @Override
    public FluidStack getFluid(int tank) {
        return tankInventory.getFluid().copy();
    }

    @Override
    public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        if (exposedTank == null)
            refreshCapability();
        return exposedTank;
    }

    public enum FuelType {
        NONE, NORMAL, SPECIAL
    }

}
