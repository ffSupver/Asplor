package com.ffsupver.asplor.block;

import com.ffsupver.asplor.block.battery.BatteryEntity;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import team.reborn.energy.api.EnergyStorage;

import javax.annotation.Nullable;
import java.util.*;

public class EnergyConnectiveHandler {
    public static <T extends BlockEntity & IMultiBlockEntityContainer> void formMulti(T be) {
        SearchCache<T> cache = new SearchCache<>();
        List<T> frontier = new ArrayList<>();
        frontier.add(be);
        formMulti(be.getType(), be.getWorld(), cache, frontier);


    }

    private static <T extends BlockEntity & IMultiBlockEntityContainer> void formMulti(BlockEntityType<?> type,
                                                                                       BlockView level, SearchCache<T> cache, List<T> frontier) {
        PriorityQueue<Pair<Integer, T>> creationQueue = makeCreationQueue();
        Set<BlockPos> visited = new HashSet<>();
        Direction.Axis mainAxis = frontier.get(0)
                .getMainConnectionAxis();

        // essentially, if it's a vertical multi then the search won't be restricted by
        // Y
        // alternately, a horizontal multi search shouldn't be restricted by X or Z
        int minX = (mainAxis == Direction.Axis.Y ? Integer.MAX_VALUE : Integer.MIN_VALUE);
        int minY = (mainAxis != Direction.Axis.Y ? Integer.MAX_VALUE : Integer.MIN_VALUE);
        int minZ = (mainAxis == Direction.Axis.Y ? Integer.MAX_VALUE : Integer.MIN_VALUE);

        for (T be : frontier) {
            BlockPos pos = be.getPos();
            minX = Math.min(pos.getX(), minX);
            minY = Math.min(pos.getY(), minY);
            minZ = Math.min(pos.getZ(), minZ);
        }
        if (mainAxis == Direction.Axis.Y)
            minX -= frontier.get(0)
                    .getMaxWidth();
        if (mainAxis != Direction.Axis.Y)
            minY -= frontier.get(0)
                    .getMaxWidth();
        if (mainAxis == Direction.Axis.Y)
            minZ -= frontier.get(0)
                    .getMaxWidth();

        while (!frontier.isEmpty()) {
            T part = frontier.remove(0);
            BlockPos partPos = part.getPos();
            if (visited.contains(partPos))
                continue;

            visited.add(partPos);

            int amount = tryToFormNewMulti(part, cache, true);
            if (amount > 1) {
                creationQueue.add(Pair.of(amount, part));
            }

            for (Direction.Axis axis : Iterate.axes) {
                Direction dir = Direction.get(Direction.AxisDirection.NEGATIVE, axis);
                BlockPos next = partPos.offset(dir);

                if (next.getX() <= minX || next.getY() <= minY || next.getZ() <= minZ)
                    continue;
                if (visited.contains(next))
                    continue;
                T nextBe = partAt(type, level, next);
                if (nextBe == null)
                    continue;
                if (nextBe.isRemoved())
                    continue;
                frontier.add(nextBe);
            }

                //连接一号
//            if (part instanceof BatteryEntity){
//                System.out.println(part.getController()+" <-c " + part.getPos());
//                ((BatteryEntity) part).updateBlockStateForConnectivity();
//            }

        }
        visited.clear();

        while (!creationQueue.isEmpty()) {
            Pair<Integer, T> next = creationQueue.poll();
            T toCreate = next.getValue();
            if (visited.contains(toCreate.getPos()))
                continue;

            visited.add(toCreate.getPos());
            tryToFormNewMulti(toCreate, cache, false);


            //连接二号
//            if (toCreate instanceof BatteryEntity){
//                System.out.println(toCreate.getController()+" <-c " + toCreate.getPos());
//                ((BatteryEntity) toCreate).updateBlockStateForConnectivity();
//            }


        }


    }

    private static <T extends BlockEntity & IMultiBlockEntityContainer> int tryToFormNewMulti(T be, SearchCache<T> cache,
                                                                                              boolean simulate) {
        int bestWidth = 1;
        int bestAmount = -1;
        if (!be.isController())
            return 0;

        int radius = be.getMaxWidth();
        for (int w = 1; w <= radius; w++) {
            int amount = tryToFormNewMultiOfWidth(be, w, cache, true);
            if (amount < bestAmount)
                continue;
            bestWidth = w;
            bestAmount = amount;
        }

        if (!simulate) {
            int beWidth = be.getWidth();
            if (beWidth == bestWidth && beWidth * beWidth * be.getHeight() == bestAmount)
                return bestAmount;

            splitMultiAndInvalidate(be, cache, false);
            if (be instanceof IMultiBlockEntityContainerEnergy iEnergy && iEnergy.hasEnergyStorage())
                iEnergy.setEnergyCapacity(bestAmount);

            tryToFormNewMultiOfWidth(be, bestWidth, cache, false);

            be.preventConnectivityUpdate();
            be.setWidth(bestWidth);
            be.setHeight(bestAmount / bestWidth / bestWidth);
            be.notifyMultiUpdated();
        }
        return bestAmount;
    }

