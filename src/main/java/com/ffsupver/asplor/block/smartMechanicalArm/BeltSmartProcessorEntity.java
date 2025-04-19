package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.block.SmartEnergyStorage;
import com.ffsupver.asplor.item.item.SchematicItem;
import com.ffsupver.asplor.item.item.ToolItem;
import com.ffsupver.asplor.recipe.SmartProcessingRecipe;
import com.ffsupver.asplor.util.GoggleDisplays;
import com.ffsupver.asplor.util.RenderUtil;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;
import java.util.Optional;

import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.PASS;

public class BeltSmartProcessorEntity extends SmartBlockEntity implements SidedStorageBlockEntity, IHaveGoggleInformation {

    private final ToolGearEntity.ToolItemStackHandler itemStackHandler;
    protected BeltProcessingBehaviour beltProcessing;
    private String schematic;
    private static final int PROCESS_TICK = 40;
    private int processTick;
    private static final long ENERGY_PER_TICK = 25;
    private final SmartEnergyStorage energyStorage;
    public BeltSmartProcessorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        itemStackHandler = new ToolGearEntity.ToolItemStackHandler(1,this::notifyUpdate);
        this.processTick = PROCESS_TICK;
        energyStorage = new SmartEnergyStorage(2000,2000,2000,e->notifyUpdate());
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (schematic != null){
            tag.putString("schematic", schematic);
        }
        tag.putInt("process_tick",processTick);
        tag.put("item",itemStackHandler.serializeNBT());

        tag.put("energy",energyStorage.writeToNBT(new NbtCompound()));
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("schematic", NbtElement.STRING_TYPE)){
            schematic = tag.getString("schematic");
        }
        processTick = tag.getInt("process_tick");
        itemStackHandler.deserializeNBT(tag.getCompound("item"));

        energyStorage.readFromNBT(tag.getCompound("energy"));
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        beltProcessing = new BeltProcessingBehaviour(this).whenItemEnters(this::onItemEnters).whileItemHeld(this::whileItemHeld);
        behaviours.add(beltProcessing);
    }

    private BeltProcessingBehaviour.ProcessingResult whileItemHeld(TransportedItemStack transportedItemStack, TransportedItemStackHandlerBehaviour handler) {
        if (getToolType() == null){
            resetProcess();
            return PASS;
        }


        Optional<SmartProcessingRecipe> recipe = getRecipe(transportedItemStack,schematic);
        if (recipe.isPresent()){
                SmartProcessingRecipe smartProcessingRecipe = recipe.get();
                ToolType toolType = smartProcessingRecipe.getToolType(transportedItemStack.stack);
                if (getToolType().equals(toolType)){
                    if (hasEnergy()){
                        if (processTick > 0) {
                            costEnergy();
                            declineProcess();
                        } else {
                            resetProcess();
                            TransportedItemStack result = transportedItemStack.copy();
                            result.stack = smartProcessingRecipe.process(transportedItemStack.stack.copy());
                            if (result.stack != null && !result.stack.isEmpty() && damageTool()) {
                                handler.handleProcessingOnItem(transportedItemStack, TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(result));
                            }
                        }
                    }
                }

        }else {
            resetProcess();
        }


        notifyUpdate();
        return HOLD;
    }

    public void declineProcess(){
        if (processTick > 0){
            processTick--;
        }
    }

    public void resetProcess(){
        this.processTick = PROCESS_TICK;
    }

    private BeltProcessingBehaviour.ProcessingResult onItemEnters(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        if (handler.blockEntity.isVirtual())
            return PASS;
        if (getToolType() == null)
            return PASS;
        if (!getRecipe(transported,schematic).isPresent())
            return PASS;
        return HOLD;
    }



    private ToolType getToolType(){
        ItemStack toolStack = this.itemStackHandler.getStackInSlot(0);
        if (toolStack.getItem() instanceof ToolItem toolItem){
           return ToolType.getById(toolItem.getToolTypeId());
        }
        return null;
    }

    private boolean damageTool(){
        ItemStack toolStack = this.itemStackHandler.getStackInSlot(0);
        if (toolStack.getItem() instanceof ToolItem toolItem){
            this.itemStackHandler.setStackInSlot(0,toolItem.setUse(toolStack,toolItem.getUse(toolStack) - 1));
            notifyUpdate();
            return true;
        }else {
            return false;
        }
    }

    private Optional<SmartProcessingRecipe> getRecipe(TransportedItemStack transported,String schematic){
        return SmartMechanicalArmEntity.getCurrentRecipe(transported.stack,world,schematic);
    }

    private boolean hasEnergy(){
        return energyStorage.getAmount() >= ENERGY_PER_TICK;
    }

    private void  costEnergy(){
        energyStorage.extractEnergy(ENERGY_PER_TICK);
    }

    public int getProcessTick() {
        return processTick;
    }
    public int getMaxProcessTick() {
        return PROCESS_TICK;
    }

    public PartialModel getToolModel(boolean work){
       ItemStack itemStack = this.itemStackHandler.getStackInSlot(0);
       if (itemStack.getItem() instanceof ToolItem toolItem){
          return work ? toolItem.getToolWorkModel() : toolItem.getToolModel();
       }
       return null;
    }

    public boolean setSchematic(ItemStack itemStack){
        if (itemStack.getItem() instanceof SchematicItem){
           schematic = SchematicItem.getSchematicFromItem(itemStack);
           return true;
        }
        return false;
    }



    public ItemStack removeSchematic(){
        if (schematic != null){
            ItemStack schematicItem = SchematicItem.getSchematicItem(schematic);
            schematic = null;
           return schematicItem;
        }else {
            return ItemStack.EMPTY;
        }
    }

    public long insertTool(ItemStack itemStack){
        long inserted;
        try(Transaction t = Transaction.openOuter()) {
            inserted = itemStackHandler.insert(ItemVariant.of(itemStack),itemStack.getCount(),t);
            t.commit();
        }
        return inserted;
    }

    public ItemStack extractToolItem(){
        ItemStack toolStack = itemStackHandler.getStackInSlot(0);
        long count = 0;
        if (!toolStack.isEmpty()) {
            try (Transaction t = Transaction.openOuter()) {
                count = itemStackHandler.extract(ItemVariant.of(toolStack),toolStack.getCount(),t);
                t.commit();
            }
        }
        return toolStack.copyWithCount((int) count);
    }

    @Override
    public Box getRenderBoundingBox() {
        return RenderUtil.createRenderBoundingBox(pos,9.0);
    }

    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        return itemStackHandler;
    }

    public EnergyStorage getEnergyStorage(Direction direction) {
        if (direction.equals(Direction.UP)){
            return energyStorage;
        }
        return null;
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        return GoggleDisplays.addEnergyDisplayToGoggles(tooltip,energyStorage);
    }

    @Override
    public void destroy() {
        if (!getWorld().isClient()){
            ItemHelper.dropContents(getWorld(), getPos(), itemStackHandler);
            ItemScatterer.spawn(getWorld(),getPos().getX(),getPos().getY(),getPos().getZ(),removeSchematic());
        }
        super.destroy();
    }
}
