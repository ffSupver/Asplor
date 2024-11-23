package com.ffsupver.asplor.networking.packet.large_map;

import com.ffsupver.asplor.item.item.largeMap.LargeMapItem;
import com.ffsupver.asplor.item.item.largeMap.LargeMapState;
import com.ffsupver.asplor.networking.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class RequestLargeMapDataC2SPacket {
    private final int mapId;

    private final long[] chunks;

    public RequestLargeMapDataC2SPacket(int mapId, long[] chunks) {
        this.mapId = mapId;
        this.chunks = chunks;
    }

    public RequestLargeMapDataC2SPacket(PacketByteBuf buf) {
        this.mapId = buf.readInt();
        int chunkCount = buf.readInt();
        this.chunks = new long[chunkCount];
        for (int i = 0;i<chunkCount;i++){
            chunks[i] = buf.readLong();
        }
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(mapId);
        buf.writeInt(chunks.length);

        for (long chunk : chunks){
            buf.writeLong(chunk);
        }
    }

    // 创建 PacketByteBuf 对象
    public PacketByteBuf toPacketByteBuf() {
        PacketByteBuf buf = PacketByteBufs.create();
        this.write(buf);
        return buf;
    }

    public void handle(ServerPlayNetworkHandler handler) {
        ServerWorld world = handler.player.getServerWorld();
        LargeMapState mapState = LargeMapItem.getMapState(mapId, world);
        if (mapState != null) {
            Map<Long, byte[]> colors = mapState.getColors(chunks);
            if (colors != null) {
                // 将数据发送回客户端
                LargeMapDataS2CPacket response = new LargeMapDataS2CPacket(mapId, colors,mapState.getIconDataList());
                ServerPlayNetworking.send(handler.player, ModPackets.LARGE_MAP_DATA, response.toPacketByteBuf());
            }
        }
    }

 public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        RequestLargeMapDataC2SPacket packet = new RequestLargeMapDataC2SPacket(buf);
        packet.handle(handler);
    }
}