    private static <T extends BlockEntity & IMultiBlockEntityContainer> int tryToFormNewMultiOfWidth(T be, int width,
                                                                                                     SearchCache<T> cache, boolean simulate) {
        int amount = 0;
        int height = 0;
        BlockEntityType<?> type = be.getType();
        World level = be.getWorld();
        if (level == null)
            return 0;
        BlockPos origin = be.getPos();


        EnergyStorage beEnergy = null;
        if (be instanceof IMultiBlockEntityContainerEnergy iEnergy && iEnergy.hasEnergyStorage()) {
            beEnergy = iEnergy.getStoredEnergy();
        }
        Direction.Axis axis = be.getMainConnectionAxis();

        Search: for (int yOffset = 0; yOffset < be.getMaxLength(axis, width); yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = switch (axis) {
                        case X -> origin.add(yOffset, xOffset, zOffset);
                        case Y -> origin.add(xOffset, yOffset, zOffset);
                        case Z -> origin.add(xOffset, zOffset, yOffset);
                    };
                    Optional<T> part = cache.getOrCache(type, level, pos);
                    if (part.isEmpty())
                        break Search;

                    T controller = part.get();
                    int otherWidth = controller.getWidth();
                    if (otherWidth > width)
                        break Search;
                    if (otherWidth == width && controller.getHeight() == be.getMaxLength(axis, width))
                        break Search;

                    Direction.Axis conAxis = controller.getMainConnectionAxis();
                    if (axis != conAxis)
                        break Search;

                    BlockPos conPos = controller.getPos();
                    if (!conPos.equals(origin)) {
                        if (axis == Direction.Axis.Y) {
                            if (conPos.getX() < origin.getX())
                                break Search;
                            if (conPos.getZ() < origin.getZ())
                                break Search;
                            if (conPos.getX() + otherWidth > origin.getX() + width)
                                break Search;
                            if (conPos.getZ() + otherWidth > origin.getZ() + width)
                                break Search;
                        } else { // horizontal multi, like an ItemVault
                            if (axis == Direction.Axis.Z && conPos.getX() < origin.getX())
                                break Search;
                            if (conPos.getY() < origin.getY())
                                break Search;
                            if (axis == Direction.Axis.X && conPos.getZ() < origin.getZ())
                                break Search;
                            if (axis == Direction.Axis.Z && conPos.getX() + otherWidth > origin.getX() + width)
                                break Search;
                            if (conPos.getY() + otherWidth > origin.getY() + width)
                                break Search;
                            if (axis == Direction.Axis.X && conPos.getZ() + otherWidth > origin.getZ() + width)
                                break Search;
                        }
                    }
                }
            }
            amount += width * width;
            height++;
        }

        if (simulate)
            return amount;

        Object extraData = be.getExtraData();

        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = switch (axis) {
                        case X -> origin.add(yOffset, xOffset, zOffset);
                        case Y -> origin.add(xOffset, yOffset, zOffset);
                        case Z -> origin.add(xOffset, zOffset, yOffset);
                    };
                    T part = partAt(type, level, pos);
                    if (part == null)
                        continue;
                    if (part == be)
                        continue;

                    extraData = be.modifyExtraData(extraData);

                    if (part instanceof IMultiBlockEntityContainerEnergy iEnergyPart && iEnergyPart.hasEnergyStorage()) {
                        EnergyStorage energyAt = iEnergyPart.getStoredEnergy();

                        if (energyAt.getAmount()!=0) {
                            // making this generic would be a rather large mess, unfortunately
                            if (be instanceof IMultiBlockEntityContainerEnergy iEnergyBE && iEnergyBE.hasEnergyStorage()
                                    && beEnergy != null) {
                                try(Transaction t=Transaction.openOuter()) {
                                    beEnergy.insert(energyAt.getAmount(), t);
                                    t.commit();
                                }
                            }
                        }
                        try(Transaction t=Transaction.openOuter()) {
                            energyAt.extract(energyAt.getAmount(), t);
                            t.commit();
                        }
                    }

                    splitMultiAndInvalidate(part, cache, false);
                    part.setController(origin);
                    part.preventConnectivityUpdate();
                    cache.put(pos, be);
                    part.setHeight(height);
                    part.setWidth(width);
                    part.notifyMultiUpdated();

                    //连接更新状态
