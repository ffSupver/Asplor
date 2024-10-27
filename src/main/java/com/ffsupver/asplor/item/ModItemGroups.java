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
                                entries.add(ALLOY_INGOT);
                                entries.add(ALLOY_BLOCK);
                                entries.add(CHARGED_ALLOY_INGOT);
                                entries.add(ZINC_SHEET);
                                entries.add(TIER_0_ROCKET_SHELL);
                                entries.add(SALT);
                                entries.add(CARBON_POWDER);
                                entries.add(ZINC_COPPER_BATTERY);
                                entries.add(REFINED_OIL_BUCKET);
                                entries.add(CONCENTRATED_OIL_BUCKET);
                                entries.add(GLUE_BUCKET);
                                entries.add(SALT_WATER_BUCKET);
                                entries.add(CHLORINE_BUCKET);
                                entries.add(HYDROCHLORIC_ACID_BUCKET);
                                entries.add(ALLOY_LAVA_BUCKET);
                                entries.add(MOLTEN_IRON_BUCKET);
                                entries.add(MOLTEN_GOLD_BUCKET);
                                entries.add(MOLTEN_COPPER_BUCKET);
                                entries.add(MOLTEN_ZINC_BUCKET);
                                entries.add(MOLTEN_BRASS_BUCKET);
                                entries.add(MOLTEN_ALLOY_BUCKET);
                                entries.add(MOLTEN_DESH_BUCKET);
                                entries.add(IMPURE_MOLTEN_DESH_BUCKET);
                                entries.add(ANDESITE_MACHINE);
                                entries.add(PRIMARY_MECHANISM);
                                entries.add(MECHANICAL_PRESS_HEAD);
                                entries.add(FROZEN_CORE);
                                entries.add(UNSTABLE_ROCK);
                                entries.add(SUSPICIOUS_MARS_SAND);
                                entries.add(FLINT_BLOCK);
                            }).build()
        );
        Registry.register(Registries.ITEM_GROUP,
                new Identifier(Asplor.MOD_ID,"asplor_machines"),
                FabricItemGroup.builder().displayName(Text.translatable("itemGroup.asplor_machines"))
                        .icon(()->new ItemStack(GENERATOR))
                        .entries((displayContext, entries) -> {
                            entries.add(ALLOY_CHEST);
                            entries.add(PACKER);
                            entries.add(UNPACKING_TABLE);
                            entries.add(LIQUID_BLAZE_BURNER);
                            entries.add(DIVIDER);
                            entries.add(ALLOY_MECHANICAL_PRESS);
                            entries.add(GENERATOR);
                            entries.add(MOTOR);
                            entries.add(WINDMILL_BEARING);
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
                            entries.add(ELECTROLYZER);
                            entries.add(TIER_0_ROCKET);
                        }).build());

    }



}
