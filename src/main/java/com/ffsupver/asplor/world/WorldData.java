package com.ffsupver.asplor.world;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.mixin.MinecraftServerAccessor;
import com.ffsupver.asplor.networking.packet.worldAdder.PlanetCreatingData;
import com.ffsupver.asplor.planet.PlanetData;
import com.ffsupver.asplor.util.NbtUtil;
import com.google.common.collect.ImmutableList;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.planets.AdAstraData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executor;

import static com.ffsupver.asplor.world.BiomesSupplier.getBiomeSource;

public class WorldData {
    private static final Map<RegistryKey<World>, Pair<ServerWorld,Pair<Pair<List<String>,List<String>>,List<Pair<RegistryEntry<Biome>,List<Float>>>>>> WORLDS = new HashMap<>();

    private static final String DATA_FILE_NAME = "asplor_worlds";
    public static final String NAMESPACE = "outer_space";


    public static RegistryKey<World> createWorldKey(boolean isPlanet) {
        return createWorldKey(isPlanet,UUID.randomUUID());
    }
    public static RegistryKey<World> createWorldKey(boolean isPlanet,UUID uuid){
        return createWorldKey((isPlanet ? "planet_" : "orbit_") + uuid);
    }

    public static RegistryKey<World> createWorldKey(String id){
        return RegistryKey.of(RegistryKeys.WORLD,new Identifier(NAMESPACE,id));
    }

    public static RegistryKey<World> createWorldKey(RegistryKey<World> planetWorldKey) {
        return createWorldKey(planetWorldKey.getValue().getPath().replace("planet_","orbit_"));
    }

    public static RegistryKey<World> getOrbitPlanetWorldKey(RegistryKey<World> orbitWorldKey) {
        return createWorldKey(orbitWorldKey.getValue().getPath().replace("orbit_","planet_"));
    }

    public static boolean isOuterSpaceKey(RegistryKey<World> worldKey){
        return worldKey.getValue().getNamespace().equals(WorldData.NAMESPACE);
    }

    public static void createNewPlantWithOrbit(MinecraftServer server, RegistryKey<World> worldKey, List<Pair<RegistryEntry<Biome>,List<Float>>> biomeSource, ArrayList<String> chunkGeneratorSettingsCode, List<String> blockList){
        createNewPlantWithOrbit(server,worldKey,biomeSource,chunkGeneratorSettingsCode,blockList,
                false, (short) -270,4.8f,32,10);
    }
    public static void createNewPlantWithOrbit(MinecraftServer server, RegistryKey<World> worldKey, List<Pair<RegistryEntry<Biome>,List<Float>>> biomeSource, ArrayList<String> chunkGeneratorSettingsCode, List<String> blockList, PlanetCreatingData planetCreatingData){
        planetCreatingData.fillNullValues(false, (short) -270,4.8f,32,10);
        createNewPlantWithOrbit(server,worldKey,biomeSource,chunkGeneratorSettingsCode,blockList,
                planetCreatingData.oxygen, planetCreatingData.temperature,planetCreatingData.gravity,planetCreatingData.solarPower,planetCreatingData.tier);
    }
    public static void createNewPlantWithOrbit(MinecraftServer server, RegistryKey<World> worldKey, List<Pair<RegistryEntry<Biome>,List<Float>>> biomeSource, ArrayList<String> chunkGeneratorSettingsCode, List<String> blockList,
                                               boolean oxygen,short temperature,float gravity,int solarPower,int tier){
        RegistryKey<World> orbitKey = createWorldKey(worldKey);
        createNewDimension(server,worldKey,biomeSource,chunkGeneratorSettingsCode,blockList);
        Planet planet = new Planet(worldKey, oxygen, temperature, gravity, solarPower, new Identifier(NAMESPACE,"system"), Optional.of(orbitKey), tier, List.of());
        addPlanet(planet,worldKey);

        createNewOrbitDimension(server,orbitKey);
        Planet orbitPlanet = new Planet(orbitKey, false, (short) -270, 0.0f, solarPower, new Identifier(NAMESPACE,"system"), Optional.empty(), tier, List.of());
        addPlanet(orbitPlanet,orbitKey);
    }


