package com.ffsupver.asplor.item;

import com.ffsupver.asplor.Asplor;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.ffsupver.asplor.AllBlocks.*;
import static com.ffsupver.asplor.item.ModItems.*;

public class ModItemGroups {
    public static void registerModItemGroups(){
        Registry.register(Registries.ITEM_GROUP,
                new Identifier(Asplor.MOD_ID,"asplor_ingredients"),
                FabricItemGroup.builder().displayName(Text.translatable("itemGroup.asplor_ingredients"))
                        .icon(()->new ItemStack(ALLOY_INGOT))
                            .entries((displayContext, entries) -> {
                                entries.add(GUIDE_BOOK);
                                entries.add(EMPTY_LARGE_MAP);
                                entries.add(GOLD_ORCHID_SEED);
                                entries.add(GOLD_ORCHID_STAMEN);
                                entries.add(GOLD_ORCHID_STAMEN_DUST);
                                entries.add(MOON_STONE_BILLET);
                                entries.add(RAW_ASTRA_COPPER);
                                entries.add(RAW_ASTRA_IRON);
                                entries.add(RAW_ASTRA_SILVER);
                                entries.add(RAW_ETRUIM);
                                entries.add(ASTRA_COPPER_INGOT);
                                entries.add(ASTRA_IRON_INGOT);
                                entries.add(ASTRA_SILVER_INGOT);
                                entries.add(ALLOY_INGOT);
                                entries.add(ALLOY_NUGGET);
                                entries.add(ALLOY_BLOCK);
                                entries.add(CHARGED_ALLOY_INGOT);
                                entries.add(CHARGED_ALLOY_BLOCK);
                                entries.add(ZINC_SHEET);
                                entries.add(COPPER_WIRE);
                                entries.add(RESONANT_CRYSTAL);
                                entries.add(TIER_0_ROCKET_SHELL);
                                entries.add(TIER_1_ROCKET_SHELL);
                                entries.add(TIER_2_ROCKET_SHELL);
                                entries.add(TIER_3_ROCKET_SHELL);
                                entries.add(TIER_4_ROCKET_SHELL);
                                entries.add(NAVIGATION_CHIP);
                                entries.add(SALT);
                                entries.add(CARBON_POWDER);
                                entries.add(KELP_PUREE);
                                entries.add(KELP_BREAD);
                                entries.add(DIAMOND_SHARD);
                                entries.add(TRIDENT_SHARD);
                                entries.add(METEORITE_FRAGMENT);
                                entries.add(EMPTY_TANK);
                                entries.add(CONCENTRATED_OIL_TANK);
                                entries.add(SINGLE_ITEM_STORAGE_CELL_HOUSING);
                                entries.add(ZINC_COPPER_BATTERY);
                                entries.add(SPACE_CORE);
                                entries.add(CONTROLLING_CHIP);
                                entries.add(CIRCUIT_BOARD);
                                entries.add(TIER_2_ROCKET_CONTROLLING_CHIP);
                                entries.add(ADVANCE_CIRCUIT_BOARD);
                                entries.add(REFINED_OIL_BUCKET);
                                entries.add(CONCENTRATED_OIL_BUCKET);
                                entries.add(HEAVY_OIL_BUCKET);
                                entries.add(LIGHT_OIL_BUCKET);
                                entries.add(GLUE_BUCKET);
                                entries.add(SALT_WATER_BUCKET);
                                entries.add(CHLORINE_BUCKET);
                                entries.add(HYDROCHLORIC_ACID_BUCKET);
                                entries.add(CHEESE_BUCKET);
                                entries.add(NETHER_OIL_BUCKET);
                                entries.add(ALLOY_LAVA_BUCKET);
                                entries.add(MOLTEN_IRON_BUCKET);
                                entries.add(MOLTEN_GOLD_BUCKET);
                                entries.add(MOLTEN_COPPER_BUCKET);
                                entries.add(MOLTEN_ZINC_BUCKET);
                                entries.add(MOLTEN_BRASS_BUCKET);
                                entries.add(MOLTEN_ALLOY_BUCKET);
                                entries.add(MOLTEN_DESH_BUCKET);
                                entries.add(IMPURE_MOLTEN_DESH_BUCKET);
                                entries.add(MOLTEN_OSTRUM_BUCKET);
                                entries.add(MOLTEN_CALORITE_BUCKET);
                                entries.add(MOLTEN_QUARTZ_BUCKET);
                                entries.add(ANDESITE_MACHINE);
                                entries.add(AllOY_CASING);
                                entries.add(AllOY_MACHINE);
                                entries.add(PRIMARY_MECHANISM);
                                entries.add(IRON_MECHANISM);
                                entries.add(ALLOY_MECHANISM);
                                entries.add(MECHANICAL_PRESS_HEAD);
                                entries.add(DRILL);
                                entries.add(SAW);
                                entries.add(DIVIDER_TOOL);
                                entries.add(WINDMILL_HEAD);
                                entries.add(MAGNETIC_ROTOR);
                                entries.add(FROZEN_CORE);
                                entries.add(UNSTABLE_ROCK);
                                entries.add(SUSPICIOUS_MARS_SAND);
                                entries.add(FARM_MOON_SAND);
                                entries.add(FLINT_BLOCK);
                                entries.add(ALLOY_HELMET);
                                entries.add(ALLOY_CHESTPLATE);
                                entries.add(ALLOY_LEGGINGS);
                                entries.add(ALLOY_BOOTS);
                                entries.add(ALLOY_SWORD);
                                entries.add(ALLOY_SHOVEL);
                                entries.add(ALLOY_PICKAXE);
                                entries.add(ALLOY_AXE);
                                entries.add(ALLOY_HOE);
                                entries.add(STAFF_OF_SHOOTING_METEORITE);
                                entries.add(CRUDE_PRINTED_CALCULATION_PROCESSOR);
                                entries.add(CRUDE_PRINTED_ENGINEERING_PROCESSOR);
                                entries.add(CRUDE_PRINTED_LOGIC_PROCESSOR);
                                entries.add(ASTRA_DIABASE_DUST);
                                entries.add(ASTRA_DIABASE_DIRT);
                                entries.add(ASTRA_DIABASE_GRASS_BLOCK);
                                entries.add(ASTRA_DIABASE_GRASS);
                                entries.add(ASTRA_DIABASE_SAPLING);
                                entries.add(ASTRA_DIABASE_LOG);
                                entries.add(ASTRA_DIABASE_WOOD);
                                entries.add(ASTRA_DIABASE_LEAVES);
                                entries.add(ASTRA_DIABASE_STONE);
                                entries.add(ASTRA_DIABASE_COBBLESTONE);
                                entries.add(ASTRA_DIABASE_STONE_BRICKS);
                                entries.add(ASTRA_DIABASE_STONE_BRICK_STAIRS);
                                entries.add(ASTRA_DIABASE_STONE_BRICK_SLAB);
                                entries.add(ASTRA_DIABASE_COPPER_ORE);
                                entries.add(ASTRA_IRON_ORE);
                                entries.add(ASTRA_SILVER_ORE);
                                entries.add(METEORITE);
                                entries.add(GLACIO_ETRUIM_ORE);
                                entries.add(ZOMBIFIED_COSMONAUT_SPAWN_EGG);
                                entries.add(ZOMBIFIED_COSMONAUT_HEAD);
                                entries.add(GLACIO_VILLAGER_SHAMAN_SPAWN_EGG);
                            }).build()
        );
        Registry.register(Registries.ITEM_GROUP,
                new Identifier(Asplor.MOD_ID,"asplor_machines"),
                FabricItemGroup.builder().displayName(Text.translatable("itemGroup.asplor_machines"))
                        .icon(()->new ItemStack(GENERATOR))
                        .entries((displayContext, entries) -> {
                            entries.add(ALLOY_CHEST);
                            entries.add(CHEST_SORTER);
                            entries.add(PACKER);
                            entries.add(UNPACKING_TABLE);
                            entries.add(LIQUID_BLAZE_BURNER);
                            entries.add(DIVIDER);
                            entries.add(ALLOY_MECHANICAL_PRESS);
                            entries.add(GENERATOR);
                            entries.add(MOTOR);
                            entries.add(WINDMILL_BEARING);
                            entries.add(LIGHTNING_ABSORBER);
                            entries.add(BATTERY);
                            entries.add(ASSEMBLER);
                            entries.add(ENERGY_OUTPUT);
                            entries.add(TIME_INJECTOR);
                            entries.add(SPACE_TELEPORTER);
                            entries.add(SPACE_TELEPORTER_ANCHOR);
                            entries.add(THE_NETHER_RETURNER);
                            entries.add(LOCATOR);
                            entries.add(MECHANICAL_PUMP);
                            entries.add(MELTING_FURNACE);
                            entries.add(LARGE_MELTING_FURNACE_CONTROLLER);
                            entries.add(LARGE_MELTING_FURNACE_FLUID_PORT);
                            entries.add(LARGE_MELTING_FURNACE_ITEM_PORT);
                            entries.add(ELECTROLYZER);
                            entries.add(PLANET_NAMING);
                            entries.add(PLANET_LOCATOR);
                            entries.add(TIER_0_ROCKET);
                            entries.add(CARGO_ROCKET);
                            entries.add(ADVANCE_ROCKET);
                            entries.add(ROCKET_CARGO_LOADER);
                            entries.add(ROCKET_FUEL_LOADER);
                            entries.add(SIMPLE_SPACE_STATION);
                            entries.add(REFINERY_CONTROLLER);
                            entries.add(REFINERY_INPUT);
                            entries.add(REFINERY_OUTPUT);
                            entries.add(REFINERY_BRICKS);
                            entries.add(REFINERY_GLASS);
                            entries.add(SMART_MECHANICAL_ARM);
                            entries.add(BELT_SMART_PROCESSOR);
                            entries.add(TOOL_GEAR);
                            entries.add(ALLOY_DEPOT);
                            entries.add(DRILL_TOOL);
                            entries.add(LASER_TOOL);
                            entries.add(EMPTY_DROPPER);
                            entries.add(MOLTEN_GOLD_DROPPER);
                            entries.add(MOLTEN_ALLOY_DROPPER);
                            entries.add(MOLTEN_BRASS_DROPPER);
                            entries.add(MOLTEN_DESH_DROPPER);
                            entries.add(MOLTEN_OSTRUEM_DROPPER);
                            entries.add(MOLTEN_CALORITE_DROPPER);
                            entries.add(GLUE_DROPPER);
                            entries.add(SINGLE_ITEM_STORAGE_CELL_4K);
                            entries.add(SINGLE_ITEM_STORAGE_CELL_16K);
                            entries.add(SINGLE_ITEM_STORAGE_CELL_64K);
                            entries.add(SINGLE_ITEM_STORAGE_CELL_256K);
                            entries.add(SINGLE_ITEM_STORAGE_CELL_1M);
                            entries.add(CHUNK_LOADER);
                            entries.add(INFUSION_CLOCK);
                            entries.add(IRON_AIRLOCK_SWITCH);
                            entries.add(IRON_PLATING_AIRLOCK_SWITCH);
                            entries.add(POLISHED_CUT_CALCITE_AIRLOCK_SWITCH);
                            entries.add(ATMOSPHERIC_REGULATOR_IRON);
                            entries.add(OXYGEN_PIPE_IRON);
                            entries.add(ATMOSPHERIC_REGULATOR_IRON_PLATING);
                            entries.add(OXYGEN_PIPE_IRON_PLATING);
                            entries.add(LASER_DRILL);
                            entries.add(LASER_DRILL_LEN);
                            entries.add(LASER_DRILL_SHELL);
                            entries.add(LASER_DRILL_GLASS);
                            entries.add(LASER_DRILL_ITEM_OUTPUT);
                            entries.add(LASER_DRILL_BATTERY);
                        }).build());

    }



}
