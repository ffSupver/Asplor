package com.ffsupver.asplor.block.meltingFurnace;

import com.ffsupver.asplor.util.NbtUtil;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LargeMeltingFurnaceItemPortEntity extends SmartBlockEntity implements SidedStorageBlockEntity {
    private BlockPos controllerPos;
    public LargeMeltingFurnaceItemPortEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        controllerPos = pos;
    }

    public void removeController(){
        this.controllerPos = pos;
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        controllerPos = NbtUtil.readBlockPosFromNbt(tag.getCompound("controller"));
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("controller", NbtUtil.writeBlockPosToNbt(controllerPos));
    }

    private boolean hasController(){
        return controllerPos != pos;
    }

    private LargeMeltingFurnaceControllerEntity getController() {
        if (hasController() && world.getBlockEntity(controllerPos) instanceof LargeMeltingFurnaceControllerEntity largeMeltingFurnanceControllerEntity){
            return largeMeltingFurnanceControllerEntity;
        }
        return null;
    }

    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        if (getController() != null){
            return getController().getItemHandler();
        }
        return null;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}
