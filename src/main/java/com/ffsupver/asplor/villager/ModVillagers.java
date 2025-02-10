package com.ffsupver.asplor.villager;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.sound.ModSounds;
import com.google.common.collect.ImmutableSet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import static com.ffsupver.asplor.ModPointOfInterestTypes.ASSEMBLER_POI_KEY;

public class ModVillagers {
    public static final VillagerProfession ASSEMBLER = registerVillagerProfession("assembler",ASSEMBLER_POI_KEY);
    private static VillagerProfession registerVillagerProfession(String name,RegistryKey<PointOfInterestType> type){
        return Registry.register(Registries.VILLAGER_PROFESSION,new Identifier(Asplor.MOD_ID,name),
                new VillagerProfession(name,entry->entry.matchesKey(type),entry->entry.matchesKey(type),
                    ImmutableSet.of(),ImmutableSet.of(), ModSounds.ASSEMBLER_WORK));
    }


    public static void registerVillagers(){
    }
}
