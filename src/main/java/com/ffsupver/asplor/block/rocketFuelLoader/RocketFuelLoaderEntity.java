package com.ffsupver.asplor.block.rocketFuelLoader;

import com.ffsupver.asplor.entity.custom.CargoRocketEntity;
import com.ffsupver.asplor.util.NbtUtil;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class RocketFuelLoaderEntity extends SmartBlockEntity implements SidedStorageBlockEntity , IHaveGoggleInformation , ThresholdSwitchObservable {
    private BlockPos rocketPos;
    private final FluidTank tank = new FluidTank(6000*81);
    private RocketFuelLoaderFluidHandler handler;
    private int cooldown;
    private final int MAX_COOLDOWN=5;
    private int launchPosCheckCoolDown;
    private final int MAX_LAUNCH_POS_CHECK_COOL_DOWN = 20;
    private int process;
    private final int MAX_PROCESS = 60;
    public RocketFuelLoaderEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        handler = new RocketFuelLoaderFluidHandler();
    }

    public void setRocketPos(BlockPos rocketPos) {
        this.rocketPos = rocketPos;
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (rocketPos != null){
            tag.put("rocket_pos", NbtUtil.writeBlockPosToNbt(rocketPos));
        }
        tag.put("fluid",tank.writeToNBT(new NbtCompound()));
        tag.putInt("process",process);
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("rocket_pos",10)){
            rocketPos = NbtUtil.readBlockPosFromNbt(tag.getCompound("rocket_pos"));
        }
        tank.readFromNBT(tag.getCompound("fluid"));
        process = tag.getInt("process");
    }

    @Override
    public void tick() {
        super.tick();

        if (world.getBlockState(pos.up()).isSideSolidFullSquare(world,pos.up(),Direction.DOWN)){
            return;
        }

        if (launchPosCheckCoolDown < MAX_LAUNCH_POS_CHECK_COOL_DOWN){
            launchPosCheckCoolDown++;
        }else {
            launchPosCheckCoolDown = 0;
            if (rocketPos != null && !(world.getBlockState(rocketPos).getBlock() instanceof LaunchPadBlock)){
                rocketPos = null;
                process = 0;
            }
            if (rocketPos == null){
                checkLaunchPadNeighbour();
            }
        }

        if (rocketPos == null){
            return;
        }


        if (getRocket() == null){
            process -= process > 0 ? 1 : 0;
            return;
        }else {
            process += process < MAX_PROCESS ? 1 : 0;
        }

        if (cooldown < MAX_COOLDOWN){
            cooldown++;
            return;
        }

        cooldown = 0;

        if (tank.isEmpty() || process < MAX_PROCESS){
            return;
        }


        Entity rocket = getRocket();

        if (rocket instanceof Rocket rocketEntity){
            insertToRocket(rocketEntity.fluidContainer());
        }else if (rocket instanceof CargoRocketEntity cargoRocketEntity){
            insertToRocket(cargoRocketEntity.fluidContainer());
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip,isPlayerSneaking,tank);
    }

    private Entity getRocket(){
       List<Entity> entities = world.getOtherEntities(null,new Box(rocketPos,rocketPos.add(1,5,1)),entity -> entity instanceof CargoRocketEntity || entity instanceof Rocket);
       return entities.isEmpty() ? null : entities.get(0);
    }

    private void insertToRocket(FluidContainer fluidContainer){
        try(Transaction t = Transaction.openOuter()) {
            long amount = Math.min(tank.getFluidAmount(), fluidContainer.getTankCapacity(0) - fluidContainer.getFirstFluid().getFluidAmount());
            long inserted = fluidContainer.insertFluid(FluidHolder.of(handler.getFluid(), amount, tank.getFluid().getTag()), false);
            handler.extract(FluidVariant.of(handler.getFluid()),inserted,t);
            t.commit();
        }
    }

    public float getProcess() {
        return (float) process /MAX_PROCESS;
    }

    public BlockPos getRocketPos() {
        return rocketPos;
    }

    private void checkLaunchPadNeighbour(){
        List<Direction> horizon = List.of(Direction.NORTH,Direction.SOUTH,Direction.EAST,Direction.WEST);
        for (Direction direction : horizon){
           BlockState checkState = world.getBlockState(pos.offset(direction));
           if (checkState.getBlock() instanceof LaunchPadBlock launchPadBlock){
               rocketPos = launchPadBlock.getController(checkState,pos.offset(direction));
               return;
           }
        }
    }



    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        return handler;
    }

    @Override
    public float getPercent() {
        if (getRocket() instanceof CargoRocketEntity cargoRocketEntity){
           return 100f * cargoRocketEntity.fluidContainer().getFirstFluid().getFluidAmount() / cargoRocketEntity.fluidContainer().getTankCapacity(0);
        }
        return 0;
    }

    private class RocketFuelLoaderFluidHandler implements Storage<FluidVariant>{
        public Fluid getFluid(){
            return tank.getFluid().getFluid();
        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            sendData();
            return tank.insert(resource,maxAmount,transaction);
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            sendData();
            return tank.extract(resource,maxAmount,transaction);
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            return tank.iterator();
        }
    }

}
