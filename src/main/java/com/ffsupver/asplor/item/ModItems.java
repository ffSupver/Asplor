package com.ffsupver.asplor.item;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.fluid.ModFluids;
import com.ffsupver.asplor.item.item.BatteryItem;
import com.ffsupver.asplor.item.item.PlaceableBucketItem;
import com.ffsupver.asplor.item.item.SpaceTeleporterAnchor;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
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





    public static final Item SPACE_TELEPORTER_ANCHOR = registerItems("space_teleporter_anchor",new SpaceTeleporterAnchor(new FabricItemSettings().maxCount(1)));
    public static final ItemEntry<Item> PACKER=REGISTRATE.item("packer",Item::new).register();
    public static final Item PRIMARY_MECHANISM = registerItems("primary_mechanism",new Item(new FabricItemSettings()));
    public static final Item MECHANICAL_PRESS_HEAD = registerItems("mechanical_press_head",new Item(new FabricItemSettings()));


    private static Item registerBucketItem(String name, Fluid fluid){
        return registerBucketItem(name,fluid,null);
    }
    private static Item registerBucketItem(String name, Fluid fluid, Function<Item.Settings,Item.Settings> setting){
        Item.Settings baseSetting = new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1);
        return registerItems(name,new BucketItem(fluid,setting==null?baseSetting:setting.apply(baseSetting)));
    }

    private static Item registerItems(String name,Item item){
        return Registry.register(Registries.ITEM,new Identifier(Asplor.MOD_ID,name),item);
    }
    public static void registerModItems(){
    }

}
