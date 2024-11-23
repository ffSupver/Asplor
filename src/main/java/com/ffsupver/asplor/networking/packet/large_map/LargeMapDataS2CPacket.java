package com.ffsupver.asplor.networking.packet.large_map;

import com.ffsupver.asplor.item.item.largeMap.LargeMapState;
import com.ffsupver.asplor.screen.largeMap.LargeMapScreen;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LargeMapDataS2CPacket {
    private final int mapId;
    private final Map<Long, byte[]> colors;
    private final ArrayList<LargeMapState.MapIconData> iconList;

    public LargeMapDataS2CPacket(int mapId, Map<Long, byte[]> colors, ArrayList<LargeMapState.MapIconData> iconList) {
        this.mapId = mapId;
        this.colors = colors;
        this.iconList = iconList;
    }

    public LargeMapDataS2CPacket(PacketByteBuf buf) {
        this.mapId = buf.readInt();
        int chunkCount = buf.readInt();
        this.colors = new HashMap<>();
        for (int i =0;i<chunkCount;i++){
           long chunk = buf.readLong();
           byte[] colors = buf.readByteArray();
           this.colors.put(chunk,colors);
        }

        int iconCount = buf.readInt();
        this.iconList = new ArrayList<>();
        for (int i=0;i<iconCount;i++){
            LargeMapState.MapIconData mapIconData = LargeMapState.MapIconData.readFromBuf(buf);
            this.iconList.add(mapIconData);
        }
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(mapId);
        buf.writeInt(colors.entrySet().size());
        if (!colors.isEmpty()){
            for (Map.Entry<Long, byte[]> entry : colors.entrySet()) {
                buf.writeLong(entry.getKey());
                buf.writeByteArray(entry.getValue());

            }
        }

        buf.writeInt(this.iconList.size());
        for (LargeMapState.MapIconData mapIconData : this.iconList){
            mapIconData.writeToBuf(buf);
        }

    }


    // 创建 PacketByteBuf 对象
    public PacketByteBuf toPacketByteBuf() {
        PacketByteBuf buf = PacketByteBufs.create();
        this.write(buf);
        return buf;
    }

    public void handle(ClientPlayNetworkHandler handler) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            if (client.currentScreen instanceof LargeMapScreen largeMapScreen){
                largeMapScreen.updateMapColor(colors);
                largeMapScreen.updateIcon(iconList);
            }
        });
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
        LargeMapDataS2CPacket packet = new LargeMapDataS2CPacket(buf);
        packet.handle(handler);
    }
}