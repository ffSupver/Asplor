package com.ffsupver.asplor.networking;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.networking.packet.OpenBackpackC2SPacket;
import com.ffsupver.asplor.networking.packet.large_map.LargeMapDataS2CPacket;
import com.ffsupver.asplor.networking.packet.large_map.OpenGuideBookS2CPacketHandler;
import com.ffsupver.asplor.networking.packet.large_map.OpenLargeMapS2CPacketHandler;
import com.ffsupver.asplor.networking.packet.large_map.RequestLargeMapDataC2SPacket;
import com.ffsupver.asplor.networking.packet.ranger.RangerInputC2SPacketHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier OPEN_BACKPACK = new Identifier(Asplor.MOD_ID,"open_backpack");
    public static final Identifier OPEN_BACKPACK_SYNC = new Identifier(Asplor.MOD_ID,"open_backpack");
    public static final Identifier OPEN_GUIDE_BOOK_SYNC = new Identifier(Asplor.MOD_ID,"open_guide_book");
    public static final Identifier OPEN_LARGE_MAP_SYNC = new Identifier(Asplor.MOD_ID,"open_large_map");
    public static final Identifier REQUEST_LARGE_MAP_DATA = new Identifier(Asplor.MOD_ID, "request_large_map_data");
    public static final Identifier LARGE_MAP_DATA = new Identifier(Asplor.MOD_ID, "large_map_data");
    public static final Identifier RANGER_INPUT = new Identifier(Asplor.MOD_ID, "ranger_input");



    public static void registerC2SPack(){
        ServerPlayNetworking.registerGlobalReceiver(OPEN_BACKPACK, OpenBackpackC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_LARGE_MAP_DATA, RequestLargeMapDataC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(RANGER_INPUT, RangerInputC2SPacketHandler::receive);

    }
    public static void registerS2CPack(){
        ClientPlayNetworking.registerGlobalReceiver(OPEN_GUIDE_BOOK_SYNC, OpenGuideBookS2CPacketHandler::receive);
        ClientPlayNetworking.registerGlobalReceiver(OPEN_LARGE_MAP_SYNC, OpenLargeMapS2CPacketHandler::receive);
        ClientPlayNetworking.registerGlobalReceiver(LARGE_MAP_DATA, LargeMapDataS2CPacket::receive);
    }
}
