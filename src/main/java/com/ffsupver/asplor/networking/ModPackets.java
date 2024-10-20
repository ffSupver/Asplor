package com.ffsupver.asplor.networking;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.networking.packet.OpenBackpackC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier OPEN_BACKPACK = new Identifier(Asplor.MOD_ID,"open_backpack");
    public static final Identifier OPEN_BACKPACK_SYNC = new Identifier(Asplor.MOD_ID,"open_backpack");


    public static void registerC2SPack(){
        ServerPlayNetworking.registerGlobalReceiver(OPEN_BACKPACK, OpenBackpackC2SPacket::receive);
    }
    public static void registerS2CPack(){

    }
}
