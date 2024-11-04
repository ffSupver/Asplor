package com.ffsupver.asplor.item;

import com.ffsupver.asplor.AllPartialModels;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.entity.ModEntities;
import com.ffsupver.asplor.entity.client.Tier0RocketModelLayer;
import com.ffsupver.asplor.fluid.ModFluids;
import com.ffsupver.asplor.item.item.*;
import com.ffsupver.asplor.item.item.singleItemCell.SingleItemCellItem;
import com.ffsupver.asplor.item.renderer.RocketItemRenderer;
import com.tterrag.registrate.util.entry.ItemEntry;
import earth.terrarium.adastra.common.items.vehicles.RocketItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.impl.client.rendering.BuiltinItemRendererRegistryImpl;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
    public static final Item DRILL_TOOL = registerItems("drill_tool",new ToolItem(new FabricItemSettings().maxCount(1), AllPartialModels.DRILL_TOOL,AllPartialModels.DRILL_TOOL,new Identifier(Asplor.MOD_ID,"drill"),256));
    public static final Item USED_DRILL_TOOL = registerItems("used_drill_tool",new ToolItem(new FabricItemSettings().maxCount(1), AllPartialModels.DRILL_TOOL,AllPartialModels.DRILL_TOOL,new Identifier(Asplor.MOD_ID,"empty"),0));
    public static final Item LASER_TOOL = registerItems("laser_tool",new ToolItem(new FabricItemSettings().maxCount(1), AllPartialModels.LASER_TOOL,AllPartialModels.LASER_TOOL_WORK,new Identifier(Asplor.MOD_ID,"laser"),16));
    public static final Item USED_LASER_TOOL = registerItems("used_laser_tool",new ToolItem(new FabricItemSettings().maxCount(1), AllPartialModels.LASER_TOOL,AllPartialModels.LASER_TOOL_WORK,new Identifier(Asplor.MOD_ID,"empty"),0));
    public static final Item SINGLE_ITEM_CELL_4K = registerItems("single_item_cell_4k",new SingleItemCellItem(new FabricItemSettings().maxCount(1),4));
    public static final Item SINGLE_ITEM_CELL_8K = registerItems("single_item_cell_8k",new SingleItemCellItem(new FabricItemSettings().maxCount(1),8));


    public static void registerRocketItemRender(){
        BuiltinItemRendererRegistryImpl.INSTANCE.register(TIER_0_ROCKET,  new RocketItemRenderer(Tier0RocketModelLayer.TIER_0_ROCKET_MODEL,Tier0RocketModelLayer.TIER_0_ROCKET_TEXTURE));
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
