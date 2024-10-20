package com.ffsupver.asplor.block.energyOutputer;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkPowerBlockEntity;
import com.ffsupver.asplor.AllBlockEntityTypes;
import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.ModTags;
import com.ffsupver.asplor.util.MathUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import team.reborn.energy.api.EnergyStorage;

import java.util.EnumSet;

import static com.ffsupver.asplor.util.MathUtil.fromAEtoE;
import static com.ffsupver.asplor.util.MathUtil.fromEtoAE;

public class EnergyOutputEntity extends AENetworkPowerBlockEntity implements IGridTickable {
    private static final long MAX_EXTRACT= (long) 1e7;
    public EnergyOutputEntity( BlockPos pos, BlockState blockState) {
        super(AllBlockEntityTypes.ENERGY_OUTPUT_ENTITY, pos, blockState);
        this.getMainNode().setFlags(new GridFlags[0]).setIdlePowerUsage(0.0).addService(IGridTickable.class, this);
        this.setInternalMaxPower(20000.0);
        this.setPowerSides(EnumSet.complementOf(EnumSet.of(Direction.DOWN)));
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public InternalInventory getInternalInventory() {
        return InternalInventory.empty();
    }

    public void writeNbt(NbtCompound data){
        super.writeNbt(data);
    }

    @Override
    public void loadTag(NbtCompound data) {
        super.loadTag(data);
    }

    @Override
    protected Item getItemFromBlockEntity() {
        return AllBlocks.ENERGY_OUTPUT.asItem();
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(5,5,false,true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        this.tryTransferEnergyToNeighbor();
        return TickRateModulation.FASTER;
    }

    private void tryTransferEnergyToNeighbor(){
        for (Direction direction : Direction.values()) {

            BlockPos neighborPos = pos.offset(direction);
            EnergyStorage neighborStorage = EnergyStorage.SIDED.find(world, neighborPos, direction.getOpposite());

            if (this.world != null && this.world.getBlockState(neighborPos).isIn(ModTags.Blocks.NEED_ENERGY) &&
                    neighborStorage != null) {
                long energyNeedToTransfer = Math.min(neighborStorage.getCapacity()-neighborStorage.getAmount(),MAX_EXTRACT);
                double leftPowerForAE = 4000;
                this.getMainNode().ifPresent((gridx) -> {
                    double toExtract = Math.min(fromEtoAE(energyNeedToTransfer),gridx.getEnergyService().getStoredPower()-leftPowerForAE);
                    double toExtract1 = Math.max(toExtract,0);
                    double extracted = gridx.getEnergyService().extractAEPower(toExtract1, Actionable.MODULATE, PowerMultiplier.ONE);
                    try(Transaction t=Transaction.openOuter()) {
                        neighborStorage.insert((long) fromAEtoE(extracted),t);
                        t.commit();
                    }
                });

            }
        }
    }


}
