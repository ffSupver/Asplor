package com.ffsupver.asplor.block.meltingFurnace;

import com.ffsupver.asplor.util.NbtUtil;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LargeMeltingFurnaceFluidPortEntity extends SmartBlockEntity implements SidedStorageBlockEntity {
    private BlockPos controllerPos;
    private final LargeMeltingFurnaceFluidPortStorage largeMeltingFurnaceFluidPortStorage = new LargeMeltingFurnaceFluidPortStorage();

    public LargeMeltingFurnaceFluidPortEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        return largeMeltingFurnaceFluidPortStorage;
    }

    public class LargeMeltingFurnaceFluidPortStorage implements Storage<FluidVariant>{

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            LargeMeltingFurnaceControllerEntity controller = getController();
            if (controller!=null){
               return controller.insert(resource,maxAmount,transaction);
            }
            return 0;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            LargeMeltingFurnaceControllerEntity controller = getController();
            if (controller!=null){
                return controller.extract(resource,maxAmount,transaction);
            }
            return 0;
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            LargeMeltingFurnaceControllerEntity controller = getController();
            if (controller!=null){
               return controller.getIterator();
            }
            return new ArrayList<StorageView<FluidVariant>>().iterator();
        }
    }
}
