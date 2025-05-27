package com.ffsupver.asplor.block.laserDrill;

import com.ffsupver.asplor.block.SmartEnergyStorage;
import com.ffsupver.asplor.util.GoggleDisplays;
import com.ffsupver.asplor.util.NbtUtil;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class LaserDrillBatteryEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    private BlockPos controllerPos;
    private int checkCoolDown = 0;

    public LaserDrillBatteryEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }

    public void removeControllerPos() {
        this.controllerPos = null;
    }

    @Override
    public void tick() {
        super.tick();
        if (controllerPos != null){
            if (checkCoolDown <= 0) {
                checkCoolDown = 20;
                if (getWorld().getBlockEntity(controllerPos) instanceof LaserDrillLenEntity laserDrillEntity) {
                   EnergyStorage energy = laserDrillEntity.getEnergyStorage();
                   int energyProperty = energy.getCapacity () == 0 ? 0 : (int) (energy.getAmount() * 4 / energy.getCapacity());
                   setEnergy(energyProperty);
                }else {
                    removeControllerPos();
                    setEnergy(0);
                }
            } else {
                checkCoolDown--;
            }
        }
    }

    private void setEnergy(int energy){
        world.setBlockState(pos,getCachedState().with(LaserDrillBattery.ENERGY,energy));
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (controllerPos != null){
            tag.put("controller", NbtUtil.writeBlockPosToNbt(controllerPos));
        }
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("controller", NbtElement.COMPOUND_TYPE)){
            controllerPos = NbtUtil.readBlockPosFromNbt(tag.getCompound("controller"));
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        SmartEnergyStorage energyStorage = getEnergyStorage();
        if (energyStorage != null){
            return GoggleDisplays.addEnergyDisplayToGoggles(tooltip,getEnergyStorage());
        }
        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip,isPlayerSneaking);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    public SmartEnergyStorage getEnergyStorage() {
        if (controllerPos != null && world.getBlockEntity(controllerPos) instanceof LaserDrillLenEntity laserDrillEntity){
            return laserDrillEntity.getEnergyStorage();
        }
        return null;
    }
    public EnergyStorage getEnergyStorage(Direction direction) {
        return getEnergyStorage();
    }
}
