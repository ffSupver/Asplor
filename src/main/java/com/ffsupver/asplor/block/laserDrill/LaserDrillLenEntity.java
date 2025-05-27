package com.ffsupver.asplor.block.laserDrill;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.ModTags;
import com.ffsupver.asplor.block.SmartEnergyStorage;
import com.ffsupver.asplor.util.GoggleDisplays;
import com.ffsupver.asplor.util.NbtUtil;
import com.ffsupver.asplor.util.RenderUtil;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ffsupver.asplor.block.laserDrill.LaserDrillBehaviour.ENERGY_PER_BEDROCK_MINE;
import static com.ffsupver.asplor.block.laserDrill.LaserDrillBehaviour.ENERGY_PER_TICK;

public class LaserDrillLenEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    private static final long CAPACITY = 30000L;
    private final SmartEnergyStorage energyStorage = new SmartEnergyStorage(calculateEnergyCapacity(),CAPACITY,CAPACITY,this::onEnergyChange);
    private final Set<BlockPos> batteries = new HashSet<>();
    private final Set<BlockPos> itemOutputs = new HashSet<>();
    private int checkCoolDown = 0;
    private boolean completed;
    private static final int HEIGHT = 4;

    private void onEnergyChange(Long aLong) {
        notifyUpdate();
    }

    public LaserDrillLenEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        if (checkCoolDown <= 0){
            checkCoolDown = 20;
            checkBlocks();
            updateEnergy();
            notifyUpdate();
        }else {
            checkCoolDown--;
        }

        if (!completed){
            resetLastDigPos();
            return;
        }

        if (!hasEnergy()){
            resetLastDigPos();
            return;
        }

        super.tick();
    }

    private void updateEnergy() {
        energyStorage.setCapacity(calculateEnergyCapacity());
    }

    private boolean hasEnergy(){
        return energyStorage.getAmount() >= Math.min(ENERGY_PER_TICK,ENERGY_PER_BEDROCK_MINE);
    }

    public boolean extractEnergy(long amount){
        boolean enough = amount <= energyStorage.getAmount();
        if (enough) {
            energyStorage.extractEnergy(amount);
        }
        return enough;
    }

    private long calculateEnergyCapacity(){
        if (batteries != null) {
            return batteries.size() * CAPACITY;
        }
        return 0L;
    }

    public BlockPos getLastDigPos(){
        return getBehaviour(LaserDrillBehaviour.TYPE).getLastDigPos();
    }

    public void resetLastDigPos(){
         getBehaviour(LaserDrillBehaviour.TYPE).resetLastDigPos();
    }

    private void checkBlocks(){
        boolean complete = true;
        for (int x = -1; x < 2; x++) {
            for (int y = 0; y < HEIGHT + 1; y++) {
                for (int z = -1; z < 2; z++) {
                    BlockPos checkPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    BlockState checkState = getWorld().getBlockState(checkPos);

                    tryBattery(checkPos);
                    tryItemOutput(checkPos);

                    if (!checkState.isIn(ModTags.Blocks.LASER_DRILL_BLOCK)){
                        complete = false;
                    }
                    if (x == 0 && z == 0){
                        if (y == HEIGHT && !checkState.isOf(AllBlocks.LASER_DRILL)){
                            complete = false;
                        }else if (y != 0 && y != HEIGHT && !checkState.isOf(AllBlocks.LASER_DRILL_GLASS)){
                            complete = false;
                        }
                    }
                }
            }
        }

        this.completed = complete;
    }

    private void tryBattery(BlockPos checkPos){
        if (getWorld().getBlockEntity(checkPos) instanceof LaserDrillBatteryEntity laserDrillBatteryEntity) {
            batteries.add(checkPos);
            laserDrillBatteryEntity.setControllerPos(pos);
        } else {
            batteries.remove(checkPos);
        }
    }

    private void tryItemOutput(BlockPos checkPos){
        if (getWorld().getBlockEntity(checkPos) instanceof LaserDrillItemOutputEntity) {
            itemOutputs.add(checkPos);
        } else {
            itemOutputs.remove(checkPos);
        }
    }

    public int insertItemToOutput(ItemStack output){
        ItemStack toInsert = output.copy();
        for (BlockPos outputPos : itemOutputs){
            if (getWorld().getBlockEntity(outputPos) instanceof LaserDrillItemOutputEntity outputEntity){
               toInsert = outputEntity.insert(toInsert);
               if (toInsert.getCount() <= 0){
                   return 0;
               }
            }
        }
        return toInsert.getCount();
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        batteries.clear();
        batteries.addAll(NbtUtil.readBlockPosListFromNbt(tag.getList("batteries", NbtElement.COMPOUND_TYPE)));
        itemOutputs.addAll(NbtUtil.readBlockPosListFromNbt(tag.getList("item_output", NbtElement.COMPOUND_TYPE)));
        energyStorage.readFromNBT(tag.getCompound("energy"));
        completed = tag.getBoolean("complete");
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("batteries", NbtUtil.writeBlockPosListToNbt(batteries));
        tag.put("item_output", NbtUtil.writeBlockPosListToNbt(itemOutputs));
        tag.put("energy",energyStorage.writeToNBT(new NbtCompound()));
        tag.putBoolean("complete",completed);
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        return GoggleDisplays.addEnergyDisplayToGoggles(tooltip,energyStorage);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new LaserDrillBehaviour(this));
    }

    @Override
    public Box getRenderBoundingBox() {
        return RenderUtil.createRenderBoundingBox(pos,256.0);
    }

    public SmartEnergyStorage getEnergyStorage() {
       return energyStorage;
    }
}
