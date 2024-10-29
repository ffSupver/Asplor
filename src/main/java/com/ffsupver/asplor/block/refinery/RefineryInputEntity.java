package com.ffsupver.asplor.block.refinery;

import com.ffsupver.asplor.util.NbtUtil;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class RefineryInputEntity extends SmartBlockEntity implements SidedStorageBlockEntity {
    private BlockPos controllerPos;
    private RefineryInputFluidHandler fluidHandler;
    public RefineryInputEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.controllerPos = pos;
        this.fluidHandler=new RefineryInputFluidHandler();
    }

    @Override
    public void tick() {
        super.tick();

    }

    private void checkControllerPos(){
        if (getControllerEntity()==null){
            controllerPos = pos;
        }
    }

    private RefineryControllerEntity getControllerEntity(){
        BlockEntity blockEntity = world.getBlockEntity(controllerPos);
        if (blockEntity instanceof RefineryControllerEntity){
            return  (RefineryControllerEntity) blockEntity;
        }
        return null;
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        controllerPos = NbtUtil.readBlockPosFromNbt(tag.getCompound("controller"));
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("controller",NbtUtil.writeBlockPosToNbt(controllerPos));
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }

    private boolean hasController(){
        checkControllerPos();
        return controllerPos != pos;
    }

    @Override
    public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        return fluidHandler;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
    private class RefineryInputFluidHandler implements Storage<FluidVariant>{

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            if (hasController()){
                return getControllerEntity().getFluidTank().insert(resource,maxAmount,transaction);
            }
            return 0;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            if (hasController()){
                return getControllerEntity().getFluidTank().extract(resource,maxAmount,transaction);
            }
            return 0;
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            if (hasController()){
               return getControllerEntity().getFluidTank().iterator();
            }
            return null;
        }
    }
}
