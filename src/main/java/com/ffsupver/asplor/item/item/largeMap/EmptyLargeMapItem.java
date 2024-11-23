package com.ffsupver.asplor.item.item.largeMap;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EmptyLargeMapItem extends Item {
    public EmptyLargeMapItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (world.isClient) {
            return TypedActionResult.success(itemStack);
        } else {
            if (!user.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            ItemStack newItemStack = LargeMapItem.createMap(world);
            if (itemStack.isEmpty()) {
                return TypedActionResult.consume(newItemStack);
            } else {
                if (!user.getInventory().insertStack(newItemStack.copy())) {
                    user.dropItem(newItemStack, false);
                }

                return TypedActionResult.consume(itemStack);
            }
        }
    }
}
