package com.ffsupver.asplor.block.blocks;

import com.ffsupver.asplor.block.alloyChest.AlloyChestEntity;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ChestSorter extends Block implements IWrenchable {
    public ChestSorter(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isEmpty() && world.getBlockState(pos).getBlock() instanceof ChestSorter) {
            itemStack = tryInsertToChest(itemStack,pos,world);
            player.setStackInHand(hand, itemStack);
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    public ItemStack tryInsertToChest(ItemStack itemStack,BlockPos pos,World world){
        ArrayList<BlockPos> chests = new ArrayList<>();
        BlockPos checkPos = pos;
        BlockPos nextCheckPos = checkPos;
        for (int j = 0;j < 10;j++){
            for (Direction direction : Direction.values()) {
                BlockPos tmpCheckPos = checkPos.offset(direction);
                if (!chests.contains(tmpCheckPos)) {
                    Inventory inventory = getInventory(world,tmpCheckPos);
                    if (inventory != null) {
                        itemStack = tryInsertToInventory(itemStack, inventory);
                        nextCheckPos = tmpCheckPos;
                        chests.add(tmpCheckPos);
                    }
                }
            }
            checkPos = nextCheckPos;
        }
        return itemStack;
    }

    private Inventory getInventory(World world, BlockPos tmpCheckPos){
        BlockEntity blockEntity = world.getBlockEntity(tmpCheckPos);
        BlockState blockState = world.getBlockState(tmpCheckPos);
        if (blockState.getBlock() instanceof ChestBlock chestBlock){
            return ChestBlock.getInventory(chestBlock,blockState,world,tmpCheckPos,true);
        }
        if (blockEntity instanceof AlloyChestEntity alloyChestEntity){
            return alloyChestEntity;
        }
        return null;
    }

    private ItemStack tryInsertToInventory(ItemStack itemStack,Inventory inventory){
        Item item = itemStack.getItem();
        if (inventory.containsAny(itemStack1 -> itemStack1.getItem().equals(item))){
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack itemStack1 = inventory.getStack(i);
                if (itemStack.isEmpty()){
                    return ItemStack.EMPTY;
                }
                if (itemStack1.isOf(item)){
                    int merged = Math.min(itemStack1.getCount() + itemStack.getCount(),itemStack1.getMaxCount());
                    int extract = merged - itemStack1.getCount();
                    inventory.setStack(i,itemStack1.copyWithCount(merged));
                    itemStack = itemStack.copyWithCount(itemStack.getCount() - extract);
                }
                if (itemStack1.isEmpty()){
                    inventory.setStack(i,itemStack);
                    return ItemStack.EMPTY;
                }
            }

        }
        return itemStack;
    }
}
