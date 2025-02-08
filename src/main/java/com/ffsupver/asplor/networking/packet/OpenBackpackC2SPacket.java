package com.ffsupver.asplor.networking.packet;

import com.ffsupver.asplor.screen.backpack.*;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

import static com.ffsupver.asplor.screen.backpack.BackpackBaseHandler.backpackDataKey;

public class OpenBackpackC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        //服务器运行内容
        ItemStack mainHandStack = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack chestItem = player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack needToOpenStack;
        EquipmentSlot needToOpenSlot = null;
        if (mainHandStack.hasNbt()&&mainHandStack.getNbt() != null && mainHandStack.getNbt().contains(backpackDataKey)) {
            needToOpenStack=mainHandStack;
            needToOpenSlot=EquipmentSlot.MAINHAND;
        }else if(chestItem.hasNbt()&&chestItem.getNbt() != null && chestItem.getNbt().contains(backpackDataKey)){
            needToOpenStack=chestItem;
            needToOpenSlot=EquipmentSlot.CHEST;
        }else {
            needToOpenStack=ItemStack.EMPTY;
        }


        if (!needToOpenStack.equals(ItemStack.EMPTY)) {
            EquipmentSlot finalNeedToOpenSlot = needToOpenSlot;
            player.openHandledScreen(
                    new ExtendedScreenHandlerFactory() {
                        @Override
                        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                            buf.writeBlockPos(player.getBlockPos());
                        }

                        @Override
                        public Text getDisplayName() {
                            return BackpackBaseHandler.title;
                        }

                        @Nullable
                        @Override
                        public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                            if (needToOpenStack.getNbt().getCompound(backpackDataKey).contains(BackpackBaseHandler.backpackTypeKey,8)){
                                return switch (needToOpenStack.getNbt().getCompound(backpackDataKey).getString(BackpackBaseHandler.backpackTypeKey)){
                                    case "small" -> new BackpackSmallHandler(syncId,playerInventory, finalNeedToOpenSlot);
                                    case "large" -> new BackpackLargeHandler(syncId,playerInventory, finalNeedToOpenSlot);
                                    default -> new BackpackSmallHandler(syncId,playerInventory, finalNeedToOpenSlot);
                                };
                            }
                            return new BackpackSmallHandler(syncId,playerInventory, finalNeedToOpenSlot);
                        }
                    }
            );
        }
    }
}
