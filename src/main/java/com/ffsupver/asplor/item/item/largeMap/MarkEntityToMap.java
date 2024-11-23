package com.ffsupver.asplor.item.item.largeMap;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityInteractCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class MarkEntityToMap {
    public static void registerListener(){
        EntityInteractCallback.EVENT.register((player, hand, entity) -> {
            System.out.println("interaction with: " + entity);
            ItemStack handStack = player.getStackInHand(hand);
            if (handStack.getItem() instanceof LargeMapItem && entity instanceof CarriageContraptionEntity) {
                // 在这里处理可交互实体
                System.out.println("Custom interaction with: " + entity);
                LargeMapState largeMapState = LargeMapItem.getMapState(handStack,player.getWorld());
                if (largeMapState != null){
                    largeMapState.AddOrRemoveEntity(entity);
                    System.out.println("add "+entity);
                }
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
}