    public static void createNewDimension(MinecraftServer server, RegistryKey<World> worldKey, List<Pair<RegistryEntry<Biome>,List<Float>>> biomeSource, ArrayList<String> chunkGeneratorSettingsCode, List<String> blockList) {
        if (server instanceof MinecraftServerAccessor serverAccessor) {
            long seed = server.getOverworld().getSeed();

            chunkGeneratorSettingsCode = new ArrayList<>(chunkGeneratorSettingsCode.stream().map(string -> {
                if (string.equals("yClampedGradient")) {
                    string = "y_clamped_gradient";
                }
                return string;
            }).toList());

            ChunkGeneratorSettings chunkGeneratorSettings = WorldGenerator.getGeneratorSettings(server,chunkGeneratorSettingsCode,blockList,BiomesSupplier.toBiomeList(biomeSource));


            // 创建维度选项 (包括维度类型、生成器等)
//            System.out.println("biome "+biomeSource.stream().map(m-> m.getLeft() + " " + m.getRight()).toList());

            Registry<DimensionType> dimensionTypeRegistry = server.getRegistryManager().get(RegistryKeys.DIMENSION_TYPE);
            DimensionType planetsType = dimensionTypeRegistry.get(new Identifier("asplor:outer_planets"));


            DimensionOptions dimensionOptions = new DimensionOptions(
                    dimensionTypeRegistry.getEntry(planetsType),
                    new NoiseChunkGenerator(
                            getBiomeSource(biomeSource),
                            new RegistryEntry.Direct<>(chunkGeneratorSettings)
                    )
            );




            ServerWorld newWorld = createNewDimension(serverAccessor,worldKey,dimensionOptions,seed);

            serverAccessor.getWorlds().put(worldKey,newWorld);
            WORLDS.put(worldKey,new Pair<>(newWorld,new Pair<>(new Pair<>(chunkGeneratorSettingsCode,blockList),biomeSource)));
            System.out.println("put "+worldKey+" "+newWorld + " f  "+chunkGeneratorSettingsCode);


        }
    }

