package com.ffsupver.asplor.networking.packet.large_map;

import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.item.item.largeMap.LargeMapState;
import com.ffsupver.asplor.screen.largeMap.LargeMapScreen;
import io.netty.util.IllegalReferenceCountException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public class OpenLargeMapS2CPacketHandler {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Hand hand = buf.readEnumConstant(Hand.class);
        int mapId = buf.readInt();
        LargeMapState largeMapState = LargeMapState.readFromBuf(buf);
        int startX = buf.readInt();
        int startZ = buf.readInt();
        client.execute(() -> {
            try {
                ItemStack itemStack = client.player.getStackInHand(hand);
                if (itemStack.isOf(ModItems.LARGE_MAP)){
                    client.setScreen(new LargeMapScreen(Text.literal("a"),mapId,largeMapState,startX,startZ));
                }
            } catch (IllegalReferenceCountException e) {
                e.printStackTrace();
            }
        });
    }
}
