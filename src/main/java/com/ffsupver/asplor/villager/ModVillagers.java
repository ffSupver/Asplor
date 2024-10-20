package com.ffsupver.asplor.villager;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.sound.ModSounds;
import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class ModVillagers {
    public static final RegistryKey<PointOfInterestType> ASSEMBLER_POI_KEY = point("assembler_poi");
    public static final PointOfInterestType ASSEMBLER_POT = registerPointInterestType("assembler_poi", AllBlocks.ASSEMBLER);
    public static final VillagerProfession ASSEMBLER = registerVillagerProfession("assembler",ASSEMBLER_POI_KEY);
    private static VillagerProfession registerVillagerProfession(String name,RegistryKey<PointOfInterestType> type){
        return Registry.register(Registries.VILLAGER_PROFESSION,new Identifier(Asplor.MOD_ID,name),
                new VillagerProfession(name,entry->entry.matchesKey(type),entry->entry.matchesKey(type),
                    ImmutableSet.of(),ImmutableSet.of(), ModSounds.ASSEMBLER_WORK));
    }
    private static PointOfInterestType registerPointInterestType(String name, Block block){
        return PointOfInterestHelper.register(new Identifier(Asplor.MOD_ID,name),1,1,block);
    }
    private static RegistryKey<PointOfInterestType> point(String name){
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE,new Identifier(Asplor.MOD_ID,name));
    }
    public static void registerVillagers(){

    }
}
