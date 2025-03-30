package com.ffsupver.asplor;

import com.ffsupver.asplor.item.ModItems;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.List;

import static net.minecraft.loot.LootTables.*;

public class ModLootTables {
    private static final List<Identifier> LOOT_TABLES_TO_ADD_IRON = List.of(
            VILLAGE_WEAPONSMITH_CHEST,VILLAGE_TOOLSMITH_CHEST,VILLAGE_ARMORER_CHEST
    );
    private static final List<Identifier> LOOT_TABLES_TO_ADD_LIGHTNING_ABSORBER = List.of(
            UNDERWATER_RUIN_BIG_CHEST,UNDERWATER_RUIN_SMALL_CHEST
    );

    private static final List<Identifier> LOOT_TABLES_TO_ADD_GOGGLES_AND_CHEST = List.of(
            VILLAGE_PLAINS_CHEST,VILLAGE_DESERT_HOUSE_CHEST,VILLAGE_SAVANNA_HOUSE_CHEST,
            VILLAGE_TAIGA_HOUSE_CHEST,VILLAGE_SNOWY_HOUSE_CHEST
    );
    public static void register(){
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, table, setter) -> {
            addMysteryPaper(id,table);
            addLocator(id,table);
            addTridentShard(id,table);
        });

    }

    private static void addTridentShard(Identifier id, FabricLootSupplierBuilder table){
        if (id.equals(getMinecraftEntity("drowned"))){
            LootPool.Builder builder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,1))
                    .with(ItemEntry.builder(ModItems.TRIDENT_SHARD)
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0,1)))
                            .apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0,1)))
                    );
            table.pool(builder);
        }
    }

    private static void addLocator(Identifier id, FabricLootSupplierBuilder table){
        if (NETHER_BRIDGE_CHEST.equals(id)) {
            LootPool.Builder netherBridgePoolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,3))
                    .with(ItemEntry.builder(ModItems.LOCATOR));
            table.pool(netherBridgePoolBuilder);
        }
    }
    private static void addMysteryPaper(Identifier id, FabricLootSupplierBuilder table){


        if (LOOT_TABLES_TO_ADD_IRON.contains(id)) {
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


        if (LOOT_TABLES_TO_ADD_GOGGLES_AND_CHEST.contains(id)) {
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

        if (LootTables.VILLAGE_CARTOGRAPHER_CHEST.equals(id)){
            NbtCompound largeMapNbt = new NbtCompound();
            largeMapNbt.putString("chapter","earth/large_map");

            LootPool.Builder cartographerBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,2))
                    .with(ItemEntry.builder(ModItems.MYSTERIOUS_PAPER)
                            .apply(SetNbtLootFunction.builder(largeMapNbt)));
            LootPool.Builder emptyLargeMapBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,1))
                    .with(ItemEntry.builder(ModItems.EMPTY_LARGE_MAP));

            table.pool(cartographerBuilder);
            table.pool(emptyLargeMapBuilder);
        }

        if (LOOT_TABLES_TO_ADD_LIGHTNING_ABSORBER.contains(id)) {
            NbtCompound chapterNbt = new NbtCompound();
            chapterNbt.putString("chapter","earth/lightning_absorber");
            LootPool.Builder poolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,1))
                    .with(ItemEntry.builder(ModItems.MYSTERIOUS_PAPER)
                            .apply(SetNbtLootFunction.builder(chapterNbt)));

            table.pool(poolBuilder);
        }

        if (NETHER_BRIDGE_CHEST.equals(id)) {
            NbtCompound chapterNbt = new NbtCompound();
            chapterNbt.putString("chapter","the_nether/the_nether_altar");
            LootPool.Builder poolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,1))
                    .with(ItemEntry.builder(ModItems.MYSTERIOUS_PAPER)
                            .apply(SetNbtLootFunction.builder(chapterNbt)));

            table.pool(poolBuilder);
        }
    }

    private static Identifier getMinecraftEntity(String id){return new Identifier("minecraft","entities/"+id);}
    private static Identifier getMinecraft(String id){return new Identifier("minecraft",id);}
}
