package com.ffsupver.asplor.block.electrolyzer;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.block.SmartEnergyStorage;
import com.ffsupver.asplor.recipe.ElectrolyzerRecipe;
import com.ffsupver.asplor.recipe.FluidInventory;
import com.ffsupver.asplor.recipe.ModRecipes;
import com.ffsupver.asplor.sound.ModSounds;
import com.ffsupver.asplor.util.GoggleDisplays;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ElectrolyzerEntity extends SmartBlockEntity implements SidedStorageBlockEntity, IWrenchable, IHaveGoggleInformation {
    private SmartFluidTank inputTank;
    private FluidTank outputTankA;
    private FluidTank outputTankB;
    private ElectrolyzerFluidHandler inputTankHandler;
    private ElectrolyzerFluidHandler outputTankAHandler;
    private ElectrolyzerFluidHandler outputTankBHandler;
    private final long TO_MB = 81;
    private final long TANK_CAPACITY = 1000*TO_MB;
    private final long ENERGY_CAPACITY = 10000;
    private SmartEnergyStorage energyStorage;
    private final long ENERGY_PER_CRAFT = 500;
    private int coolDown;
    private final int COOL_DOWN = 20;

    private boolean hasOutputTank;

    protected int luminosity;
    public ElectrolyzerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inputTank = new SmartFluidTank(TANK_CAPACITY,this::onFluidChange);
        this.outputTankA = new SmartFluidTank(TANK_CAPACITY,this::onFluidChange);
        this.outputTankB = new SmartFluidTank(TANK_CAPACITY,this::onFluidChange);
        this.inputTankHandler = new ElectrolyzerFluidHandler(inputTank,true);
        this.outputTankAHandler = new ElectrolyzerFluidHandler(outputTankA,false);
        this.outputTankBHandler = new ElectrolyzerFluidHandler(outputTankB,false);
        this.energyStorage = new SmartEnergyStorage(ENERGY_CAPACITY,ENERGY_CAPACITY,ENERGY_CAPACITY,this::onEnergyChange);
        this.coolDown = COOL_DOWN;
    }


    private void onEnergyChange(Long changed){
        sendData();
    }

    @Override
    public void tick() {
        super.tick();

        updateOutputTank();
        if (getPart() == Electrolyzer.ElectrolyzerPart.LOWER ){
            if (this.coolDown <= 0){
                this.coolDown = COOL_DOWN;
                if (hasOutputTank && hasEnergy() && getCurrentRecipe().isPresent() && canInsert()){
                    craft();
                    playSound();
                }
            }else {
                coolDown--;
            }
        }
    }

    private void playSound(){
        if (world.isClient()){
            float pitch = MathHelper.clamp(0.5f+ .45f, .85f, 1f);
            if (world != null) {
                world.playSound(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5, ModSounds.ELECTRICITY_WORK,
                        SoundCategory.BLOCKS,0.2f,pitch,false);
            }
        }
    }

    protected void setLuminosity(int luminosity) {
        if (world.isClient)
            return;
        if (this.luminosity == luminosity)
            return;
        this.luminosity = luminosity;
        updateStateLuminosity();
    }

    private void onFluidChange(FluidStack stack){
        if (!hasWorld()){
            return;
        }
        FluidVariantAttributeHandler handler = FluidVariantAttributes.getHandlerOrDefault(stack.getFluid());
        FluidVariant variant = stack.getType();
        int actualLuminosity = (int) (handler.getLuminance(variant) / 1.2f);
        if (this.luminosity != actualLuminosity){
            setLuminosity(actualLuminosity);
        }
    }

    protected void updateStateLuminosity() {
        if (world.isClient)
            return;
        int actualLuminosity = luminosity;
        refreshBlockState();
        BlockState state = getCachedState();
        if (state.get(Electrolyzer.LIGHT_LEVEL) != actualLuminosity) {
            world.setBlockState(pos, state.with(FluidTankBlock.LIGHT_LEVEL, actualLuminosity), 23);
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        if (getPart() == Electrolyzer.ElectrolyzerPart.LOWER)
             GoggleDisplays.addEnergyDisplayToGoggles(tooltip,energyStorage);
        return containedFluidTooltip(tooltip,isPlayerSneaking,getFluidStorage(null));
    }



    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);

        int prevLum = luminosity;
        luminosity = tag.getInt("Luminosity");
        if (clientPacket && luminosity != prevLum && hasWorld())
            world.getChunkManager()
                    .getLightingProvider()
                    .checkBlock(pos);

        switch (getCachedState().get(Electrolyzer.PART)){
            case LOWER -> {
                inputTank.readFromNBT(tag.getCompound("input_tank"));
                energyStorage.readFromNBT(tag.getCompound("energy"));
            }
            case MIDDLE -> outputTankA.readFromNBT(tag.getCompound("output_tank_a"));
            case UPPER -> outputTankB.readFromNBT(tag.getCompound("output_tank_b"));
        }
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);

        tag.putInt("Luminosity", luminosity);

        switch (getCachedState().get(Electrolyzer.PART)){
            case LOWER -> {
                tag.put("input_tank",inputTank.writeToNBT(new NbtCompound()));
                tag.put("energy",energyStorage.writeToNBT(new NbtCompound()));
            }
            case MIDDLE -> tag.put("output_tank_a",outputTankA.writeToNBT(new NbtCompound()));
            case UPPER -> tag.put("output_tank_b",outputTankB.writeToNBT(new NbtCompound()));
        }
    }

    private Optional<ElectrolyzerRecipe> getCurrentRecipe(){
        FluidInventory testInventory = new FluidInventory(0,1);
        testInventory.insertFluid(0,this.inputTank.getFluid().getType(),this.inputTank.getFluidAmount());
        return world.getRecipeManager().getFirstMatch(ModRecipes.ELECTROLYZER_RECIPETYPE,testInventory,world);
    }


    private void craft(){
        ElectrolyzerRecipe recipe = getCurrentRecipe().get();
        try (Transaction t = Transaction.openOuter()){
            this.inputTank.extract(recipe.getRecipeFluids().getStoredFluid(0),recipe.getRecipeFluids().getStoredAmount(0),t);
            getBlockEntity(pos.up(), Electrolyzer.ElectrolyzerPart.MIDDLE).outputTankA.insert(FluidVariant.of(recipe.getOutputFluidA()),recipe.getOutputAmountA(),t);
            getBlockEntity(pos.up().up(), Electrolyzer.ElectrolyzerPart.UPPER).outputTankB.insert(FluidVariant.of(recipe.getOutputFluidB()),recipe.getOutputAmountB(),t);
            this.energyStorage.extract(ENERGY_PER_CRAFT,t);
            sendData();
            t.commit();
        }
    }

    private boolean canInsert(){
        ElectrolyzerRecipe recipe = getCurrentRecipe().get();
        FluidTank upperTank = getBlockEntity(pos.up().up(), Electrolyzer.ElectrolyzerPart.UPPER).outputTankB;
        FluidTank middleTank = getBlockEntity(pos.up(), Electrolyzer.ElectrolyzerPart.MIDDLE).outputTankA;
        return middleTank.getCapacity() - middleTank.getFluidAmount() >= recipe.getOutputAmountA() &&
                (middleTank.getFluid().getFluid().equals(recipe.getOutputFluidA()) || middleTank.getFluid().getType().isBlank()) &&
                upperTank.getCapacity() - upperTank.getFluidAmount() >= recipe.getOutputAmountB() &&
                (upperTank.getResource().equals(FluidVariant.of(recipe.getOutputFluidB())) || upperTank.getResource().isBlank());
    }

    private boolean hasEnergy(){
        return this.energyStorage.getAmount()>ENERGY_PER_CRAFT;
    }

    private void updateOutputTank(){
       hasOutputTank = !(getBlockEntity(pos.up(), Electrolyzer.ElectrolyzerPart.MIDDLE)==null||
               getBlockEntity(pos.up().up(), Electrolyzer.ElectrolyzerPart.UPPER) == null);
    }

    private ElectrolyzerEntity getBlockEntity(BlockPos pos, Electrolyzer.ElectrolyzerPart part){
        if (world != null) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ElectrolyzerEntity){
                if (blockEntity.getCachedState().contains(Electrolyzer.PART) && blockEntity.getCachedState().get(Electrolyzer.PART).equals(part)){
                    return (ElectrolyzerEntity) blockEntity;
                }
            }
        }
        return null;
    }


    public SmartEnergyStorage getEnergyStorage() {
        return this.getPart().equals(Electrolyzer.ElectrolyzerPart.LOWER) ? energyStorage : null;
    }

    public Electrolyzer.ElectrolyzerPart getPart(){
        return this.getCachedState().get(Electrolyzer.PART);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        return switch (getCachedState().get(Electrolyzer.PART)){
            case LOWER -> inputTankHandler;
            case MIDDLE -> outputTankAHandler;
            case UPPER -> outputTankBHandler;
        };
    }

    public FluidStack getFluidStackForRender(){
       return  ((ElectrolyzerFluidHandler)this.getFluidStorage(null)).getTank().getFluid();
    }

    private class ElectrolyzerFluidHandler implements Storage<FluidVariant>{
        private FluidTank tank;
        private boolean canInsert;
        public ElectrolyzerFluidHandler(FluidTank tank, boolean canInsert){
            this.tank = tank;
            this.canInsert = canInsert;
        }



        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            if (canInsert){
                sendData();
                return tank.insert(resource, maxAmount, transaction);
            }
            return 0L;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            sendData();
            return tank.extract(resource,maxAmount,transaction);
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            return tank.iterator();
        }

        public FluidTank getTank() {
            return tank;
        }
    }

}
