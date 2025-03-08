package com.ffsupver.asplor.networking.packet.worldAdder;

import com.ffsupver.asplor.world.BiomesSupplier;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.ArrayList;

import static com.ffsupver.asplor.world.WorldData.createNewPlantWithOrbit;

public class CreateWorldC2SPacketHandler {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        ServerWorld serverWorld = player.getServerWorld();
        PlayerEntity user = player;
        int size = buf.readInt();
        ArrayList<String> functionList = new ArrayList<>();
        for (int i = 0 ;i<size;i++){
            functionList.add(buf.readString());
        }

        int blockSize = buf.readInt();
        ArrayList<String> blockList = new ArrayList<>();
        for (int i = 0 ;i<blockSize;i++){
            blockList.add(buf.readString());
        }

        int biomesSize = buf.readInt();
        ArrayList<String> biomesList = new ArrayList<>();
        for (int i = 0 ;i<biomesSize;i++){
            biomesList.add(buf.readString());
        }

        PlanetCreatingData planetCreatingData = PlanetCreatingData.readFromBuffer(buf);

        Identifier worldId = buf.readIdentifier();

        boolean teleport = buf.readBoolean();

        server.execute(()->{
            RegistryKey<World> worldKey =RegistryKey.of(RegistryKeys.WORLD,worldId);
            createNewPlantWithOrbit(
                    server,worldKey,
                    BiomesSupplier.getBiomeSourceSetting(
                            biomesList.isEmpty()?
                                    BiomesSupplier.toBiomeEntryListKey(server,BiomesSupplier.BIOMES_LIST) :
                                    BiomesSupplier.toBiomeEntryList(server,biomesList) ,
                            server.getOverworld().getRandom()
                    ),
                    functionList,
                    blockList,
                    planetCreatingData
            );
            if (teleport){
                FabricDimensions.teleport(user, server.getWorld(worldKey), new TeleportTarget(user.getPos(), user.getVelocity(), user.getYaw(), user.getPitch()));
            }
        });
    }
}
