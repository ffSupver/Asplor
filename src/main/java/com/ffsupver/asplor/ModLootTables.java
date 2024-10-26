package com.ffsupver.asplor;

import com.ffsupver.asplor.item.ModItems;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class ModLootTables {
    public static void register(){
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, table, setter) -> {
            addMysteryPaper(id,table);
        });
    }
    private static void addMysteryPaper(Identifier id, FabricLootSupplierBuilder table){
        ArrayList<Identifier> lootTablesToAddIron = new ArrayList<>();
        lootTablesToAddIron.add(LootTables.VILLAGE_TOOLSMITH_CHEST);
        lootTablesToAddIron.add(LootTables.VILLAGE_WEAPONSMITH_CHEST);
        lootTablesToAddIron.add(LootTables.VILLAGE_ARMORER_CHEST);
        if (lootTablesToAddIron.contains(id)) {
            NbtCompound chapterNbt = new NbtCompound();
            chapterNbt.putString("chapter","earth/iron");
            LootPool.Builder poolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,3))
                    .with(ItemEntry.builder(ModItems.MYSTERIOUS_PAPER)
                            .apply(SetNbtLootFunction.builder(chapterNbt)));

            table.pool(poolBuilder);
        }
        if (LootTables.RUINED_PORTAL_CHEST.equals(id)) {
            NbtCompound netherPortalNbt = new NbtCompound();
            netherPortalNbt.putString("chapter","earth/nether_portal");
            NbtCompound locatorNbt = new NbtCompound();
            locatorNbt.putString("chapter","earth/locator");


            LootPool.Builder netherPortalpoolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,3))
                    .with(ItemEntry.builder(ModItems.MYSTERIOUS_PAPER)
                            .apply(SetNbtLootFunction.builder(netherPortalNbt)));
            LootPool.Builder locatorPoolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,1))
                    .with(ItemEntry.builder(ModItems.MYSTERIOUS_PAPER)
                            .apply(SetNbtLootFunction.builder(locatorNbt)));


            table.pool(netherPortalpoolBuilder);
            table.pool(locatorPoolBuilder);
        }

        ArrayList<Identifier> lootTablesToAddGogglesAndChest = new ArrayList<>();
        lootTablesToAddGogglesAndChest.add(LootTables.VILLAGE_PLAINS_CHEST);
        lootTablesToAddGogglesAndChest.add(LootTables.VILLAGE_DESERT_HOUSE_CHEST);
        lootTablesToAddGogglesAndChest.add(LootTables.VILLAGE_SAVANNA_HOUSE_CHEST);
        lootTablesToAddGogglesAndChest.add(LootTables.VILLAGE_TAIGA_HOUSE_CHEST);
        lootTablesToAddGogglesAndChest.add(LootTables.VILLAGE_SNOWY_HOUSE_CHEST);
        if (lootTablesToAddGogglesAndChest.contains(id)) {
            NbtCompound netherPortalNbt = new NbtCompound();
            netherPortalNbt.putString("chapter","earth/add_chest");
            NbtCompound locatorNbt = new NbtCompound();
            locatorNbt.putString("chapter","earth/add_goggles");


            LootPool.Builder netherPortalpoolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,1))
                    .with(ItemEntry.builder(ModItems.MYSTERIOUS_PAPER)
                            .apply(SetNbtLootFunction.builder(netherPortalNbt)));
            LootPool.Builder locatorPoolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,1))
                    .with(ItemEntry.builder(ModItems.MYSTERIOUS_PAPER)
                            .apply(SetNbtLootFunction.builder(locatorNbt)));


            table.pool(netherPortalpoolBuilder);
            table.pool(locatorPoolBuilder);
        }
    }
}
