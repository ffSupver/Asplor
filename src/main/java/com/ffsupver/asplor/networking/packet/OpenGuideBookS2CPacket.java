package com.ffsupver.asplor.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

import static com.ffsupver.asplor.networking.ModPackets.OPEN_GUIDE_BOOK_SYNC;

public class OpenGuideBookS2CPacket  {
    public static void send(ServerPlayerEntity player, Hand hand) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(hand);
        ServerPlayNetworking.send(player, OPEN_GUIDE_BOOK_SYNC, buf);
    }



}
