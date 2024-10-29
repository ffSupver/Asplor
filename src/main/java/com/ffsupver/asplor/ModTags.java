package com.ffsupver.asplor;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEED_ENERGY = createTag("need_energy");
        public static final TagKey<Block> LET_STORAGE_ENTITY_BE_BLOCK = createTag("let_storage_entity_to_be_block");
        public static final TagKey<Block> REFINERY_BLOCK = createTag("refinery_block");
        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK,new Identifier(Asplor.MOD_ID,name));
        }
    }
    public static class Fluids{
        public static final TagKey<Fluid> REFINED_OIL = createTag("refined_oil");
        public static final TagKey<Fluid> GLUE = createTag("glue");
        public static final TagKey<Fluid> MOLTEN_METAL = createTag("molten_metal");
        public static final TagKey<Fluid> TIER_0_ROCKET_FUEL = createTag("tier_0_rocket_fuel");

        private static TagKey<Fluid> createTag(String name) {
            return TagKey.of(RegistryKeys.FLUID,new Identifier(Asplor.MOD_ID,name));
        }
    }
    public static class EntityTypes{
        public static final TagKey<EntityType<?>> CAN_TELEPORT = createTag("can_teleport");
        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE,new Identifier(Asplor.MOD_ID,name));
        }
    }

    public static class Biomes{
        public static TagKey<Biome> MOON = tag("moon");
        public static TagKey<Biome> NO_WIND = tag("no_wind");
        public static TagKey<Biome> HAS_LAUNCH_CENTER = tag("has_launch_center");

        private static TagKey<Biome> tag(String name) {
            return TagKey.of(RegistryKeys.BIOME,new Identifier(Asplor.MOD_ID,name));
        }
    }
}