//                    if (part instanceof BatteryEntity){
//                        ((BatteryEntity)part).updateBlockStateForConnectivity();
//                    }

                }
            }
        }
        be.setExtraData(extraData);
        be.notifyMultiUpdated();
        return amount;
    }

    public static <T extends BlockEntity & IMultiBlockEntityContainer> void splitMulti(T be) {
        splitMultiAndInvalidate(be, null, false);
    }

    // tryReconnect helps whenever only a few tanks have been removed
    private static <T extends BlockEntity & IMultiBlockEntityContainer> void splitMultiAndInvalidate(T be,
                                                                                                     @Nullable SearchCache<T> cache, boolean tryReconnect) {
        World level = be.getWorld();
        if (level == null)
            return;

        be = be.getControllerBE();
        if (be == null)
            return;

        int height = be.getHeight();
        int width = be.getWidth();
        if (width == 1 && height == 1)
            return;

        BlockPos origin = be.getPos();
        List<T> frontier = new ArrayList<>();
        Direction.Axis axis = be.getMainConnectionAxis();


        List<T> affectedBlocks = new ArrayList<>(); // 用于存储受影响的方块实体

        Long toDistribute = 0L;
        long maxCapacity = 0;
        if (be instanceof IMultiBlockEntityContainerEnergy iEnergyBE && iEnergyBE.hasEnergyStorage()) {
            toDistribute = iEnergyBE.getStoredEnergy().getAmount();
            iEnergyBE.setEnergyCapacity( 1);
            maxCapacity = iEnergyBE.getEnergyCapacity();
            if (toDistribute!=0 && !be.isRemoved())
                toDistribute-=maxCapacity;

        }

        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {

                    BlockPos pos = switch (axis) {
                        case X -> origin.add(yOffset, xOffset, zOffset);
                        case Y -> origin.add(xOffset, yOffset, zOffset);
                        case Z -> origin.add(xOffset, zOffset, yOffset);
                    };

                    T partAt = partAt(be.getType(), level, pos);
                    if (partAt == null)
                        continue;
                    if (!partAt.getController()
                            .equals(origin))
                        continue;
                    T controllerBE = partAt.getControllerBE();
                    partAt.setExtraData((controllerBE == null ? null : controllerBE.getExtraData()));
                    partAt.removeController(true);

                    affectedBlocks.add(partAt);  // 收集所有被影响的方块实体

                    //断开更新
                    if (partAt instanceof BatteryEntity){
//                        ((BatteryEntity)partAt).updateBlockStateForConnectivity();
                    }


                    if (toDistribute!=0 && partAt != be) {
                        Long copy = toDistribute;
                        EnergyStorage energyInv =
                                (partAt instanceof IMultiBlockEntityContainerEnergy iEnergyPart ? iEnergyPart.getStoredEnergy() : null);
                        // making this generic would be a rather large mess, unfortunately
                            long split = Math.min(maxCapacity, toDistribute);
                            copy=split;
                            toDistribute-=split;
                            if (energyInv != null)
                            try(Transaction t=Transaction.openOuter()){
                                energyInv.insert(copy,t);
                                t.commit();
                            }

                    }
                    if (tryReconnect) {
                        frontier.add(partAt);
                        partAt.preventConnectivityUpdate();
                    }
                    if (cache != null)
                        cache.put(pos, partAt);
                }
            }

            // 在完成所有拆分后，遍历 affectedBlocks 并调用 update
//            for (T affected : affectedBlocks) {
//                if (affected instanceof BatteryEntity){
//                    System.out.println(affected.getController()+" <-c " + affected.getPos());
//                    ((BatteryEntity) affected).updateBlockStateForConnectivity();
//                }
//            }

        }
        if (tryReconnect)
            formMulti(be.getType(), level, cache == null ? new SearchCache<>() : cache, frontier);
    }

    private static <T extends BlockEntity & IMultiBlockEntityContainer> PriorityQueue<Pair<Integer, T>> makeCreationQueue() {
        return new PriorityQueue<>((one, two) -> two.getKey() - one.getKey());
    }

    @Nullable
    public static <T extends BlockEntity & IMultiBlockEntityContainer> T partAt(BlockEntityType<?> type, BlockView level,
                                                                                BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && be.getType() == type && !be.isRemoved())
            return checked(be);
        return null;
    }

    public static <T extends BlockEntity & IMultiBlockEntityContainer> boolean isConnected(BlockView level, BlockPos pos,
                                                                                           BlockPos other) {
        T one = checked(level.getBlockEntity(pos));
        T two = checked(level.getBlockEntity(other));
        if (one == null || two == null)
            return false;
        return one.getController()
                .equals(two.getController());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity & IMultiBlockEntityContainer> T checked(BlockEntity be) {
        if (be instanceof IMultiBlockEntityContainer)
            return (T) be;
        return null;
    }

    private static class SearchCache<T extends BlockEntity & IMultiBlockEntityContainer> {
        Map<BlockPos, Optional<T>> controllerMap;

        public SearchCache() {
            controllerMap = new HashMap<>();
        }

        void put(BlockPos pos, T target) {
            controllerMap.put(pos, Optional.of(target));
        }

        void putEmpty(BlockPos pos) {
            controllerMap.put(pos, Optional.empty());
        }

        boolean hasVisited(BlockPos pos) {
            return controllerMap.containsKey(pos);
        }

        Optional<T> getOrCache(BlockEntityType<?> type, BlockView level, BlockPos pos) {
            if (hasVisited(pos))
                return controllerMap.get(pos);

            T partAt = partAt(type, level, pos);
            if (partAt == null) {
                putEmpty(pos);
                return Optional.empty();
            }
            T controller = checked(level.getBlockEntity(partAt.getController()));
            if (controller == null) {
                putEmpty(pos);
                return Optional.empty();
            }
            put(pos, controller);
            return Optional.of(controller);
        }
    }
}
