package com.ffsupver.asplor.block.rocketCargoLoader;

import com.ffsupver.asplor.entity.custom.AlloyChestEntity;
import com.ffsupver.asplor.entity.custom.rocket.CargoRocketEntity;
import com.ffsupver.asplor.util.NbtUtil;
import com.ffsupver.asplor.util.RenderUtil;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.ffsupver.asplor.block.rocketCargoLoader.RocketCargoLoader.CONNECTED;

public class RocketCargoLoaderEntity extends SmartBlockEntity implements SidedStorageBlockEntity , ThresholdSwitchObservable {
    private BlockPos rocketPos;
    private RocketCargoLoaderItemHandler itemHandler;
    private Entity lastUnload;
    private Entity lastLoad;
    private int launchPosCheckCoolDown;
    private final int MAX_LAUNCH_POS_CHECK_COOL_DOWN = 20;
    public RocketCargoLoaderEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        itemHandler = new RocketCargoLoaderItemHandler();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public void setRocketPos(BlockPos rocketPos,Direction facing) {
        this.rocketPos = rocketPos;
        world.setBlockState(pos,getCachedState().with(RocketCargoLoader.FACING,facing));
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (rocketPos != null){
            tag.put("rocket_pos", NbtUtil.writeBlockPosToNbt(rocketPos));
        }
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("rocket_pos",10)){
            rocketPos = NbtUtil.readBlockPosFromNbt(tag.getCompound("rocket_pos"));
        }
    }

    @Override
    public void tick() {
        super.tick();


        if (launchPosCheckCoolDown < MAX_LAUNCH_POS_CHECK_COOL_DOWN){
            launchPosCheckCoolDown++;
        }else {
            launchPosCheckCoolDown = 0;
            if (rocketPos != null && !(world.getBlockState(rocketPos).getBlock() instanceof LaunchPadBlock)){
                rocketPos = null;
            }
            if (rocketPos == null){
                checkLaunchPadNeighbour();
            }
        }

        if (world.isClient()){
            return;
        }

        if (rocketPos == null || getRocket() == null){
            if (world.getBlockState(pos).get(CONNECTED)){
                world.setBlockState(pos, getCachedState().with(CONNECTED, false));
            }
            return;
        }

        if (!world.getBlockState(pos).get(CONNECTED)){
            world.setBlockState(pos, getCachedState().with(CONNECTED, true));
        }


        if (getRocket() instanceof CargoRocketEntity cargoRocketEntity){
            List<Entity> entities = cargoRocketEntity.getPassengerList();
            if (getAlloyChestEntity() == null){
                Vec3d des = pos.toCenterPos().add(0,0.5,0);
                if (!entities.isEmpty() && entities.get(0) instanceof AlloyChestEntity downChestEntity){
                   List<Entity> alloyChestPassenger = downChestEntity.getPassengerList();
                   if (alloyChestPassenger.isEmpty()){
                       if (downChestEntity != lastLoad){
                           downChestEntity.stopRiding();
                           downChestEntity.teleport(des.x, des.y, des.z);
                           lastUnload = downChestEntity;
                       }
                   }else {
                       Entity upChest = alloyChestPassenger.get(0);
                       if (upChest != lastLoad){
                           upChest.stopRiding();
                           upChest.teleport(des.x, des.y, des.z);
                           lastUnload = upChest;
                       }
                   }
                }
            }else if (getAlloyChestEntity() != null && getAlloyChestEntity() != lastUnload) {
                if (entities.isEmpty()) {
                       boolean loaded = getAlloyChestEntity().startRiding(cargoRocketEntity);
                       if (loaded){
                           lastLoad = cargoRocketEntity.getPassengerList().get(0);
                       }
                } else if (entities.get(0) instanceof AlloyChestEntity alloyChestEntity) {
                    boolean loaded = getAlloyChestEntity().startRiding(alloyChestEntity);
                    if (loaded){
                        lastLoad = alloyChestEntity.getPassengerList().get(0);
                    }
                }

            }
        }


    }

    private void checkLaunchPadNeighbour(){
        List<Direction> horizon = List.of(Direction.NORTH,Direction.SOUTH,Direction.EAST,Direction.WEST);
        for (Direction direction : horizon){
            BlockState checkState = world.getBlockState(pos.offset(direction));
            if (checkState.getBlock() instanceof LaunchPadBlock launchPadBlock){
                setRocketPos(launchPadBlock.getController(checkState,pos.offset(direction)),direction);
                return;
            }
        }
    }

    private Entity getRocket(){
        List<Entity> entities = world.getOtherEntities(null,new Box(rocketPos,rocketPos.add(1,5,1)), entity -> entity instanceof CargoRocketEntity || entity instanceof Rocket);
        return entities.isEmpty() ? null : entities.get(0);
    }

    private AlloyChestEntity getAlloyChestEntity(){
        List<Entity> entities = world.getOtherEntities(null,new Box(pos.up(),pos.up().add(1,1,1)),entity -> entity instanceof AlloyChestEntity);
        if (entities.isEmpty()){
//            lastUnload = null;
            return null;
        }
        return entities.get(0) instanceof AlloyChestEntity alloyChestEntity ? alloyChestEntity : null;
    }

    @Override
    public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        return getRocket() != null ? itemHandler : null;
    }

    @Override
    public Box getRenderBoundingBox() {
        return RenderUtil.createRenderBoundingBox(pos,9.0);
    }

    @Override
    public float getPercent() {
        if (getRocket() != null && getRocket() instanceof CargoRocketEntity cargoRocketEntity){
            Inventory inventory = cargoRocketEntity.inventory();
            float percent = 0;
            for (int i = 2;i<inventory.size();i++){
                ItemStack itemStack = inventory.getStack(i);
                percent += (float) itemStack.getCount() / itemStack.getMaxCount();
            }
            percent /= inventory.size()-2;
            return percent*100;
        }
        return 0;
    }

    private class RocketCargoLoaderItemHandler implements Storage<ItemVariant>{
        private long insertToRocket(Inventory inventory,ItemVariant resource, long maxAmount){
            long left = maxAmount;
            for (int i = 2;i < inventory.size();i++){
                ItemStack itemStack = inventory.getStack(i);
                if (resource.matches(itemStack) || itemStack.isEmpty()){
                    long inserted = Math.min(left,itemStack.getMaxCount()-itemStack.getCount());
                    ItemStack newItemStack = itemStack.isEmpty() ? resource.toStack((int) inserted) : itemStack.copyWithCount((int) inserted + itemStack.getCount());
                    inventory.setStack(i,newItemStack);
                    left -= inserted;
                    if (left <= 0){
                        return maxAmount;
                    }
                }
            }
            return maxAmount - left;
        }

        private long extractFromRocket(Inventory inventory,ItemVariant resource, long maxAmount,TransactionContext transaction){
            long left = maxAmount;
            for (int i = 2;i < inventory.size();i++){
                ItemStack itemStack = inventory.getStack(i);
                if (resource.matches(itemStack)){
                    long extracted = Math.min(left,itemStack.getCount());
                    ItemStack newItemStack = itemStack.copyWithCount((int) (itemStack.getCount() - extracted));
                    int finalI = i;
                    transaction.addCloseCallback((transaction1, result) -> {
                        if (result.wasCommitted()){
                            inventory.setStack(finalI,newItemStack);
                        }
                    });
                    left -= extracted;
                    if (left <= 0){
                        return maxAmount;
                    }
                }
            }
            return maxAmount - left;
        }

        private Iterator<StorageView<ItemVariant>> getIterator(Inventory inventory){
            ArrayList<StorageView<ItemVariant>> list = new ArrayList<>();
            for (int i = 2;i < inventory.size();i++){
                int finalI = i;
                list.add(new StorageView<>() {
                    @Override
                    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                        return itemHandler.extract(resource, maxAmount, transaction);
                    }

                    @Override
                    public boolean isResourceBlank() {
                        return inventory.getStack(finalI).isEmpty();
                    }

                    @Override
                    public ItemVariant getResource() {
                        return ItemVariant.of(inventory.getStack(finalI).getItem(), inventory.getStack(finalI).getNbt());
                    }

                    @Override
                    public long getAmount() {
                        return inventory.getStack(finalI).getCount();
                    }

                    @Override
                    public long getCapacity() {
                        return inventory.getStack(finalI).getMaxCount() - inventory.getStack(finalI).getCount();
                    }
                });
            }
            return list.iterator();
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (getRocket() instanceof CargoRocketEntity cargoRocketEntity){
               return insertToRocket(cargoRocketEntity.inventory(),resource,maxAmount);
            }
            if (getRocket() instanceof Rocket rocket){
                return insertToRocket(rocket.inventory(),resource,maxAmount);
            }
            return 0;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (getRocket() instanceof CargoRocketEntity cargoRocketEntity){
                return extractFromRocket(cargoRocketEntity.inventory(),resource,maxAmount,transaction);
            }
            if (getRocket() instanceof Rocket rocket){
                return extractFromRocket(rocket.inventory(),resource,maxAmount,transaction);
            }
            return 0;
        }

        @Override
        public Iterator<StorageView<ItemVariant>> iterator() {
            List<StorageView<ItemVariant>> e = List.of();
            if (getRocket() instanceof CargoRocketEntity cargoRocketEntity){
                return getIterator(cargoRocketEntity.inventory());
            }
            if (getRocket() instanceof Rocket rocket){
                return getIterator(rocket.inventory());
            }
            return e.stream().iterator();
        }
    }

}
