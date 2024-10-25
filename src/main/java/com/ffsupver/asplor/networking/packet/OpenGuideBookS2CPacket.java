package com.ffsupver.asplor.networking.packet;

import com.ffsupver.asplor.screen.guideBook.GuideBookScreen;
import io.netty.util.IllegalReferenceCountException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import static com.ffsupver.asplor.networking.ModPackets.OPEN_GUIDE_BOOK_SYNC;

public class OpenGuideBookS2CPacket  {
    public static void send(ServerPlayerEntity player, Hand hand) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(hand);
        ServerPlayNetworking.send(player, OPEN_GUIDE_BOOK_SYNC, buf);
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,PacketByteBuf buf, PacketSender responseSender) {
        Hand hand = buf.readEnumConstant(Hand.class);
        client.execute(() -> {
            try {
                // 在主线程中执行
                if (client.player != null) {

                    ItemStack itemStack = client.player.getStackInHand(hand);
                    client.setScreen(
                            new GuideBookScreen(Text.literal("a"), itemStack)
                    );
                }
            } catch (IllegalReferenceCountException e) {
                e.printStackTrace();
            }
        });
    }

}