    public static void createNewOrbitDimension(MinecraftServer server,RegistryKey<World> worldKey){
        if (server instanceof MinecraftServerAccessor serverAccessor) {
            DynamicRegistryManager registryManager = server.getRegistryManager();
            Registry<Biome> biomeRegistry = registryManager.get(RegistryKeys.BIOME);
            Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry = registryManager.get(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
            Registry<DimensionType> dimensionTypeRegistry = registryManager.get(RegistryKeys.DIMENSION_TYPE);
            Biome orbitBiome = biomeRegistry.get(new Identifier("ad_astra:orbit"));
            ChunkGeneratorSettings orbitSettings = chunkGeneratorSettingsRegistry.get(new Identifier("ad_astra:orbit"));
            DimensionType orbitType = dimensionTypeRegistry.get(new Identifier("asplor:outer_planets_orbit"));
            DimensionOptions orbitOptions = new DimensionOptions(
                    dimensionTypeRegistry.getEntry(orbitType),
                    new NoiseChunkGenerator(
                            BiomesSupplier.getBiomeSource(
                                    List.of(new Pair<>(biomeRegistry.getEntry(orbitBiome), List.of(0f, 0f, 0f, 0f, 0f, 0f, 0f)))
                            ),
                            chunkGeneratorSettingsRegistry.getEntry(orbitSettings)
                    )
            );

            long seed = server.getOverworld().getSeed();

            ServerWorld newWorld = createNewDimension(serverAccessor, worldKey, orbitOptions, seed);

            serverAccessor.getWorlds().put(worldKey, newWorld);




            System.out.println("put orbit " + worldKey + " " + newWorld);
        }
    }

    private static void addPlanet(Planet planet,RegistryKey<World> worldKey){
        Map<RegistryKey<World>, Planet> planets = AdAstraData.planets();
        planets.put(worldKey, planet);
        PlanetData.planetEnvironments().put(worldKey,new PlanetData.PlanetEnvironment(worldKey,false));
    }

    public static ServerWorld createNewDimension(MinecraftServerAccessor serverAccessor,RegistryKey<World> worldKey,DimensionOptions dimensionOptions,long seed){
        // 创建维度属性
        ServerWorldProperties properties = new UnmodifiableLevelProperties(serverAccessor.getSaveProperties(), serverAccessor.getSaveProperties().getMainWorldProperties());

        // 获取世界会话
        LevelStorage.Session session =  serverAccessor.getSession();
        Executor executor = serverAccessor.getWorkerExecutor();

        // 创建世界生成进度监听器
        WorldGenerationProgressListener progressListener = serverAccessor.getWorldGenerationProgressListenerFactory().create(11);

        // 创建新维度
        ServerWorld newWorld = new ServerWorld(
                (MinecraftServer) serverAccessor,
                executor,
                session,
                properties,
                worldKey,
                dimensionOptions,
                progressListener,
                false, // debugWorld
                seed,
                ImmutableList.of(),
                false, // shouldTickTime
                null// randomSequencesState
        );
        return newWorld;
    }

    private static Path getDataPath(MinecraftServer server){
        return server.getSavePath(new WorldSavePath("data"));
    }


    public static void saveWorlds(MinecraftServer server) {
        File file = getDataPath(server).resolve(DATA_FILE_NAME).toFile();


        NbtCompound allWorldData = new NbtCompound();

        NbtList worldList = new NbtList();
        for (Map.Entry<RegistryKey<World>, Pair<ServerWorld,Pair<Pair<List<String>,List<String>>,List<Pair<RegistryEntry<Biome>,List<Float>>>>>> worldEntry : WORLDS.entrySet()) {
            NbtCompound worldData = new NbtCompound();
            RegistryKey<World> worldKey = worldEntry.getKey();
            worldData.putString("id", worldKey.getValue().toString());

            List<String> chunkGeneratingSettingsCode = worldEntry.getValue().getRight().getLeft().getLeft();
            worldData.put("chunk_generating_settings",NbtUtil.writeStringListToNbt(chunkGeneratingSettingsCode));

            List<String> blockList = worldEntry.getValue().getRight().getLeft().getRight();
            worldData.put("block_list",NbtUtil.writeStringListToNbt(blockList));


            NbtList biomeList = new NbtList();
            List<Pair<RegistryEntry<Biome>,List<Float>>> biomes = worldEntry.getValue().getRight().getRight();
            for (Pair<RegistryEntry<Biome>,List<Float>> pair : biomes){
                RegistryEntry<Biome> biomeRegistryEntry = pair.getLeft();
                NbtCompound biomeData = new NbtCompound();
                biomeData.putString("id",biomeRegistryEntry.getKey().orElseThrow(
                        ()-> new RuntimeException("no biome key : " + biomeRegistryEntry)
                ).getValue().toString());
                biomeData.put("noise",NbtUtil.writeFloatListToNbt(pair.getRight()));
                biomeList.add(biomeData);
            }

            worldData.put("biomes",biomeList);

            worldList.add(worldData);

            Planet planet = AdAstraData.getPlanet(worldKey);
            if (planet != null){
                NbtCompound planetData = new NbtCompound();
                planetData.putBoolean("oxygen",planet.oxygen());
                planetData.putShort("temperature",planet.temperature());
                planetData.putFloat("gravity",planet.gravity());
                planetData.putInt("solar_power",planet.solarPower());
                planetData.putInt("tier",planet.tier());

                worldData.put("planet",planetData);
            }
        }

        allWorldData.put("data",worldList);
        WORLDS.clear();

        NbtList renderers = WorldRenderingData.saveRenderers();
        allWorldData.put("renderers",renderers);


        try {
            NbtIo.write(allWorldData,file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadWorlds(MinecraftServer server) {
        File file = getDataPath(server).resolve(DATA_FILE_NAME).toFile();

        if (!file.exists()) {
            return;
        }



        try {
            NbtCompound nbt = NbtIo.read(file);
            NbtList worldList = nbt.getList("data", NbtElement.COMPOUND_TYPE);


            for (NbtElement e : worldList) {
                NbtCompound worldData = (NbtCompound) e;
                RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(worldData.getString("id")));

                NbtList biomeList = worldData.getList("biomes", NbtElement.COMPOUND_TYPE);
                ArrayList<Pair<RegistryEntry<Biome>,List<Float>>> biomes = new ArrayList<>();
                Registry<Biome> biomeRegistry = server.getRegistryManager().get(RegistryKeys.BIOME);
                for (NbtElement be : biomeList) {
                    NbtCompound biomeData = (NbtCompound) be;
                    try {
                        RegistryEntry<Biome> biomeRegistryEntry = biomeRegistry.entryOf(RegistryKey.of(RegistryKeys.BIOME, new Identifier(biomeData.getString("id"))));
                        List<Float> biomeSetting = NbtUtil.readFloatListFromNbt(biomeData.getList("noise", NbtElement.FLOAT_TYPE));
                        biomes.add(new Pair<>(biomeRegistryEntry, biomeSetting));
                    }catch (IllegalStateException illegalStateException){
                        Asplor.LOGGER.error("no biome get : "+biomeData);
                    }
                }

                ArrayList<String> chunkGeneratingSettingsCode = new ArrayList<>(NbtUtil.readStringListFromNbt(worldData.getList("chunk_generating_settings",NbtElement.STRING_TYPE)));
                ArrayList<String> blockList = new ArrayList<>(NbtUtil.readStringListFromNbt(worldData.getList("block_list",NbtElement.STRING_TYPE)));

                if (worldData.contains("planet",NbtElement.COMPOUND_TYPE)){
                    NbtCompound planetData = worldData.getCompound("planet");
                    boolean oxygen = planetData.getBoolean("oxygen");
                    short temperature = planetData.getShort("temperature");
                    float gravity = planetData.getFloat("gravity");
                    int solarPower = planetData.getInt("solar_power");
                    int tier = planetData.getInt("tier");
                    createNewPlantWithOrbit(server,worldKey,biomes,chunkGeneratingSettingsCode,blockList,
                            oxygen,temperature,gravity,solarPower,tier);
                }else {
                    createNewPlantWithOrbit(server,worldKey,biomes,chunkGeneratingSettingsCode,blockList);
                }
            }

            if (nbt.contains("renderers",NbtElement.LIST_TYPE)){
                WorldRenderingData.loadRenderers(nbt.getList("renderers",NbtElement.COMPOUND_TYPE));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
