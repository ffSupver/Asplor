package com.ffsupver.asplor.networking.packet.worldAdder;

import com.ffsupver.asplor.networking.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

import static com.ffsupver.asplor.world.WorldData.createWorldKey;

public class CreateWorldC2SPacket {
    public static void send(List<String> functionList, List<String> blockList, List<String> biomesList, PlanetCreatingData planetData, Identifier worldKey,boolean teleport){
        PacketByteBuf buf = PacketByteBufs.create();
        int size = functionList.size();
        buf.writeInt(size);
        for (int i = 0 ;i<size;i++){
            buf.writeString(functionList.get(i));
        }
        int blockSize = blockList.size();
        buf.writeInt(blockSize);
        for (int i = 0 ;i<blockSize;i++){
            buf.writeString(blockList.get(i));
        }
        int biomesSize = biomesList.size();
        buf.writeInt(biomesSize);
        for (int i = 0 ;i<biomesSize;i++){
            buf.writeString(biomesList.get(i));
        }

        planetData.writeToBuffer(buf);

        buf.writeIdentifier(worldKey);

        buf.writeBoolean(teleport);

        ClientPlayNetworking.send(ModPackets.WORLD_ADDER_CREATE_WORLD,buf);
    }

    public static void send(List<String> functionList, List<String> blockList, List<String> biomesList, PlanetCreatingData planetData){
        send(functionList,blockList,biomesList,planetData,createWorldKey(true).getValue(),true);
    }
}
