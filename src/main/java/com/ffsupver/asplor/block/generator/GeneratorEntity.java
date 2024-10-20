package com.ffsupver.asplor.block.generator;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;

import static com.ffsupver.asplor.block.generator.Generator.FACING;

public class GeneratorEntity extends KineticBlockEntity {
    protected static final long CAPACITY = 100000;
    private static final long MAX_INSERT= 10000;
    private static final long MAX_EXTRACT= 10000;
    public final SimpleEnergyStorage energyInventory =new SimpleEnergyStorage(CAPACITY,MAX_INSERT,MAX_EXTRACT);
    protected int energyPerTickPerRPM = 1;



    public GeneratorEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    private float getSpeedABS(){
        return Math.abs(this.getSpeed());
    }
    private long getEnergyPerTick(){
        return Math.max(
                (long) (energyPerTickPerRPM*getSpeedABS()*getSpeedABS()/2),1);
    }
    private void genEnergy(){
        try(Transaction t=Transaction.openOuter()){
        energyInventory.insert(getEnergyPerTick(),t);
        markDirty();
        t.commit();
        }

    }


    private void transferEnergyToNeighbors() {
        for (Direction direction : Direction.values()) {
            if (canTransferEnergy(direction)) {
                BlockPos neighborPos = pos.offset(direction);
                EnergyStorage neighborStorage = EnergyStorage.SIDED.find(world, neighborPos, direction.getOpposite());

                if (neighborStorage != null && energyInventory.amount > 0) {
                    // 计算要传输的能量量
                    long extractableEnergy = Math.min(energyInventory.amount, energyInventory.maxExtract);
                    try (Transaction t = Transaction.openOuter()) {
                        long acceptedEnergy = neighborStorage.insert(extractableEnergy, t);
                        // 从当前能量存储中减少传输的能量
                        energyInventory.amount -= acceptedEnergy;
                        markDirty();
                        t.commit();
                    }

                }
            }
        }
    }



    @Override
    public void tick() {
        super.tick();
        // 向邻近方块传输能量
        transferEnergyToNeighbors();

        if(getSpeed()==0){
            return;
        }

        //发电
        genEnergy();

    }

    @Override
    public void tickAudio() {
        super.tickAudio();
    }

    @Override
    protected void write(NbtCompound compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        NbtCompound energyTag = new NbtCompound();
        energyTag.putLong("Amount",this.energyInventory.amount);
        energyTag.putLong("Capacity",this.energyInventory.capacity);

        compound.put("Energy",energyTag);
        if (clientPacket){

        }

    }

    @Override
    protected void read(NbtCompound compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        // 检查是否存在 Energy 标签，并确保其是复合标签（Compound）
        if (compound.contains("Energy", NbtElement.COMPOUND_TYPE)) {
            NbtCompound energyTag = compound.getCompound("Energy");

            // 从子标签中读取 amount 和 capacity
            long amount = energyTag.getLong("amount");
            long capacity = energyTag.getLong("capacity");

            // 将读取的数据应用到能量存储
            this.energyInventory.amount  = amount;
        }
    }

    public boolean canTransferEnergy(Direction direction){
        return direction==getCachedState().get(FACING).getOpposite();
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        boolean  added = false;
        if (!IRotate.StressImpact.isEnabled())
            return added;

        float stressBase = calculateStressApplied();
        if (MathHelper.approximatelyEquals(stressBase, 0))
            return added;

        Lang.translate("gui.goggles.kinetic_stats")
                .forGoggles(tooltip);
        Lang.translate("tooltip.stressImpact")
                .style(Formatting.GRAY)
                .forGoggles(tooltip);

        float stressTotal = stressBase * getSpeedABS();

        Lang.number(stressTotal)
                .translate("generic.unit.stress")
                .style(Formatting.AQUA)
                .space()
                .add(Lang.translate("gui.goggles.at_current_speed")
                        .style(Formatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        Lang.translate("tooltip.energyGenerated")
                .style(Formatting.GRAY)
                .forGoggles(tooltip);
        Lang.number(getEnergyPerTick())
                .translate("generic.unit.energy")
                .style(Formatting.AQUA)
                .space()
                .add(Lang.translate("gui.goggles.at_current_speed")
                        .style(Formatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        return true;
    }
}
