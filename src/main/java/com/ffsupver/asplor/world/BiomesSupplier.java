package com.ffsupver.asplor.world;

import com.ffsupver.asplor.Asplor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

import java.util.ArrayList;
import java.util.List;

public class BiomesSupplier {
    public static final BiomesData OUTER_SPACE_PLAINS = createBiomesData("outer_space_plains",getBaseMaterialRule(Blocks.SAND.getDefaultState(),Blocks.IRON_BLOCK.getDefaultState()));
    public static final BiomesData OUTER_SPACE_HILLS = createBiomesData("outer_space_hills",getBaseMaterialRule(Blocks.DIRT.getDefaultState(),Blocks.IRON_BLOCK.getDefaultState()));
    public static final List<BiomesData> BIOMES_LIST = List.of(OUTER_SPACE_PLAINS,OUTER_SPACE_HILLS);
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

    public static BiomesData createBiomesData(String id, MaterialRules.MaterialRule materialRule){
        return new BiomesData(RegistryKey.of(RegistryKeys.BIOME,new Identifier(Asplor.MOD_ID,id)),materialRule);
    }

    public record BiomesData(RegistryKey<Biome> registryKey, MaterialRules.MaterialRule materialRule) {
    }

    public static MaterialRules.MaterialRule generateMaterialRules(List<BiomesData> biomesDataList) {
        // 将 BiomesData 转换为 MaterialRules.condition(...)
        MaterialRules.MaterialRule[] rules = biomesDataList.stream()
                .map(data -> MaterialRules.condition(
                        MaterialRules.biome(data.registryKey()), // 根据 Biome 的 registryKey 创建条件
                        data.materialRule()                      // 对应的 materialRule
                ))
                .toArray(MaterialRules.MaterialRule[]::new);   // 转换为数组

        // 将所有条件组合到 MaterialRules.sequence 中
        return MaterialRules.sequence(rules);
    }
    public static MaterialRules.MaterialRule getBaseMaterialRule(BlockState surfaceBlockState, BlockState baseBlockState){
        MaterialRules.MaterialCondition isSurface = MaterialRules.stoneDepth(0,true,0, VerticalSurfaceType.FLOOR);
        return MaterialRules.condition(
                MaterialRules.aboveYWithStoneDepth(YOffset.aboveBottom(128),-3),
                MaterialRules.sequence(
                        MaterialRules.condition(
                                isSurface,
                                MaterialRules.block(surfaceBlockState)
                        ),
                        MaterialRules.condition(
                                MaterialRules.not(isSurface),
                                MaterialRules.block(baseBlockState)
                        )
                )
        );
    }
}
