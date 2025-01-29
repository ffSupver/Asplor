package com.ffsupver.asplor.networking.packet.largeMap;

import com.ffsupver.asplor.item.item.largeMap.LargeMapState;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
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

}
