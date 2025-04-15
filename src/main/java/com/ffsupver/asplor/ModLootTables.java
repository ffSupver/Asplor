package com.ffsupver.asplor;

import com.ffsupver.asplor.item.ModItems;
import earth.terrarium.adastra.AdAstra;
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

    private static final Identifier MOON_VILLAGE_CHEST = new Identifier(AdAstra.MOD_ID,"chests/village/moon/house");
    public static void register(){
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, table, setter) -> {
            addMysteryPaper(id,table);
            addLocator(id,table);
            addTridentShard(id,table);
            addGoldOrchidSeed(id,table);
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
    private static void addGoldOrchidSeed(Identifier id, FabricLootSupplierBuilder table){
        if (MOON_VILLAGE_CHEST.equals(id)) {
            LootPool.Builder netherBridgePoolBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,2))
                    .with(ItemEntry.builder(ModItems.GOLD_ORCHID_SEED))
                    .with(ItemEntry.builder(ModItems.GOLD_ORCHID_STAMEN));
            table.pool(netherBridgePoolBuilder);
        }
    }
    private static void addMysteryPaper(Identifier id, FabricLootSupplierBuilder table){


        if (LOOT_TABLES_TO_ADD_IRON.contains(id)) {
            LootPool.Builder poolBuilder = LootPool.builder();
            addMysteryPaperToPool("earth/iron",poolBuilder,0,3);

            table.pool(poolBuilder);
        }
        if (LootTables.RUINED_PORTAL_CHEST.equals(id)) {

            LootPool.Builder netherPortalpoolBuilder = LootPool.builder();
            addMysteryPaperToPool("earth/nether_portal",netherPortalpoolBuilder,0,3);
            LootPool.Builder locatorPoolBuilder = LootPool.builder();
            addMysteryPaperToPool("earth/locator",locatorPoolBuilder);


            table.pool(netherPortalpoolBuilder);
            table.pool(locatorPoolBuilder);
        }


        if (LOOT_TABLES_TO_ADD_GOGGLES_AND_CHEST.contains(id)) {
            LootPool.Builder netherPortalpoolBuilder = LootPool.builder();
            addMysteryPaperToPool("earth/add_chest",netherPortalpoolBuilder);
            LootPool.Builder locatorPoolBuilder = LootPool.builder();
            addMysteryPaperToPool("earth/add_goggles",locatorPoolBuilder);


            table.pool(netherPortalpoolBuilder);
            table.pool(locatorPoolBuilder);
        }

        if (LootTables.VILLAGE_CARTOGRAPHER_CHEST.equals(id)){

            LootPool.Builder cartographerBuilder = LootPool.builder();
            addMysteryPaperToPool("earth/large_map",cartographerBuilder,0,2);

            LootPool.Builder emptyLargeMapBuilder = LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(0,1))
                    .with(ItemEntry.builder(ModItems.EMPTY_LARGE_MAP));

            table.pool(cartographerBuilder);
            table.pool(emptyLargeMapBuilder);
        }

        if (LOOT_TABLES_TO_ADD_LIGHTNING_ABSORBER.contains(id)) {
            LootPool.Builder poolBuilder = LootPool.builder();
            addMysteryPaperToPool("earth/lightning_absorber",poolBuilder);

            table.pool(poolBuilder);
        }

        if (NETHER_BRIDGE_CHEST.equals(id)) {
            LootPool.Builder poolBuilder = LootPool.builder();
            addMysteryPaperToPool("the_nether/the_nether_altar",poolBuilder);
            addMysteryPaperToPool("the_nether/netherrack",poolBuilder,0,3);
            addMysteryPaperToPool("the_nether/redstone",poolBuilder);
            addMysteryPaperToPool("the_nether/quartz",poolBuilder);

            table.pool(poolBuilder);
        }

        if (MOON_VILLAGE_CHEST.equals(id)){
            LootPool.Builder poolBuilder = LootPool.builder();
            addMysteryPaperToPool("moon/gold",poolBuilder);

            table.pool(poolBuilder);
        }
    }

    private static LootPool.Builder addMysteryPaperToPool(String chapterId,LootPool.Builder poolBuilder){
        return addMysteryPaperToPool(chapterId,poolBuilder,0,1);
    }
    private static LootPool.Builder addMysteryPaperToPool(String chapterId,LootPool.Builder poolBuilder,int min,int max){
        NbtCompound altarChapterNbt = new NbtCompound();
        altarChapterNbt.putString("chapter",chapterId);
        poolBuilder
                .rolls(UniformLootNumberProvider.create(min,max))
                .with(ItemEntry.builder(ModItems.MYSTERIOUS_PAPER)
                        .apply(SetNbtLootFunction.builder(altarChapterNbt))
                );
        return poolBuilder;
    }

    private static Identifier getMinecraftEntity(String id){return new Identifier("minecraft","entities/"+id);}
    private static Identifier getMinecraft(String id){return new Identifier("minecraft",id);}
}
