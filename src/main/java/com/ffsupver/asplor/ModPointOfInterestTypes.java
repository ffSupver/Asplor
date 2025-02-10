package com.ffsupver.asplor;

import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

public class ModPointOfInterestTypes {
    public static final RegistryKey<PointOfInterestType> ASSEMBLER_POI_KEY = point("assembler_poi");
    public static final RegistryKey<PointOfInterestType> LIGHTNING_ABSORBER_KEY = point("lightning_absorber");
    private static RegistryKey<PointOfInterestType> point(String name){
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE,new Identifier(Asplor.MOD_ID,name));
    }

    public static void register(){
        registerPointInterestType("assembler_poi", AllBlocks.ASSEMBLER,1,1);
        registerPointInterestType("lightning_absorber",AllBlocks.LIGHTNING_ABSORBER.get(),0,1);

    }
    private static PointOfInterestType registerPointInterestType(String name, Block block,int ticketCount,int searchDistance){
        return registerPointInterestType(new Identifier(Asplor.MOD_ID,name),block,ticketCount,searchDistance);
    }
    private static PointOfInterestType registerPointInterestType(Identifier identifier, Block block,int ticketCount,int searchDistance){
        return PointOfInterestHelper.register(identifier,ticketCount,searchDistance,block);
    }
}
