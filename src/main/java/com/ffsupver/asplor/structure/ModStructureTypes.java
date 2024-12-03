package com.ffsupver.asplor.structure;

import com.ffsupver.asplor.Asplor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class ModStructureTypes {


    public static final StructureType<CrashedGenerator.Structure> CRASHED_STRUCTURE_TYPE = registerStructureType("crashed", CrashedGenerator.Structure.CODEC);



    public static void register() {
    }

    private static StructurePieceType registerPieceType(StructurePieceType.ManagerAware type, String id) {
        return Registry.register(Registries.STRUCTURE_PIECE, new Identifier(Asplor.MOD_ID,id), type);
    }
    private static <S extends Structure> StructureType<S> registerStructureType(String id, StructureType<S> type) {
        return (StructureType)Registry.register(Registries.STRUCTURE_TYPE, new Identifier(Asplor.MOD_ID,id), type);
    }

    private static RegistryKey<Structure> registerStructureKey(String name){
        return RegistryKey.of(RegistryKeys.STRUCTURE,new Identifier(Asplor.MOD_ID,name));
    }
    private static RegistryKey<StructurePool> registerStructurePoolKey(String name){
        return RegistryKey.of(RegistryKeys.TEMPLATE_POOL,new Identifier(Asplor.MOD_ID,name));
    }
}
