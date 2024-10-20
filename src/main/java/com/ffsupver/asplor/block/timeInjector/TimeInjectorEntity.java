package com.ffsupver.asplor.block.timeInjector;

import com.ffsupver.asplor.block.SmartEnergyStorage;
import com.ffsupver.asplor.recipe.FluidInventory;
import com.ffsupver.asplor.recipe.ModRecipes;
import com.ffsupver.asplor.recipe.TimeInjectorRecipe;
import com.ffsupver.asplor.util.RenderUtil;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import com.ffsupver.asplor.util.GoggleDisplays;
import net.minecraft.util.math.Box;

import java.util.*;

public class TimeInjectorEntity extends SmartBlockEntity implements IWrenchable, IHaveGoggleInformation {
    public final SmartEnergyStorage energyStorage;
    private static final long CAPACITY=500000;
    private static final long MAX_TRANSFER =CAPACITY;

    private static final int MAX_DETECT_DISTANCE=5;


    private ArrayList<BlockPos> workArea;
    private int process ;
    private final int processTime = 200;
    private boolean workAreaChanged ;
    //渲染用变量
    private int renderProcess;
    private Map<BlockPos,ItemStack> renderWorkArea;
    private final Double RENDER_DISTANCE = 128.0;
    public TimeInjectorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.energyStorage=createInventory();
        this.process=processTime;
        this.renderProcess=process;
        this.workArea=new ArrayList<>();
        this.renderWorkArea=new HashMap<>();
        this.workAreaChanged =false;
    }
    protected SmartEnergyStorage createInventory() {
        return new SmartEnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER,this::onEnergyLevelChanged);
    }

    protected void onEnergyLevelChanged(long newEnergyLevel) {
        markDirty();
        if (!world.isClient()) {
            sendData();
        }
    }

    public int getRenderProcess(){
        return renderProcess;
    }

    public int getProcessTime() {
        return processTime;
    }

    public Map<BlockPos, ItemStack> getRenderWorkArea() {
        return renderWorkArea;
    }

    public ArrayList<BlockPos> getWorkArea() {
        return workArea;
    }

    @Override
    public void tick() {
        super.tick();
        if (world==null){
            return;
        }
        if (world.isClient()){
//            System.out.println("p"+process+"  "+workArea.toString()+"   E   "+energyStorage.getAmount());
        }
        if (process==processTime){
            renderProcess=processTime;
        }else {
            renderProcess--;
        }

        if (energyStorage.getAmount()<CAPACITY-1){
            return;
        }
        if (workAreaChanged){
            process=processTime;
            renderProcess=process;
            workAreaChanged=false;
        }
        if (checkFluid()&&world.getTime()%10==0){
            process-=10;
            renderProcess=process;
        }
        if (process<=0){
            process=processTime;
            renderProcess=process;
            try (Transaction t = Transaction.openOuter()){
                energyStorage.extract(CAPACITY-1,t);
                t.commit();

            }
            craftBlock();
        }
        if (world.getTime()%40==0) {
        }
    }

    private boolean hasRecipe(BlockPos blockPos){
        if (world != null && world.getBlockState(blockPos).getBlock() instanceof FluidBlock) {
            BlockState blockState = world.getBlockState(blockPos);
            FluidState fluidState = ((FluidBlock) blockState.getBlock()).getFluidState(blockState);
            Block blockDown =world.getBlockState(blockPos.down()).getBlock();
            if (fluidState.isStill()&&getCurrentRecipe(fluidState.getFluid(),blockDown).isPresent()){
                return true;
            }
        }
        return false;
    }
    private Optional<TimeInjectorRecipe> getCurrentRecipe(Fluid fluid, Block block){
       FluidInventory testInv = new FluidInventory(1,1000L);
       testInv.insertFluid(0,FluidVariant.of(fluid),1000);
       testInv.setStack(0,block.asItem().getDefaultStack());
       return world.getRecipeManager().getFirstMatch(ModRecipes.TIME_INJECTOR_RECIPETYPE,testInv,world);
    }

    private Optional<TimeInjectorRecipe> getCurrentRecipe(BlockPos blockPos){
        if (world != null && world.getBlockState(blockPos).getBlock() instanceof FluidBlock) {
            BlockState blockState = world.getBlockState(blockPos);
            FluidState fluidState = ((FluidBlock) blockState.getBlock()).getFluidState(blockState);
            Block blockDown = world.getBlockState(blockPos.down()).getBlock();
            return getCurrentRecipe(fluidState.getFluid(), blockDown);
        }
        return Optional.empty();
    }
    private void craftBlock() {
        BlockPos down = pos.down();
        for (int i = -MAX_DETECT_DISTANCE;i<MAX_DETECT_DISTANCE;i++) {
            for (int j = -MAX_DETECT_DISTANCE; j < MAX_DETECT_DISTANCE; j++) {
                BlockPos checkPos = down.add(i,0,j);
                if (hasRecipe(checkPos)){
                    Block blockToPlace =Blocks.AIR;
                    ItemStack resultStack = getCurrentRecipe(checkPos).get().getOutput(null);
                    if (resultStack.getItem() instanceof BlockItem){
                        blockToPlace=((BlockItem)resultStack.getItem()).getBlock();
                    }
                    if (!blockToPlace.equals(Blocks.AIR)) {
                        world.playSound(checkPos.getX()+0.5f,checkPos.getY()+0.5f,checkPos.getZ()+0.5f, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS,1.5F , 0.9F,false);
                        world.setBlockState(checkPos,blockToPlace.getDefaultState());
                    }
                }
            }
        }
    }

    private boolean checkFluid() {
        BlockPos down = pos.down();
        ArrayList<BlockPos> oldWorkArea = new ArrayList<>(workArea);
        renderWorkArea.clear();
        workArea.clear();
        for (int i = -MAX_DETECT_DISTANCE;i<MAX_DETECT_DISTANCE;i++) {
            for (int j = -MAX_DETECT_DISTANCE; j < MAX_DETECT_DISTANCE; j++) {
                BlockPos checkPos = down.add(i,0,j);
                if (hasRecipe(checkPos)){
                    workArea.add(checkPos);
                    renderWorkArea.put(checkPos,getCurrentRecipe(checkPos).get().getOutput(null));
                }
            }
        }
        if (!oldWorkArea.equals(workArea)){
            workAreaChanged=true;
        }
        return !workArea.isEmpty();
    }

    private BlockPos readBlockPos(NbtCompound nbtCompound){
        return new BlockPos(nbtCompound.getInt("x"),nbtCompound.getInt("y"),nbtCompound.getInt("z"));
    }

    private NbtCompound writeBlockPos(BlockPos blockPos,NbtCompound nbtCompound){
        nbtCompound.putInt("x",blockPos.getX());
        nbtCompound.putInt("y",blockPos.getY());
        nbtCompound.putInt("z",blockPos.getZ());
        return nbtCompound;
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        energyStorage.readFromNBT(tag.getCompound("Energy"));
        process = tag.getInt("process");


        workArea.clear();
        NbtList workArea = tag.getList("work_area",10);
        for (NbtElement compound:workArea){
           this.workArea.add(readBlockPos((NbtCompound)compound));
        }


    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("Energy",energyStorage.writeToNBT(new NbtCompound()));
        tag.putInt("process",process);

        NbtList workArea = new NbtList();
        for (BlockPos pos1 : this.workArea){
           workArea.add(writeBlockPos(pos1,new NbtCompound()));
        }
        tag.put("work_area",workArea);

    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}
    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        if (this.energyStorage==null){
            return false;
        }
        return GoggleDisplays.addEnergyDisplayToGoggles(tooltip,energyStorage);
    }

    public long getAmount(){
        return this.energyStorage.getAmount();
    }
    public long getCapacity(){
        return this.energyStorage.getCapacity();
    }

    public SmartEnergyStorage getEnergyStorage() {
        return energyStorage;
    }


    //视锥外不剔除

    @Override
    protected Box createRenderBoundingBox() {
        return RenderUtil.createRenderBoundingBox(pos,RENDER_DISTANCE);
    }
}
