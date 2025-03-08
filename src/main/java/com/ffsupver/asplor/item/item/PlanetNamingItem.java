package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.block.planetLocator.PlanetLocatorEntity;
import com.ffsupver.asplor.networking.packet.worldAdder.CreateWorldC2SPacket;
import com.ffsupver.asplor.networking.packet.worldAdder.PlanetCreatingData;
import com.ffsupver.asplor.world.WorldData;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;

public class PlanetNamingItem extends Item {
    public PlanetNamingItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity user = context.getPlayer();
        Hand hand = context.getHand();
        ItemStack itemStack = user.getStackInHand(hand);

        if (world.getBlockEntity(context.getBlockPos()) instanceof PlanetLocatorEntity planetLocatorEntity){
            if (itemStack.hasCustomName()) {
                String name = itemStack.getName().getString();
                boolean isNameValid = Identifier.isValid(name) && !name.contains(":");
                if (isNameValid) {
                    PlanetCreatingData planetCreatingData = planetLocatorEntity.getPlanet();
                    if (planetCreatingData != null) {
                        Identifier worldId = WorldData.createWorldKey(name).getValue();
                        planetLocatorEntity.setWorldKey(worldId);
                        if (user instanceof ClientPlayerEntity clientPlayer) {
                            CreateWorldC2SPacket.send(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), planetCreatingData, worldId, false);
                        }
                        user.setStackInHand(hand, itemStack.copyWithCount(itemStack.getCount() - 1));
                        return ActionResult.SUCCESS;
                    } else {
                        user.sendMessage(Text.translatable("message.asplor.planet_naming.no_planet_data").formatted(Formatting.RED), true);
                        return ActionResult.SUCCESS;
                    }
                } else {
                    user.sendMessage(Text.translatable("message.asplor.planet_naming.invalid_name").formatted(Formatting.RED), true);
                }
            }else {
                user.sendMessage(Text.translatable("message.asplor.planet_naming.need_name").formatted(Formatting.RED), true);
                return ActionResult.SUCCESS;
            }
        }
        return super.useOnBlock(context);
    }
}
