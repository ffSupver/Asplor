package com.ffsupver.asplor.block.chunkLoader;

import com.ibm.icu.impl.Pair;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkLoaderServer {
    public static Map<RegistryKey<World>, List<Pair<ChunkPos, BlockPos>>> FORCE_LOAD_CHUNKS = new HashMap<>();
    private static final String DATA_FILE_NAME = "asplor_forced_chunks.nbt";
    public static void addChunk(World world,ChunkPos chunkPos,BlockPos blockPos){
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.setChunkForced(chunkPos.x,chunkPos.z,true);
            RegistryKey<World> worldKey = serverWorld.getRegistryKey();
            if (!hasChunk(serverWorld,chunkPos)){
                FORCE_LOAD_CHUNKS.computeIfAbsent(worldKey, k -> new ArrayList<>()).add(Pair.of(chunkPos, blockPos));
            }
        }
    }
    public static void removeChunk(World world,ChunkPos chunkPos,BlockPos blockPos){
        if (world instanceof ServerWorld serverWorld){
            RegistryKey<World> worldKey = serverWorld.getRegistryKey();
            List<Pair<ChunkPos, BlockPos>> chunkList = FORCE_LOAD_CHUNKS.get(worldKey);
            if (chunkList != null) {
                if (chunkList.remove(Pair.of(chunkPos, blockPos))) {
                    serverWorld.setChunkForced(chunkPos.x, chunkPos.z, false);
                }
            }
        }
    }

    public static boolean hasChunk(ServerWorld serverWorld,ChunkPos chunkPos){
        if (FORCE_LOAD_CHUNKS.containsKey(serverWorld.getRegistryKey())){
            for (Pair<ChunkPos, BlockPos> chunkPosBlockPosPair : FORCE_LOAD_CHUNKS.get(serverWorld.getRegistryKey())) {
                if (chunkPosBlockPosPair.first.toLong() == chunkPos.toLong()) {
                    return true;
                }
            }
        }
        return false;
    }



    private static Path getDataPath(MinecraftServer server){
        return server.getSavePath(new WorldSavePath("data"));
    }

    // 保存加载的区块到文件
    public static void saveChunksToDisk(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            File file = getDataPath(server).resolve(DATA_FILE_NAME).toFile();

            // 存储区块数据的NBT
            NbtCompound nbt = new NbtCompound();
            NbtList chunksList = new NbtList();

            // 遍历每个维度和对应的区块和块位置
            List<Pair<ChunkPos, BlockPos>> chunkList = FORCE_LOAD_CHUNKS.get(world.getRegistryKey());
            if (chunkList != null) {
                for (Pair<ChunkPos, BlockPos> pair : chunkList) {
                    ChunkPos chunkPos = pair.first;
                    BlockPos blockPos = pair.second;

                    // 保存该维度下区块和块位置的信息
                    NbtCompound chunkData = new NbtCompound();
                    chunkData.putString("dimension", world.getRegistryKey().getValue().toString());
                    chunkData.putInt("x", chunkPos.x);
                    chunkData.putInt("z", chunkPos.z);
                    chunkData.putInt("block_x", blockPos.getX());
                    chunkData.putInt("block_y", blockPos.getY());
                    chunkData.putInt("block_z", blockPos.getZ());

                    chunksList.add(chunkData);
                }
            }

            nbt.put("loaded_chunks", chunksList);

            try {
                NbtIo.write(nbt, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        NbtCompound nbt = new NbtCompound();
//        NbtList chunksList = new NbtList();
//
//        // 遍历每个维度和对应的区块
//        for (Map.Entry<RegistryKey<World>, List<Pair<ChunkPos, BlockPos>>> entry : FORCE_LOAD_CHUNKS.entrySet()) {
//            ServerWorld world = server.getWorld(entry.getKey());
//            ChunkPos chunkPos = entry.getValue();
//
//            // 获取维度ID
//            String dimensionKey = world.getRegistryKey().getValue().toString();
//
//            // 创建保存该维度下区块的信息
//            NbtCompound chunkData = new NbtCompound();
//            chunkData.putString("dimension", dimensionKey);
//            chunkData.putInt("x", chunkPos.x);
//            chunkData.putInt("z", chunkPos.z);
//
//            chunksList.add(chunkData);
//        }
//
//        nbt.put("loaded_chunks", chunksList);
//
//        // 保存到指定的文件
//        File file = new File(getDataPath(server).toFile(), DATA_FILE_NAME);
//        try {
//            NbtIo.write(nbt, file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    // 从文件加载强制加载的区块
    public static void loadChunksFromDisk(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            File file = getDataPath(server).resolve(DATA_FILE_NAME).toFile();

            if (!file.exists()) {
                continue; // 文件不存在时跳过
            }

            try {
                NbtCompound nbt = NbtIo.read(file);
                NbtList chunksList = nbt.getList("loaded_chunks", 10);

                // 遍历区块数据并加载到 FORCE_LOAD_CHUNKS
                for (int i = 0; i < chunksList.size(); i++) {
                    NbtCompound chunkData = chunksList.getCompound(i);
                    String dimensionKey = chunkData.getString("dimension");
                    int x = chunkData.getInt("x");
                    int z = chunkData.getInt("z");
                    int blockX = chunkData.getInt("block_x");
                    int blockY = chunkData.getInt("block_y");
                    int blockZ = chunkData.getInt("block_z");

                    // 获取维度和区块位置
                    RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(dimensionKey));
                    ChunkPos chunkPos = new ChunkPos(x, z);
                    BlockPos blockPos = new BlockPos(blockX, blockY, blockZ);

                    FORCE_LOAD_CHUNKS.computeIfAbsent(worldKey, k -> new ArrayList<>()).add(Pair.of(chunkPos, blockPos));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        File file = new File(getDataPath(server).toFile(), DATA_FILE_NAME);
//        if (!file.exists()) {
//            return; // 文件不存在时直接返回
//        }
//
//        try {
//            NbtCompound nbt = NbtIo.read(file);
//            NbtList chunksList = null;
//            if (nbt != null) {
//                chunksList = nbt.getList("loaded_chunks", 10);
//            }
//
//            // 遍历区块数据并加载到 FORCE_LOAD_CHUNKS
//            if (chunksList != null) {
//                for (int i = 0; i < chunksList.size(); i++) {
//                    NbtCompound chunkData = chunksList.getCompound(i);
//                    String dimensionKey = chunkData.getString("dimension");
//                    int x = chunkData.getInt("x");
//                    int z = chunkData.getInt("z");
//
//                    // 获取维度和区块位置
//                    RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(dimensionKey));
//                    ChunkPos chunkPos = new ChunkPos(x, z);
//                    FORCE_LOAD_CHUNKS.put(worldKey, chunkPos);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }



    public static void registerLoadChunkFunction(){
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (RegistryKey<World> worldKey : FORCE_LOAD_CHUNKS.keySet()){
                ServerWorld world = server.getWorld(worldKey);
                List<Pair<ChunkPos,BlockPos>> chunkList = FORCE_LOAD_CHUNKS.get(worldKey);


                //遍历区块加载器的常加载
                for (Pair<ChunkPos,BlockPos> chunkWithBlockPos : chunkList){
                    ChunkPos chunkPos = chunkWithBlockPos.first;

                    //加载随机刻
                    world.tickChunk(world.getChunk(chunkPos.x, chunkPos.z), world.getServer().getGameRules().getInt(GameRules.RANDOM_TICK_SPEED));
                }
            }
        });
        ServerLifecycleEvents.SERVER_STARTED.register(ChunkLoaderServer::loadChunksFromDisk);
        ServerLifecycleEvents.SERVER_STOPPING.register(ChunkLoaderServer::saveChunksToDisk);
    }
}
