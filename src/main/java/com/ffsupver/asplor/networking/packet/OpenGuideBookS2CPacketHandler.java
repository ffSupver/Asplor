package com.ffsupver.asplor.networking.packet;

import com.ffsupver.asplor.screen.guideBook.GuideBookScreen;
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
public class OpenGuideBookS2CPacketHandler {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
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
