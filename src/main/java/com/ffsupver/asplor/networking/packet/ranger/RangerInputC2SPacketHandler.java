package com.ffsupver.asplor.networking.packet.ranger;

import com.ffsupver.asplor.entity.custom.Ranger;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class RangerInputC2SPacketHandler {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
       UUID uuid = buf.readUuid();
       Identifier worldKey = buf.readIdentifier();


        boolean jump = buf.readBoolean();
       boolean down = buf.readBoolean();

       ServerWorld world = server.getWorld(RegistryKey.of(RegistryKeys.WORLD,worldKey));
        if (world != null && world.getEntity(uuid) instanceof Ranger ranger) {
            ranger.setControl(jump,down);
        }
    }
}
