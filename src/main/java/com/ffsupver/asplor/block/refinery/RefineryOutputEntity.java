package com.ffsupver.asplor.block.refinery;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class RefineryOutputEntity extends SmartBlockEntity implements SidedStorageBlockEntity , IHaveGoggleInformation {
    private final SmartFluidTank fluidTank;
    private RefineryOutputFluidHandler fluidHandler;
    public RefineryOutputEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        fluidTank = new SmartFluidTank(81000,this::onFluidStackChange);
        fluidHandler = new RefineryOutputFluidHandler();
    }

    private void onFluidStackChange(FluidStack fluidStack){
        if (!hasWorld()){
            return;
        }
        if (!world.isClient()){
            sendData();
            markDirty();
        }
    }

    public SmartFluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.fluidTank.readFromNBT(tag.getCompound("fluid"));
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("fluid",this.fluidTank.writeToNBT(new NbtCompound()));
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip,isPlayerSneaking,fluidTank);
    }

    @Override
    public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        return fluidHandler;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    private class RefineryOutputFluidHandler implements Storage<FluidVariant>{

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return fluidTank.extract(resource,maxAmount,transaction);
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            return fluidTank.iterator();
        }
    }
}
