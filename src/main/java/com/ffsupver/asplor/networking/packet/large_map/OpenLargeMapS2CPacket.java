package com.ffsupver.asplor.networking.packet.large_map;

import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.item.item.largeMap.LargeMapState;
import com.ffsupver.asplor.screen.largeMap.LargeMapScreen;
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
import net.minecraft.util.math.MathHelper;

import static com.ffsupver.asplor.networking.ModPackets.OPEN_LARGE_MAP_SYNC;

public class OpenLargeMapS2CPacket {
    public static void send(ServerPlayerEntity player, Hand hand,int mapId, LargeMapState largeMapState) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(hand);
        buf.writeInt(mapId);
        largeMapState.writeToBuf(buf);
        buf.writeInt(MathHelper.floor(player.getX()));
        buf.writeInt(MathHelper.floor(player.getZ()));
        ServerPlayNetworking.send(player, OPEN_LARGE_MAP_SYNC, buf);
    }
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
