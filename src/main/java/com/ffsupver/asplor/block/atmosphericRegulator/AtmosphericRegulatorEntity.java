package com.ffsupver.asplor.block.atmosphericRegulator;

import com.ffsupver.asplor.util.ModUtil;
import com.ffsupver.asplor.util.NbtUtil;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import earth.terrarium.adastra.api.systems.OxygenApi;
import earth.terrarium.adastra.api.systems.TemperatureApi;
import earth.terrarium.adastra.common.blocks.SlidingDoorBlock;
import earth.terrarium.adastra.common.config.MachineConfig;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.utils.floodfill.FloodFill3D;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.ffsupver.asplor.block.atmosphericRegulator.AtmosphericRegulator.FACING;

public class AtmosphericRegulatorEntity extends SmartBlockEntity implements SidedStorageBlockEntity, IHaveGoggleInformation {
    private final SmartFluidTank oxygenTank;
    private final Set<BlockPos> lastBlocks = new HashSet<>();
    private int tickCoolDown;
    private static final int MAX_TICK_COOLDOWN = 30;
    private int connectCoolDown;
    private static final int MAX_CONNECT_COOLDOWN = 4;
    private static final int MAX_BLOCKS = MachineConfig.maxDistributionBlocks;

    private BlockPos fromPos;
    private Set<BlockPos> toPosList = new HashSet<>();
    public AtmosphericRegulatorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        oxygenTank = new SmartFluidTank(8 * 81000L, fluidStack -> this.notifyUpdate());
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCoolDown > 0){
            tickCoolDown--;
        }else {
            tickCoolDown = MAX_TICK_COOLDOWN;
            if (getOxygenAmount() > 0){
                checkBlocks();
            }
            if (connectCoolDown > 0){
                connectCoolDown--;
            }else {
                connectCoolDown = MAX_CONNECT_COOLDOWN;
                updateConnectBlocks();
                if (isController()){
                    shareOxygen();
                }
            }
        }
    }

    private void updateConnectBlocks(){
        Direction.Axis axis = getFacingSide().getAxis();
        toPosList.clear();
        for (Direction direction : Direction.values()){
            if (!axis.test(direction)){
               BlockPos checkConnectPos = walkThroughOxygenBlock(direction);
               if (checkConnectPos != null){
                   toPosList.add(checkConnectPos);
               }
            }
        }

        if (fromPos != null && (!(getWorld().getBlockEntity(fromPos) instanceof AtmosphericRegulatorEntity) || fromPos.equals(pos))){
            fromPos = null;
        }

        toPosList.remove(pos);

        for (BlockPos connectPos : toPosList) {
            if (getWorld().getBlockEntity(connectPos) instanceof AtmosphericRegulatorEntity atmosphericRegulatorEntity) {
                atmosphericRegulatorEntity.setFromPos(isController() ? pos : fromPos.equals(connectPos) ? null : fromPos);
                if (!isController()){
                   AtmosphericRegulatorEntity a = getControllerEntity();
                   if (a != null){
                       a.addToPosList(toPosList);
                   }
                }
            }else {
                toPosList.remove(connectPos);
            }
        }


    }

    private BlockPos walkThroughOxygenBlock(Direction direction){
        BlockPos lastPos = pos;
        BlockPos checkPos = lastPos.offset(direction);
        BlockState checkState = getWorld().getBlockState(checkPos);
        while (checkState.getBlock() instanceof OxygenPipe){
                BlockPos tmpPos = checkPos;
                checkPos = OxygenPipe.getConnectPos(checkPos, checkState, lastPos);
                lastPos = tmpPos;
                checkState = getWorld().getBlockState(checkPos);
        }
        if (checkPos.equals(pos)){
            return null;
        }else if (checkState.getBlock() instanceof AtmosphericRegulator){
            return checkPos;
        }
        return null;
    }

    private void checkBlocks(){
        if (OxygenApi.API.hasOxygen(getWorld())){
            return;
        }

        if(!FloodFill3D.TEST_FULL_SEAL.test(
                getWorld(),
                getPos().offset(getFacingSide()),
                getWorld().getBlockState(getPos().offset(getFacingSide())),
                LongSet.of(),
                new LongArrayFIFOQueue(),
                getFacingSide())
        ){
            removeOxygenBlocks();
            return;
        }

        Set<BlockPos> lastBlocksCopy = Set.copyOf(lastBlocks);
        Set<BlockPos> newBlocks = FloodFill3D.run(
                getWorld(),getPos().offset(getFacingSide()),
                MAX_BLOCKS,
                (level, pos, state, positions, queue, direction) -> {
                    boolean testFullSeal = FloodFill3D.TEST_FULL_SEAL.test(level,pos,state,positions,queue,direction);
                    if (state.getBlock() instanceof SlidingDoorBlock){
                        boolean isSlidingDoorOpen = ModUtil.isSlideDoorOpen(level,state,pos);
                        if (isSlidingDoorOpen){
                            BlockPos posOfOtherSide = pos.offset(direction);
                            boolean testFullSealOtherSide = FloodFill3D.TEST_FULL_SEAL.test(level,posOfOtherSide,state,positions,queue,direction);
                            boolean canPast = (testFullSealOtherSide && !OxygenApi.API.hasOxygen(level,posOfOtherSide)) || lastBlocksCopy.contains(posOfOtherSide);
                            if (canPast){
                                return true;
                            }else {
                                positions.add(pos.asLong());
                                return false;
                            }
                        }
                    }

                    BlockPos lastPos = pos.offset(direction.getOpposite());
                    BlockState lastState = level.getBlockState(lastPos);
                    if (lastState.getBlock() instanceof SlidingDoorBlock && !(state.getBlock() instanceof SlidingDoorBlock) && !ModUtil.isSlideDoorOpen(level,lastState,lastPos)){
                        positions.add(pos.asLong());
                        return false;
                    }
                    return testFullSeal;
                },
                true
        );



        if (extractOxygen()){
            OxygenApi.API.setOxygen(getWorld(), newBlocks, true);
            TemperatureApi.API.setTemperature(getWorld(), newBlocks, (short) 22);
        }else {
            removeOxygenBlocks();
        }
        resetOxygenBlocks(newBlocks);
    }

    private boolean extractOxygen(){
        FluidVariant toExtract = getOxygenFluidVariant();
        long amountToExtract = getOxygenCost(MAX_TICK_COOLDOWN);
        if (oxygenTank.getFluid().getType().equals(toExtract) && oxygenTank.getFluid().getAmount() >= amountToExtract){
            boolean isExtracted;
            try (Transaction t = Transaction.openOuter()) {
               long extracted = oxygenTank.extract(toExtract, amountToExtract, t);
                isExtracted = extracted >= amountToExtract;
                t.commit();
            }
            return isExtracted;
        }
        return false;
    }

    private void resetOxygenBlocks(Set<BlockPos> newBlocks){
        //清除多余方块
        lastBlocks.removeAll(newBlocks);
        removeOxygenBlocks();
        //加入新方块
        lastBlocks.addAll(newBlocks);
    }

    private void removeOxygenBlocks(){
        OxygenApi.API.removeOxygen(getWorld(),lastBlocks);
        TemperatureApi.API.removeTemperature(getWorld(),lastBlocks);
        lastBlocks.clear();
    }

    private boolean isController(){
        return fromPos == null;
    }

    private AtmosphericRegulatorEntity getControllerEntity(){
        return isController()? this :
                getWorld().getBlockEntity(fromPos) instanceof AtmosphericRegulatorEntity atmosphericRegulatorEntity ?
                        atmosphericRegulatorEntity : null;
    }

    private void shareOxygen(){
        List<AtmosphericRegulatorEntity> atmosphericRegulatorEntities = new ArrayList<>();
        for (BlockPos blockPos : toPosList){
            if (getWorld().getBlockEntity(blockPos) instanceof AtmosphericRegulatorEntity a && !atmosphericRegulatorEntities.contains(a)){
                atmosphericRegulatorEntities.add(a);
            }
        }

        long totalAmount = getOxygenAmount();
        for (AtmosphericRegulatorEntity a : atmosphericRegulatorEntities){
            totalAmount += a.getOxygenAmount();
        }

        long eachOxygenAmount = totalAmount / (atmosphericRegulatorEntities.size() + 1);


        if (eachOxygenAmount <= 0){
            return;
        }

        for (AtmosphericRegulatorEntity a : atmosphericRegulatorEntities){
            long tAmount = a.getOxygenAmount() - eachOxygenAmount;
            boolean shouldInsert = tAmount < 0;
            if (shouldInsert){
                a.insertOxygen(-tAmount);
            }else if (tAmount != 0){
                a.extractOxygen(tAmount);
            }
        }
        long thisOxygenAmount = eachOxygenAmount + totalAmount % (atmosphericRegulatorEntities.size() + 1);
        long thisTAmount = getOxygenAmount() - thisOxygenAmount;
        if (thisTAmount < 0){
            insertOxygen(-thisTAmount);
        }else if (thisTAmount != 0){
            extractOxygen(thisTAmount);
        }
    }

    public long getOxygenAmount(){
        return oxygenTank.getAmount();
    }

    public long extractOxygen(long amount){
        long extracted;
        try (Transaction t = Transaction.openOuter()) {
            extracted = oxygenTank.extract(getOxygenFluidVariant(), amount, t);
            t.commit();
        }
        return extracted;
    }

    public long insertOxygen(long amount){
        long inserted;
        try (Transaction t = Transaction.openOuter()) {
            inserted = oxygenTank.insert(getOxygenFluidVariant(), amount, t);
            t.commit();
        }
        return inserted;
    }

    private static FluidVariant getOxygenFluidVariant(){
        return FluidVariant.of(ModFluids.OXYGEN.get());
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        lastBlocks.clear();
        lastBlocks.addAll(NbtUtil.readBlockPosListFromNbt(tag.getList("last_block", NbtElement.COMPOUND_TYPE)));
        this.oxygenTank.readFromNBT(tag.getCompound("oxygen"));
        if (tag.contains("from_pos",NbtElement.COMPOUND_TYPE)){
            fromPos = NbtUtil.readBlockPosFromNbt(tag.getCompound("from_pos"));
        }

        toPosList.clear();
        toPosList.addAll(NbtUtil.readBlockPosListFromNbt(tag.getList("to_pos",NbtElement.COMPOUND_TYPE)));
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("last_block",NbtUtil.writeBlockPosListToNbt((new ArrayList<>(lastBlocks))));
        tag.put("oxygen", this.oxygenTank.writeToNBT(new NbtCompound()));
        if (fromPos != null){
            tag.put("from_pos",NbtUtil.writeBlockPosToNbt(fromPos));
        }
        tag.put("to_pos",NbtUtil.writeBlockPosListToNbt(new ArrayList<>(toPosList)));
    }

    public void setFromPos(BlockPos fromPos) {
        this.fromPos = fromPos;
    }

    public void addToPosList(Collection<BlockPos> toPosList){
        this.toPosList.addAll(toPosList);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip,isPlayerSneaking,oxygenTank);
    }

    @Override
    public void destroy() {
        super.destroy();
        removeOxygenBlocks();
    }

    private Direction getFacingSide(){
        return getCachedState().get(FACING);
    }

    private long getOxygenCost(int tickPast){
        return FluidConstants.fromMillibuckets(Math.max(tickPast,tickPast * lastBlocks.size() / 1500));
    }

    @Override
    public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        return side.equals(getFacingSide()) ? null : oxygenTank;
    }
}
