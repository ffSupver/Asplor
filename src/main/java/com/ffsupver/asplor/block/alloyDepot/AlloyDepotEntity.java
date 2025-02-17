package com.ffsupver.asplor.block.alloyDepot;

import com.ffsupver.asplor.item.item.SchematicItem;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlloyDepotEntity extends SmartBlockEntity implements SidedStorageBlockEntity , IWrenchable {
    AlloyDepotBehaviour behaviour;
    private String schematic;
    public static final String EMPTY_SCHEMATIC = "empty" ;
    public AlloyDepotEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        schematic = EMPTY_SCHEMATIC;
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putString("schematic", schematic);
    }


    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("schematic", NbtElement.STRING_TYPE)){
            schematic = tag.getString("schematic");
        }
    }

    public void setSchematic(String schematic) {
        this.schematic = schematic;
        BlockState newState = getCachedState().with(AlloyDepot.SCHEMATIC,!schematic.equals(EMPTY_SCHEMATIC));
        if (!newState.equals(getCachedState())){
            world.setBlockState(pos,newState);
        }
        markDirty();
        sendData();
    }

    public String getSchematic() {
        return schematic.equals(EMPTY_SCHEMATIC) ? null : schematic;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(behaviour = new AlloyDepotBehaviour(this));
        behaviour.addSubBehaviours(behaviours);
    }

    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        return behaviour.itemHandler;
    }

    public ItemStack getHeldItem(){
        return behaviour.getHeldItemStack();
    }

    public long insertToOutput(ItemVariant resource, long amount,boolean simulate){
        long result;
        try(Transaction t = Transaction.openOuter()) {
            result = behaviour.processingOutputBuffer.insert(resource, amount, t);
            if (simulate){
                t.abort();
            }else {
                t.commit();
            }
        }
        return result;
    }

    @Override
    public void destroy() {
        if (getSchematic() != null){
            ItemScatterer.spawn(world,pos.getX(),pos.getY(),pos.getZ(),SchematicItem.getSchematicItem(getSchematic()));
        }
        super.destroy();
    }
}
