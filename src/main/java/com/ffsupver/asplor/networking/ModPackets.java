package com.ffsupver.asplor.networking;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.networking.packet.OpenBackpackC2SPacket;
import com.ffsupver.asplor.networking.packet.OpenGuideBookS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier OPEN_BACKPACK = new Identifier(Asplor.MOD_ID,"open_backpack");
    public static final Identifier OPEN_BACKPACK_SYNC = new Identifier(Asplor.MOD_ID,"open_backpack");
    public static final Identifier OPEN_GUIDE_BOOK_SYNC = new Identifier(Asplor.MOD_ID,"open_guide_book");



    public static void registerC2SPack(){
        ServerPlayNetworking.registerGlobalReceiver(OPEN_BACKPACK, OpenBackpackC2SPacket::receive);
    }
    public static void registerS2CPack(){
        ClientPlayNetworking.registerGlobalReceiver(OPEN_GUIDE_BOOK_SYNC, OpenGuideBookS2CPacket::receive);
    }
}
