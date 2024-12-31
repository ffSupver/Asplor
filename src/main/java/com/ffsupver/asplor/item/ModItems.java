package com.ffsupver.asplor.item;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.AllPartialModels;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.entity.ModEntities;
import com.ffsupver.asplor.entity.client.CargoRocketRenderer;
import com.ffsupver.asplor.entity.client.Tier0RocketModelLayer;
import com.ffsupver.asplor.fluid.ModFluids;
import com.ffsupver.asplor.item.item.ToolItem;
import com.ffsupver.asplor.item.item.*;
import com.ffsupver.asplor.item.item.largeMap.EmptyLargeMapItem;
import com.ffsupver.asplor.item.item.largeMap.LargeMapItem;
import com.ffsupver.asplor.item.item.singleItemCell.SingleItemCellItem;
import com.ffsupver.asplor.item.renderer.RocketItemRenderer;
import com.jozufozu.flywheel.core.PartialModel;
import com.tterrag.registrate.util.entry.ItemEntry;
import earth.terrarium.adastra.common.items.vehicles.RocketItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.impl.client.rendering.BuiltinItemRendererRegistryImpl;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static com.ffsupver.asplor.Asplor.REGISTRATE;

public class ModItems {
    public static final Item ALLOY_INGOT = registerItems("alloy_ingot",new Item(new FabricItemSettings()));
    public static final Item CHARGED_ALLOY_INGOT = registerItems("charged_alloy_ingot",new Item(new FabricItemSettings()));
    public static final Item SALT = registerItems("salt",new Item(new FabricItemSettings()));
    public static final Item  ZINC_COPPER_BATTERY = registerItems("zinc_copper_battery",new BatteryItem(new FabricItemSettings().maxCount(1)));


