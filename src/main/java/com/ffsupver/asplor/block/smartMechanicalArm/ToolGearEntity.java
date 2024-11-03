package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.item.item.ToolItem;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ToolGearEntity extends SmartBlockEntity implements SidedStorageBlockEntity , IWrenchable {
    private ToolItemStackHandler itemStackHandler;
    public ToolGearEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        itemStackHandler = new ToolItemStackHandler(4);
    }

    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        return itemStackHandler;
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        itemStackHandler.deserializeNBT(tag.getCompound("items"));
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("items",itemStackHandler.serializeNBT());
    }

    public boolean canDropTool(){
        for (int i = 0 ; i < itemStackHandler.getSlots().size(); i++){
            if (itemStackHandler.getStackInSlot(i).isOf(Items.AIR)){
                return true;
            }
        }
        return false;
    }

    public boolean dropTool(ToolType toolType,int usageRemain){
        ItemStack droppedTool = new ItemStack(toolType.getToolItem(),1);
        if (droppedTool.getItem() instanceof ToolItem toolItem){
           droppedTool = toolItem.setUse(droppedTool,usageRemain);
        }else {
            return false;
        }
        long droppedCount;
        try(Transaction t = Transaction.openOuter()) {
            droppedCount = this.itemStackHandler.insert(ItemVariant.of(droppedTool),droppedTool.getCount(),t);
            sendData();
            t.commit();
        }
        return droppedCount > 0;
    }

    public ArrayList<ToolType> getTools(){
        ArrayList<ToolType> tools = new ArrayList<>();
        for (int i = 0 ; i < itemStackHandler.getSlots().size(); i++){
            ItemStack itemStack = itemStackHandler.getStackInSlot(i);
             if(itemStack.getItem() instanceof ToolItem toolItem){
                 if (toolItem.getUse(itemStack) > 0) {
                     ToolType toolType =ToolType.getById(toolItem.getToolTypeId());
                     if (!toolType.equals(ToolTypes.EMPTY)){
                         tools.add(toolType);
                     }
                 }
            }
        }
        return tools;
    }

    public int getTool(ToolType toolType){
        for (int i = 0 ; i < itemStackHandler.getSlots().size(); i++){
            ItemStack itemStack = itemStackHandler.getStackInSlot(i);
             if(itemStack.getItem() instanceof ToolItem toolItem && ToolType.getById(toolItem.getToolTypeId()).equals(toolType)){
                int usage = toolItem.getUse(itemStack);
                 itemStack.setCount(itemStack.getCount() -1);
                sendData();
                return Math.max(usage,0);
            }
        }
        return 0;
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(world,pos,itemStackHandler);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    public class ToolItemStackHandler extends ItemStackHandler{

        public ToolItemStackHandler(int slots){
            super(slots);
        }
        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (resource.getItem() instanceof ToolItem){
                sendData();
                return super.insert(resource, maxAmount, transaction);
            }
            return 0L;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            sendData();
            return super.extract(resource, maxAmount, transaction);
        }

        @Override
        protected void onContentsChanged(int slot) {
            sendData();
            super.onContentsChanged(slot);
        }
    }
}
