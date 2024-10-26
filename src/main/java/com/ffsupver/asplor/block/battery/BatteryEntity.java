package com.ffsupver.asplor.block.battery;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.ModTags;
import com.ffsupver.asplor.block.EnergyConnectiveHandler;
import com.ffsupver.asplor.block.IMultiBlockEntityContainerEnergy;
import com.ffsupver.asplor.block.SmartEnergyStorage;
import com.ffsupver.asplor.util.GoggleDisplays;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

import static com.ffsupver.asplor.block.battery.Battery.*;

public class BatteryEntity extends SmartBlockEntity implements IMultiBlockEntityContainerEnergy,EnergyStorage, IHaveGoggleInformation, IWrenchable {
    private BlockPos controller;

    protected SmartEnergyStorage energyStorage;
    private static final long CAPACITY=50000;
    private static final long MAX_TRANSFER =5*CAPACITY;
    protected int width;
    protected int height;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected boolean forceEnergyLevelUpdate;
    private final int MAX_WIDTH = 5;
    private final int MAX_HEIGHT = 5;
    private LerpedFloat energyAmount;

    public BatteryEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        energyStorage=createInventory();
        forceEnergyLevelUpdate = true;
        updateConnectivity = false;
        height = 1;
        width = 1;
    }
    protected SmartEnergyStorage createInventory() {
        return new SmartEnergyStorage(getCapacityMultiplier(), MAX_TRANSFER, MAX_TRANSFER,this::onEnergyLevelChanged);
    }

    public static long getCapacityMultiplier() {
        return CAPACITY;
    }

    protected void onEnergyLevelChanged(long newEnergyLevel) {
        // 执行你需要的更新操作，比如标记区块需要保存，更新客户端等
        markDirty();
        if (!world.isClient()) {

            // 发送能量更新包到客户端
            sendData();
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        BatteryEntity controllerBE = getControllerBE();
        if (controllerBE==null) {
            return false;
        }
        return GoggleDisplays.addEnergyDisplayToGoggles(tooltip,controllerBE.energyStorage);
    }

    public boolean canTransferEnergy(Direction direction){
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (world.getTime()%20==0){
            updateBlockStateForConnectivity();
        }
        if(updateConnectivity){
            updateConnectivity();
        }
        if (world.isClient){
//            System.out.println(energyStorage.getAmount()+" "+energyAmount);
        }
        if (energyAmount!=null) {
            energyAmount.tickChaser();
        }
        if (!world.isClient()){
            transferEnergyToNeighbors();
        }
        sendData();
    }

    private void transferEnergyToNeighbors() {
        for (Direction direction : Direction.values()) {
            if (canTransferEnergy(direction)) {
                BlockPos neighborPos = pos.offset(direction);
                BlockState neighborBlockState = world.getBlockState(neighborPos);
                if (neighborBlockState.isIn(ModTags.Blocks.NEED_ENERGY) && !neighborBlockState.isOf(AllBlocks.BATTERY.get())){
                    EnergyStorage neighborStorage = EnergyStorage.SIDED.find(world, neighborPos, direction.getOpposite());

                    if (neighborStorage != null && getExposeStorage().getAmount() > 0) {
                        // 计算要传输的能量量
                        long extractableEnergy = Math.min(getExposeStorage().getAmount(), MAX_TRANSFER);
                        try (Transaction t = Transaction.openOuter()) {
                            long acceptedEnergy = neighborStorage.insert(extractableEnergy, t);
                            // 从当前能量存储中减少传输的能量
                            this.getExposeStorage().extract(acceptedEnergy, t);
                            markDirty();
                            t.commit();
                        }

                    }
                }
            }
        }
    }

    @Override
    protected void read(NbtCompound compound, boolean clientPacket) {
        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;
        long oldEnergyAmount = energyStorage.getAmount();

        updateConnectivity = compound.contains("Uninitialized");
        controller = null;
        lastKnownPos = null;

        if (compound.contains("LastKnownPos"))
            lastKnownPos = NbtHelper.toBlockPos(compound.getCompound("LastKnownPos"));
        if (compound.contains("Controller"))
            controller = NbtHelper.toBlockPos(compound.getCompound("Controller"));

        if (isController()) {
            width = compound.getInt("Size");
            height = compound.getInt("Height");
            energyStorage.setCapacity(totalBatterySize() * getCapacityMultiplier());
            energyStorage.readFromNBT(compound.getCompound("Energy"));
            if (energyStorage.getSpace() < 0) {
                try (Transaction t = TransferUtil.getTransaction()) {
                    energyStorage.extract( -energyStorage.getSpace(), t);
                    t.commit();
                }
            }
            if (energyAmount==null){
                energyAmount=LerpedFloat.linear().startWithValue(getAmount());
            }
            if (!clientPacket){
                return;
            }
            energyAmount.chase(getAmount(),0.5f, LerpedFloat.Chaser.EXP);
        }

        super.read(compound, clientPacket);

    }

    @Override
    protected void write(NbtCompound compound, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);
        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtHelper.fromBlockPos(lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtHelper.fromBlockPos(controller));
        if (isController()) {
            compound.put("Energy", energyStorage.writeToNBT(new NbtCompound()));
            compound.putInt("Size", width);
            compound.putInt("Height", height);
        }
        super.write(compound, clientPacket);
    }

    public void updateBlockStateForConnectivity(){
        BlockPos downPos = pos.down();
        BlockPos upPos=pos.up();
        BlockPos northPos = pos.north();
        BlockPos southPos = pos.south();
        BlockPos eastPos = pos.east();
        BlockPos westPos = pos.west();

        // 检查水平方向的连接状态，使用位掩码表示每个方向的连接
        int connectedMask = 0;
        if (EnergyConnectiveHandler.isConnected(world, pos, eastPos)) connectedMask |= 1;   // 0001 东
        if (EnergyConnectiveHandler.isConnected(world, pos, southPos)) connectedMask |= 2;  // 0010 南
        if (EnergyConnectiveHandler.isConnected(world, pos, westPos)) connectedMask |= 4;   // 0100 西
        if (EnergyConnectiveHandler.isConnected(world, pos, northPos)) connectedMask |= 8;  // 1000 北



        world.setBlockState(pos,getCachedState().with(BOTTOM,!EnergyConnectiveHandler.isConnected(world,pos,downPos)));
        world.setBlockState(pos,getCachedState().with(TOP,!EnergyConnectiveHandler.isConnected(world,pos,upPos)));
        if (getControllerBE() != null && getControllerBE().getWidth() == 1){
           world.setBlockState(pos,getCachedState().with(SHAPE,shape.SINGLE));
        }else {

            // 根据位掩码查找对应的形状
            shape newShape = switch (connectedMask) {
                case 3 -> shape.CORNER_WN;  // 东 + 南 -> 0011
                case 12 -> shape.CORNER_ES; // 西 + 北 -> 1100
                case 6 -> shape.CORNER_NE;  // 南 + 西 -> 0110
                case 9 -> shape.CORNER_SW;  // 北 + 东 -> 1001
                case 14 -> shape.EDGE_E; //南+西+北 -> 1110
                case 13 -> shape.EDGE_S; //东+西+北 -> 1101
                case 11 -> shape.EDGE_W; //东+南+北 -> 1011
                case 7 -> shape.EDGE_N;   //东+南+西 -> 0111
                case 15 -> shape.INNER;
                default -> null;
            };

            // 设置形状状态
            if (!(newShape ==null))
                world.setBlockState(pos, getCachedState().with(SHAPE, newShape));

        }
    }


    protected void updateConnectivity() {
        updateConnectivity = false;
        if (world.isClient)
            return;
        if (!isController())
            return;
        EnergyConnectiveHandler.formMulti(this);
    }

    @Override
    public void notifyUpdate() {
        onEnergyLevelChanged(energyStorage.getAmount());
        markDirty();
    }

    @Override
    public BlockPos getController() {
        return isController()?pos:controller;
    }

    @Override
    public <T extends BlockEntity & IMultiBlockEntityContainer> T getControllerBE() {
        if (isController())
            return (T) this;
        BlockEntity blockEntity = null;
        if (world != null) {
            blockEntity = world.getBlockEntity(controller);
        }
        if (blockEntity instanceof BatteryEntity)
            return (T) blockEntity;
        return null;
    }

    @Override
    public boolean isController() {
        return controller == null || pos.getX() == controller.getX()
                && pos.getY() == controller.getY() && pos.getZ() == controller.getZ();
    }

    @Override
    public void setController(BlockPos controller) {
        if (world.isClient && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        markDirty();
        sendData();
    }


    @Override
    public void removeController(boolean keepContents) {
        if (world.isClient)
            return;
        updateConnectivity = true;
        if (!keepContents)
            applyEnergyStorageSize(1);
        controller = null;
        width = 1;
        height = 1;
        onEnergyLevelChanged(energyStorage.getAmount());



        markDirty();
        sendData();

    }
    public int totalBatterySize(){
        return width*width*height;
    }

    private void applyEnergyStorageSize(int blocks) {
        energyStorage.setCapacity(blocks * getCapacityMultiplier());
        long overflow = energyStorage.getAmount() - energyStorage.getCapacity();
        if (overflow > 0)
            try(Transaction t = Transaction.openOuter()){
            energyStorage.extract(overflow,t);
            t.commit();
            }
        forceEnergyLevelUpdate = true;
    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity=false;
    }

    @Override
    public void notifyMultiUpdated() {
        updateConnectivity=true;
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        return MAX_WIDTH;
    }

    @Override
    public int getMaxWidth() {
        return MAX_HEIGHT;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height=height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
    this.width=width;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        return getExposeStorage().insert(maxAmount,transaction);
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return getExposeStorage().extract(maxAmount,transaction);
    }

    @Override
    public long getAmount() {
            return getExposeStorage().getAmount();
    }

    @Override
    public long getCapacity() {
            return getExposeStorage().getCapacity();
    }

    @Override
    public long getEnergyCapacity() {
        return energyStorage.getCapacity();
    }

    @Override
    public EnergyStorage getStoredEnergy() {
        return energyStorage;
    }

    @Override
    public void setStoredEnergy(long energy) {
        try(Transaction transaction = Transaction.openOuter()){
           if (isController()){
            energyStorage.setEnergy(energy,transaction);
           }else {
               ((BatteryEntity)getControllerBE()).energyStorage.setEnergy(energy,transaction);
           }
           transaction.commit();
        }
    }

    @Override
    public void setEnergyCapacity(int blocks) {
            applyEnergyStorageSize(blocks);
    }

    @Override
    public boolean hasEnergyStorage() {
        return true;
    }

    public EnergyStorage getExposeStorage() {
        return isController()?energyStorage:getControllerBE()==null ? null:((BatteryEntity)getControllerBE()).energyStorage;
    }
}