    public static final Item UNCOMPLETED_ALLOY_CHEST = registerItems("uncompleted_alloy_chest",new Item(new FabricItemSettings().maxCount(1)));
    public static final Item REFINED_OIL_BUCKET = registerBucketItem("refined_oil_bucket",ModFluids.REFINED_OIL);
    public static final Item GLUE_BUCKET = registerBucketItem("glue_bucket",ModFluids.GLUE);
    public static final Item SALT_WATER_BUCKET = registerBucketItem("salt_water_bucket",ModFluids.SALT_WATER);
    public static final Item CHLORINE_BUCKET = registerItems("chlorine_bucket",new PlaceableBucketItem(ModFluids.CHLORINE,new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1),false));
    public static final Item MOLTEN_IRON_BUCKET = registerBucketItem("molten_iron_bucket",ModFluids.MOLTEN_IRON);

    public static final Item MOLTEN_GOLD_BUCKET = registerBucketItem("molten_gold_bucket",ModFluids.MOLTEN_GOLD);
    public static final Item MOLTEN_COPPER_BUCKET = registerBucketItem("molten_copper_bucket",ModFluids.MOLTEN_COPPER);
    public static final Item MOLTEN_ZINC_BUCKET = registerBucketItem("molten_zinc_bucket",ModFluids.MOLTEN_ZINC);
    public static final Item MOLTEN_BRASS_BUCKET = registerBucketItem("molten_brass_bucket",ModFluids.MOLTEN_BRASS);
    public static final Item MOLTEN_ALLOY_BUCKET = registerBucketItem("molten_alloy_bucket",ModFluids.MOLTEN_ALLOY);
    public static final Item ALLOY_LAVA_BUCKET = registerBucketItem("alloy_lava_bucket",ModFluids.ALLOY_LAVA);
    public static final Item MOLTEN_DESH_BUCKET = registerBucketItem("molten_desh_bucket",ModFluids.MOLTEN_DESH);
    public static final Item IMPURE_MOLTEN_DESH_BUCKET = registerBucketItem("impure_molten_desh_bucket",ModFluids.IMPURE_MOLTEN_DESH);
    public static final Item HYDROCHLORIC_ACID_BUCKET = registerBucketItem("hydrochloric_acid_bucket",ModFluids.HYDROCHLORIC_ACID);
    public static final Item CONCENTRATED_OIL_BUCKET = registerBucketItem("concentrated_oil_bucket",ModFluids.CONCENTRATED_OIL);
    public  static final Item HEAVY_OIL_BUCKET = registerBucketItem("heavy_oil_bucket",ModFluids.HEAVY_OIL);
    public  static final Item LIGHT_OIL_BUCKET = registerBucketItem("light_oil_bucket",ModFluids.LIGHT_OIL);





    public static final Item SPACE_TELEPORTER_ANCHOR = registerItems("space_teleporter_anchor",new SpaceTeleporterAnchor(new FabricItemSettings().maxCount(1)));
    public static final ItemEntry<Item> PACKER=REGISTRATE.item("packer",Item::new).register();
    public static final Item PRIMARY_MECHANISM = registerItems("primary_mechanism",new Item(new FabricItemSettings()));
    public static final Item MECHANICAL_PRESS_HEAD = registerItems("mechanical_press_head",new Item(new FabricItemSettings()));
    public static final Item TIER_0_ROCKET = registerItems("tier_0_rocket",new RocketItem(()-> ModEntities.TIER_0_ROCKET,new FabricItemSettings().maxCount(1).fireproof()));
    public static final Item LOCATOR = registerItems("locator",new LocatorItem(new FabricItemSettings().maxCount(1)));
    public static final Item ZINC_SHEET = registerItems("zinc_sheet",new Item(new FabricItemSettings()));
    public static final Item GUIDE_BOOK = registerItems("guide_book",new GuideBookItem(new FabricItemSettings().maxCount(1)));
    public static final Item MYSTERIOUS_PAPER = registerItems("mysterious_paper",new MysteriousPageItem(new FabricItemSettings()));
    public static final Item CARBON_POWDER = registerItems("carbon_powder",new Item(new FabricItemSettings()));
    public static final Item TIER_0_ROCKET_SHELL = registerItems("tier_0_rocket_shell",new Item(new FabricItemSettings()));
    public static final Item EMPTY_TANK = registerItems("empty_tank",new Item(new FabricItemSettings().maxCount(16)));
    public static final Item CONCENTRATED_OIL_TANK = registerItems("concentrated_oil_tank",new Item(new FabricItemSettings().maxCount(1)));
    public static final Item DRILL = registerItems("drill",new Item(new FabricItemSettings()));
    public static final Item SAW = registerItems("saw",new Item(new FabricItemSettings()));
    public static final Item DIVIDER_TOOL = registerItems("divider_tool",new Item(new FabricItemSettings()));
    public static final Item WINDMILL_HEAD = registerItems("windmill_head",new Item(new FabricItemSettings()));
    public static final Item UNCOMPLETED_PRIMARY_MECHANISM = registerItems("uncompleted_primary_mechanism",new Item(new FabricItemSettings()));
    public static final Item DRILL_TOOL = registerItems("drill_tool",new ToolItem(new FabricItemSettings(),new Identifier(Asplor.MOD_ID,"drill"),256));
    public static final Item USED_DRILL_TOOL = registerItems("used_drill_tool",new ToolItem(new FabricItemSettings(),new Identifier(Asplor.MOD_ID,"empty"),0));
    public static final Item LASER_TOOL = registerItems("laser_tool",new ToolItem(new FabricItemSettings(),new Identifier(Asplor.MOD_ID,"laser"),16));
    public static final Item USED_LASER_TOOL = registerItems("used_laser_tool",new ToolItem(new FabricItemSettings(),new Identifier(Asplor.MOD_ID,"empty"),0));
    public static final Item SINGLE_ITEM_STORAGE_CELL_4K = registerItems("single_item_storage_cell_4k",new SingleItemCellItem(new FabricItemSettings().maxCount(1),4,5.0));
    public static final Item SINGLE_ITEM_STORAGE_CELL_16K = registerItems("single_item_storage_cell_16k",new SingleItemCellItem(new FabricItemSettings().maxCount(1),16,9.0));
    public static final Item SINGLE_ITEM_STORAGE_CELL_64K = registerItems("single_item_storage_cell_64k",new SingleItemCellItem(new FabricItemSettings().maxCount(1),64,13.0));
    public static final Item SINGLE_ITEM_STORAGE_CELL_256K = registerItems("single_item_storage_cell_256k",new SingleItemCellItem(new FabricItemSettings().maxCount(1),256,17.0));
    public static final Item SINGLE_ITEM_STORAGE_CELL_1M = registerItems("single_item_storage_cell_1m",new SingleItemCellItem(new FabricItemSettings().maxCount(1),1024,21.0));
    public static final Item EMPTY_DROPPER = registerItems("empty_dropper",new ToolItem(new FabricItemSettings(),new Identifier(Asplor.MOD_ID,"empty"),0));
    public static final Item MOLTEN_GOLD_DROPPER = registerItems("molten_gold_dropper",new ToolItem(new FabricItemSettings(),new Identifier(Asplor.MOD_ID,"molten_gold_dropper"),1));
    public static final Item GLUE_DROPPER = registerItems("glue_dropper",new ToolItem(new FabricItemSettings(),new Identifier(Asplor.MOD_ID,"glue_dropper"),4));
    public static final Item SINGLE_ITEM_STORAGE_CELL_HOUSING = registerItems("single_item_storage_cell_housing",new Item(new FabricItemSettings()));
    public static final Item KELP_PUREE = registerItems("kelp_puree",new Item(new FabricItemSettings()));
    public static final Item ALLOY_NUGGET = registerItems("alloy_nugget",new Item(new FabricItemSettings()));
    public static final Item ALLOY_HELMET = registerItems("alloy_helmet",new ArmorItem(AlloyMaterial.Armor.MATERIAL, ArmorItem.Type.HELMET,new FabricItemSettings().maxCount(1).maxDamage(384)));
    public static final Item ALLOY_CHESTPLATE = registerItems("alloy_chestplate",new ArmorItem(AlloyMaterial.Armor.MATERIAL, ArmorItem.Type.CHESTPLATE,new FabricItemSettings().maxCount(1).maxDamage(560)));
    public static final Item ALLOY_LEGGINGS = registerItems("alloy_leggings",new ArmorItem(AlloyMaterial.Armor.MATERIAL, ArmorItem.Type.LEGGINGS,new FabricItemSettings().maxCount(1).maxDamage(525)));
    public static final Item ALLOY_BOOTS = registerItems("alloy_boots",new ArmorItem(AlloyMaterial.Armor.MATERIAL, ArmorItem.Type.BOOTS,new FabricItemSettings().maxCount(1).maxDamage(455)));
    public static final Item CRUDE_PRINTED_CALCULATION_PROCESSOR = registerItems("crude_printed_calculation_processor",new Item(new FabricItemSettings()));
    public static final Item CRUDE_PRINTED_ENGINEERING_PROCESSOR = registerItems("crude_printed_engineering_processor",new Item(new FabricItemSettings()));
    public static final Item CRUDE_PRINTED_LOGIC_PROCESSOR = registerItems("crude_printed_logic_processor",new Item(new FabricItemSettings()));
    public static final Item MOLTEN_ALLOY_DROPPER = registerItems("molten_alloy_dropper",new ToolItem(new FabricItemSettings(),new Identifier(Asplor.MOD_ID,"molten_alloy_dropper"),2));
    public static final Item MOLTEN_BRASS_DROPPER = registerItems("molten_brass_dropper",new ToolItem(new FabricItemSettings(),new Identifier(Asplor.MOD_ID,"molten_brass_dropper"),3));
    public static final Item ALLOY_SWORD = registerItems("alloy_sword",new SwordItem(AlloyMaterial.Tool.MATERIAL,3,-2.4F,new FabricItemSettings()));
    public static final Item ALLOY_SHOVEL = registerItems("alloy_shovel",new ShovelItem(AlloyMaterial.Tool.MATERIAL,1.5F,-3.0F,new FabricItemSettings()));
    public static final Item ALLOY_PICKAXE = registerItems("alloy_pickaxe",new PickaxeItem(AlloyMaterial.Tool.MATERIAL,1,-2.8F,new FabricItemSettings()));
    public static final Item ALLOY_AXE = registerItems("alloy_axe",new AxeItem(AlloyMaterial.Tool.MATERIAL,5.0F,-3.0F,new FabricItemSettings()));
    public static final Item ALLOY_HOE = registerItems("alloy_hoe",new HoeItem(AlloyMaterial.Tool.MATERIAL,-4,0.0F,new FabricItemSettings()));
    public static final Item INFUSION_CLOCK = registerItems("infusion_clock",new InfusionClockItem(new FabricItemSettings()));
    public static final Item IRON_MECHANISM = registerItems("iron_mechanism",new Item(new FabricItemSettings()));
    public static final Item UNCOMPLETED_IRON_MECHANISM = registerItems("uncompleted_iron_mechanism",new Item(new FabricItemSettings().maxCount(1)));
    public static final Item COPPER_WIRE = registerItems("copper_wire",new Item(new FabricItemSettings()));
    public static final Item MAGNETIC_ROTOR = registerItems("magnetic_rotor",new Item(new FabricItemSettings()));
    public static final Item ALLOY_MECHANISM = registerItems("alloy_mechanism",new Item(new FabricItemSettings()));
    public static final Item UNCOMPLETED_ALLOY_MECHANISM = registerItems("uncompleted_alloy_mechanism",new Item(new FabricItemSettings().maxCount(1)));
    public static final Item RESONANT_CRYSTAL = registerItems("resonant_crystal",new Item(new FabricItemSettings()));
    public static final Item DIAMOND_SHARD = registerItems("diamond_shard",new Item(new FabricItemSettings()));
    public static final Item GOLD_ORCHID_STAMEN = registerItems("gold_orchid_stamen",new Item(new FabricItemSettings()));
    public static final Item GOLD_ORCHID_SEED = registerItems("gold_orchid_seed",new AliasedBlockItem(AllBlocks.GOLD_ORCHID,new FabricItemSettings()));
    public static final Item LARGE_MAP = registerItems("large_map",new LargeMapItem(new FabricItemSettings()));
    public static final Item EMPTY_LARGE_MAP = registerItems("empty_large_map",new EmptyLargeMapItem(new FabricItemSettings()));
    public static final Item TIER_1_ROCKET_SHELL = registerItems("tier_1_rocket_shell",new Item(new FabricItemSettings()));
    public static final Item CARGO_ROCKET = registerItems("cargo_rocket",new CargoRocketItem(new FabricItemSettings().maxCount(1).fireproof()));

    public static final Item SCHEMATIC = registerItems("schematic",new SchematicItem(new FabricItemSettings()));
    public static final Item SCHEMATIC_SHARD = registerItems("schematic_shard",new SchematicItem(new FabricItemSettings()));
    public static final Item SPACE_CORE = registerItems("space_core",new Item(new FabricItemSettings()));


    @Environment(EnvType.CLIENT)
    private static void registerRocketItemRender(){
        BuiltinItemRendererRegistryImpl.INSTANCE.register(TIER_0_ROCKET,  new RocketItemRenderer(Tier0RocketModelLayer.TIER_0_ROCKET_MODEL,Tier0RocketModelLayer.TIER_0_ROCKET_TEXTURE));
        BuiltinItemRendererRegistryImpl.INSTANCE.register(CARGO_ROCKET, new RocketItemRenderer(CargoRocketRenderer.CARGO_ROCKET_MODEL,CargoRocketRenderer.CARGO_ROCKET_TEXTURE));
    }

    @Environment(EnvType.CLIENT)
    private static void registerToolItemModels(){
        registerToolItemModel(DRILL_TOOL,AllPartialModels.DRILL_TOOL,AllPartialModels.DRILL_TOOL);
        registerToolItemModel(USED_DRILL_TOOL,AllPartialModels.DRILL_TOOL,AllPartialModels.DRILL_TOOL);
        registerToolItemModel(LASER_TOOL,AllPartialModels.LASER_TOOL,AllPartialModels.LASER_TOOL_WORK);
        registerToolItemModel(USED_LASER_TOOL,AllPartialModels.LASER_TOOL,AllPartialModels.LASER_TOOL);
        registerToolItemModel(EMPTY_DROPPER,AllPartialModels.EMPTY_DROPPER,AllPartialModels.EMPTY_DROPPER);
        registerToolItemModel(MOLTEN_GOLD_DROPPER,AllPartialModels.MOLTEN_GOLD_DROPPER,AllPartialModels.MOLTEN_GOLD_DROPPER_WORK);
        registerToolItemModel(MOLTEN_ALLOY_DROPPER,AllPartialModels.MOLTEN_ALLOY_DROPPER,AllPartialModels.MOLTEN_ALLOY_DROPPER_WORK);
        registerToolItemModel(GLUE_DROPPER,AllPartialModels.GLUE_DROPPER,AllPartialModels.GLUE_DROPPER_WORK);
        registerToolItemModel(MOLTEN_BRASS_DROPPER,AllPartialModels.MOLTEN_BRASS_DROPPER,AllPartialModels.MOLTEN_BRASS_DROPPER_WORK);

    }

    @Environment(EnvType.CLIENT)
    private static void registerToolItemModel(Item item, PartialModel toolModel,PartialModel toolWorkModel){
        if (item instanceof ToolItem toolItem){
            toolItem.setModel(toolModel,toolWorkModel);
        }
    }
    @Environment(EnvType.CLIENT)
    public static void registerItemClient(){
        registerRocketItemRender();
        registerToolItemModels();
    }



    private static <T extends IPart> Item registerAEPartItem(String name, Item.Settings settings, Class<T> partClass, Function<IPartItem<T>,T> factory){
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return registerItems(name,new PartItem<T>(settings,partClass,factory));
    }

    private static Item registerBucketItem(String name, Fluid fluid){
        return registerBucketItem(name,fluid,null);
    }
    private static Item registerBucketItem(String name, Fluid fluid, Function<Item.Settings,Item.Settings> setting){
        Item.Settings baseSetting = new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1);
        return registerItems(name,new BucketItem(fluid,setting==null?baseSetting:setting.apply(baseSetting)));
    }

    private static <T extends Item> T registerItems(String name,T item){
        return Registry.register(Registries.ITEM,new Identifier(Asplor.MOD_ID,name),item);
    }
    public static void registerModItems(){
    }

}
