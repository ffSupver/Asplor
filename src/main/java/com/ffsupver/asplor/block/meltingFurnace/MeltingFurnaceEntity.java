package com.ffsupver.asplor.block.meltingFurnace;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.recipe.FluidInventory;
import com.ffsupver.asplor.recipe.MeltRecipe;
import com.ffsupver.asplor.recipe.ModRecipes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class MeltingFurnaceEntity extends SmartBlockEntity implements SidedStorageBlockEntity , IWrenchable {
    private int process;
    private int processTime;
    private final ItemStackHandler inventory;
    private final MeltingFurnaceInventoryHandler inventoryHandler;
    private BlockPos outputPos;
    public MeltingFurnaceEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventoryHandler = new MeltingFurnaceInventoryHandler();
        this.inventory = new ItemStackHandler(1);
        this.processTime = 100;
        this.process = processTime;
    }

    @Override
    public void tick() {
        super.tick();


        if (process < 0){
            if (hasRecipe() && hasOutputContainer()){
                craft();
                process = processTime;
            }
        }

        if (hasRecipe()){
            updateProcessTime();
            int processSpeed = 1;
            int addProcess = switch (getHeatLevel()){
                case KINDLED -> processSpeed;
                case SEETHING -> 2 * processSpeed;
                default -> 0;
            };

            int needProcess = switch (getHeatType()){
                case KINDLED -> 0;
                case SEETHING -> processSpeed;
                default -> 0;
            };

            process -= Math.max(addProcess - needProcess, 0);
        }else {
            process = processTime;
        }



    }

    public ItemStack getItemForRender(){
        return this.inventory.getStackInSlot(0);
    }

    private boolean hasOutputContainer(){
        for (Direction direction : Direction.values()){
           BlockPos outputPos = pos.offset(direction);
            BlockEntity blockEntity = world.getBlockEntity(outputPos);
           if (blockEntity instanceof FluidTankBlockEntity){
               FluidTankBlockEntity fluidTankBlockEntity = (FluidTankBlockEntity) blockEntity;
               FluidTank fluidTank = (FluidTank) fluidTankBlockEntity.getFluidStorage(direction.getOpposite());
               Fluid fluid = fluidTank.getFluid().getFluid();
               if (fluid.equals(getCurrentRecipe().get().getOutputFluid())||fluid.equals(Fluids.EMPTY)) {
                   this.outputPos = outputPos;
                   return true;
               }
           }
        }
        return false;
    }

    private void updateProcessTime(){
        int newProcessTime = getCurrentRecipe().get().getProcessTime();
        if (processTime != newProcessTime) {
            processTime = newProcessTime;
            process = processTime;
        }
    }

    private boolean hasRecipe(){
        return getCurrentRecipe().isPresent();
    }

    private BlazeBurnerBlock.HeatLevel getHeatType(){
        return switch (getCurrentRecipe().get().getHeatType()) {
            case "normal" -> BlazeBurnerBlock.HeatLevel.KINDLED;
            case "super" -> BlazeBurnerBlock.HeatLevel.SEETHING;
            default -> BlazeBurnerBlock.HeatLevel.NONE;
        };
    }

    private Optional<MeltRecipe> getCurrentRecipe(){
        FluidInventory testInventory = new FluidInventory(1,100000L);
        testInventory.setStack(0,this.inventory.getStackInSlot(0));
        return getWorld().getRecipeManager().getFirstMatch(ModRecipes.MELT_RECIPETYPE,testInventory,getWorld());
    }

    private BlazeBurnerBlock.HeatLevel getHeatLevel(){
        BlockState blockState = world.getBlockState(pos.down());
        if (blockState.contains(BlazeBurnerBlock.HEAT_LEVEL)) {
            return blockState.get(BlazeBurnerBlock.HEAT_LEVEL);
        }
        return BlazeBurnerBlock.HeatLevel.NONE;
    }

    private void craft(){
        FluidTankBlockEntity outputEntity = (FluidTankBlockEntity) world.getBlockEntity(outputPos);
        FluidTank outputStorage = (FluidTank) outputEntity.getFluidStorage(null);
        Fluid fluidToCraft = getCurrentRecipe().get().getOutputFluid();
        long amountToCraft = getCurrentRecipe().get().getOutputAmount();
        if (outputStorage.getCapacity()-outputStorage.getFluidAmount() >= amountToCraft) {
            try (Transaction t = Transaction.openOuter()) {
                outputStorage.insert(FluidVariant.of(fluidToCraft), amountToCraft, t);
                this.inventoryHandler.extract(this.inventory.getSlot(0).getResource(),1,t);
                t.commit();
            }
        }
    }

    public int getProcess() {
        return process;
    }

    public int getProcessTime() {
        return processTime;
    }

    @Override
    public void destroy() {
        ItemHelper.dropContents(this.getWorld(),this.getPos(),inventory);
        super.destroy();
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.process = tag.getInt("process");
        this.inventory.deserializeNBT(tag.getCompound("inventory"));

    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("process",process);
        tag.put("inventory",this.inventory.serializeNBT());
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        return inventoryHandler;
    }

    private class MeltingFurnaceInventoryHandler implements Storage<ItemVariant> {
        public long extract(ItemVariant resource, long maxAmount){
            long extracted = 0 ;
            try (Transaction t = Transaction.openOuter()) {
                extracted = this.extract(resource,maxAmount,t);
                t.commit();
            }
            return extracted;
        }

        public long insert(ItemVariant resource, long maxAmount){
            long inserted = 0 ;
            try (Transaction t = Transaction.openOuter()) {
                inserted = this.insert(resource,maxAmount,t);
                t.commit();
            }
            return inserted;
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            sendData();
            return inventory.insert(resource,maxAmount,transaction);
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return inventory.extract(resource,maxAmount,transaction);
        }

        @Override
        public Iterator<StorageView<ItemVariant>> iterator() {
            return inventory.iterator();
        }
    }
}
