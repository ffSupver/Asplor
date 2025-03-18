package com.ffsupver.asplor.world;

import com.ffsupver.asplor.Asplor;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class BiomesSupplier {
    public static final BiomesData OUTER_SPACE_PLAINS = createBiomesData("outer_space_plains");
    public static final BiomesData OUTER_SPACE_HILLS = createBiomesData("outer_space_hills");
    public static final List<BiomesData> BIOMES_LIST = List.of(OUTER_SPACE_PLAINS,OUTER_SPACE_HILLS);

    private static final MaterialRules.MaterialCondition IS_SURFACE = MaterialRules.stoneDepth(0,false,8, VerticalSurfaceType.FLOOR);
    private static final Function<BlockState,MaterialRules.MaterialRule> IS_SURFACE_TOP = blockState ->  MaterialRules.condition(
                MaterialRules.stoneDepth(0,false,0, VerticalSurfaceType.FLOOR),
                MaterialRules.condition(
                        MaterialRules.water(0,0),
                        MaterialRules.block(blockState)
                )
            );

    public static MultiNoiseBiomeSource getBiomeSource(List<Pair<RegistryEntry<Biome>, List<Float>>> biomes){
        ArrayList<com.mojang.datafixers.util.Pair<MultiNoiseUtil.NoiseHypercube,RegistryEntry<Biome>>> entries = new ArrayList<>();
        for (Pair<RegistryEntry<Biome>,List<Float>> biomeData : biomes){
            List<Float> noiseArguments = biomeData.getRight();
            com.mojang.datafixers.util.Pair<MultiNoiseUtil.NoiseHypercube,RegistryEntry<Biome>> pair = new com.mojang.datafixers.util.Pair<>(
                    MultiNoiseUtil.createNoiseHypercube(noiseArguments.get(0),noiseArguments.get(1),noiseArguments.get(2),noiseArguments.get(3),noiseArguments.get(4),noiseArguments.get(5),noiseArguments.get(6)),
                    biomeData.getLeft()
            );
            entries.add(pair);
        }
        return MultiNoiseBiomeSource.create(
                new MultiNoiseUtil.Entries<>(entries)
        );
    }
    public static List<Pair<RegistryEntry<Biome>,List<Float>>> getBiomeSourceSetting(List<RegistryEntry<Biome>> biomes, Random random){
        ArrayList<Pair<RegistryEntry<Biome>,List<Float>>> entries = new ArrayList<>();

        for (RegistryEntry<Biome> biomeRegistryEntry : biomes){
            List<Float> noiseArguments = new ArrayList<>(6);
            for (int i=0;i<7;i++){
                noiseArguments.add(random.nextInt(6) == 0 ? random.nextFloat() - 0.5f : 0);
            }

            Pair<RegistryEntry<Biome>,List<Float>> entry = new Pair<>(
                    biomeRegistryEntry,
                    noiseArguments
            );
            entries.add(entry);
        }
        return entries;
    }
    public static List<RegistryEntry<Biome>> toBiomeEntryList(MinecraftServer server, List<String> biomeIdList){
        Registry<Biome> biomeRegistry = server.getRegistryManager().get(RegistryKeys.BIOME);
        ArrayList<RegistryEntry<Biome>> result = new ArrayList<>();
        for (String id : biomeIdList){
            Biome biome = biomeRegistry.get(new Identifier(id));
            if (biome != null){
                RegistryEntry<Biome> entry = biomeRegistry.getEntry(biome);
                if (!result.contains(entry)){
                    result.add(entry);
                }
            }
        }
        return result;
    }
    public static List<RegistryEntry<Biome>> toBiomeEntryListKey(MinecraftServer server, List<BiomesData> biomeIdList){
        Registry<Biome> biomeRegistry = server.getRegistryManager().get(RegistryKeys.BIOME);
        ArrayList<RegistryEntry<Biome>> result = new ArrayList<>();
        for (BiomesData data : biomeIdList){
            Biome biome = biomeRegistry.get(data.registryKey().getValue());
            if (biome != null){
                RegistryEntry<Biome> entry = biomeRegistry.getEntry(biome);
                if (!result.contains(entry)){
                    result.add(entry);
                }
            }
        }
        return result;
    }

    public static BiomesData createBiomesData(String id){
        return BiomesData.of(RegistryKey.of(RegistryKeys.BIOME,new Identifier(Asplor.MOD_ID,id)));
    }

    public record BiomesData(RegistryKey<Biome> registryKey, Function<List<BlockState>,MaterialRules.MaterialRule> materialRule) {
        public static BiomesData of(RegistryKey<Biome> registryKey){
            return new BiomesData(registryKey,BiomesData::defaultRule);
        }
        private static MaterialRules.MaterialRule defaultRule(List<BlockState> blockStates) {
            return getBaseMaterialRule(blockStates.get(0),blockStates.get(1),blockStates.get(2));
        }
    }

    public static MaterialRules.@Nullable MaterialRule generateMaterialRules(List<BiomesData> biomesDataList, List<BlockState> blockStates, List<RegistryKey<Biome>> biomes, List<Pair<BlockState,Pair<BlockState,BlockState>>> biomeBlocks, Random random) {
        // 将 BiomesData 转换为 MaterialRules.condition(...)
        AtomicInteger index = new AtomicInteger(3);
        MaterialRules.MaterialRule[] rules = biomesDataList.stream()
                .filter(biomesData -> biomes.contains(biomesData.registryKey))
                .map(data -> {
                    MaterialRules.MaterialRule materialRule;
                    if (blockStates.size() > index.get() + 2){
                        materialRule = getBaseMaterialRule(blockStates.get(index.get()),blockStates.get(index.get() + 1),blockStates.get(index.get() + 2));
                    }else {
                        Pair<BlockState,Pair<BlockState,BlockState>> pair = biomeBlocks.get(random.nextInt(biomeBlocks.size()));
                        List<BlockState> bs = List.of(pair.getLeft(),pair.getRight().getLeft(),pair.getRight().getRight());
                        materialRule = data.materialRule().apply(bs);
                        blockStates.addAll(bs);
                    }
                    MaterialRules.MaterialRule rule = MaterialRules.condition(
                            MaterialRules.biome(data.registryKey()), // 根据 Biome 的 registryKey 创建条件
                            materialRule
                    );
                    index.addAndGet(3);
                    return rule;
                })
                .toArray(MaterialRules.MaterialRule[]::new);   // 转换为数组

        // 将所有条件组合到 MaterialRules.sequence 中
        return rules.length == 0 ? null : MaterialRules.sequence(rules);
    }
    public static MaterialRules.MaterialRule getBaseMaterialRule(BlockState surfaceBlockState, BlockState baseBlockState,BlockState surfaceTopBlockState){

        return MaterialRules.condition(
                MaterialRules.aboveYWithStoneDepth(YOffset.aboveBottom(128),-3),
                MaterialRules.sequence(
                        MaterialRules.condition(
                                IS_SURFACE,
                                MaterialRules.sequence(
                                        IS_SURFACE_TOP.apply(surfaceTopBlockState),
                                        MaterialRules.block(surfaceBlockState)
                                )
                        ),
                        MaterialRules.condition(
                                MaterialRules.not(IS_SURFACE),
                                MaterialRules.block(baseBlockState)
                        )
                )
        );
    }
    public static List<RegistryKey<Biome>> toBiomeList(List<Pair<RegistryEntry<Biome>,List<Float>>> biomeSource){
        return biomeSource.stream().map(data -> data.getLeft().getKey().orElseThrow(() -> new RuntimeException("Unknown biome : "+data.getLeft()))).toList();
    }
}
