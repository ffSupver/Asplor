package com.ffsupver.asplor.networking.packet.ranger;

import com.ffsupver.asplor.networking.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class RangerInputC2SPacket {
    public static void send(UUID uuid, Identifier worldKey, boolean jump, boolean down){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(uuid);
        buf.writeIdentifier(worldKey);


        buf.writeBoolean(jump);
        buf.writeBoolean(down);

        ClientPlayNetworking.send(ModPackets.RANGER_INPUT,buf);
    }

}
