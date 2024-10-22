package com.ffsupver.asplor;

import com.ffsupver.asplor.block.alloyChest.AlloyChest;
import com.ffsupver.asplor.block.alloy_mechanical_press.AlloyMechanicalPress;
import com.ffsupver.asplor.block.battery.Battery;
import com.ffsupver.asplor.block.battery.BatteryModel;
import com.ffsupver.asplor.block.blocks.Assembler;
import com.ffsupver.asplor.block.blocks.FrozenCore;
import com.ffsupver.asplor.block.blocks.UnstableRock;
import com.ffsupver.asplor.block.divider.Divider;
import com.ffsupver.asplor.block.electrolyzer.Electrolyzer;
import com.ffsupver.asplor.block.energyOutputer.EnergyOutput;
import com.ffsupver.asplor.block.generator.Generator;
import com.ffsupver.asplor.block.liquid_blaze_burner.LiquidBlazeBurner;
import com.ffsupver.asplor.block.liquid_blaze_burner.LiquidBlazeBurnerItem;
import com.ffsupver.asplor.block.mechanicalPump.MechanicalPump;
import com.ffsupver.asplor.block.meltingFurnace.MeltingFurnace;
import com.ffsupver.asplor.block.motor.Motor;
import com.ffsupver.asplor.block.spaceTeleporter.SpaceTeleporter;
import com.ffsupver.asplor.block.timeInjector.TimeInjector;
import com.ffsupver.asplor.block.windmill.WindmillBearingBlock;
import com.ffsupver.asplor.fluid.ModFluids;
import com.ffsupver.asplor.sound.ModSounds;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.content.processing.burner.BlazeBurnerMovementBehaviour;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.utility.Couple;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static com.ffsupver.asplor.Asplor.REGISTRATE;
import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class AllBlocks {
    public static final BlockEntry<Divider> DIVIDER =
            REGISTRATE.block("divider", Divider::new)
            .properties(p -> p.mapColor(MapColor.IRON_GRAY).nonOpaque().strength(4f,4f).nonOpaque().sounds(BlockSoundGroup.WOOD))
            .transform(pickaxeOnly())
            .blockstate((c, p) -> AssetLookup.standardModel(c,p))
            .addLayer(() -> RenderLayer::getCutoutMipped)
            .transform(BlockStressDefaults.setImpact(4.0))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<AlloyMechanicalPress> ALLOY_MECHANICAL_PRESS =
            REGISTRATE.block("alloy_mechanical_press", AlloyMechanicalPress::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.nonOpaque().mapColor(MapColor.SPRUCE_BROWN))
                    .transform(axeOrPickaxe())
                    .transform(BlockStressDefaults.setImpact(12.0))
                    .item(AssemblyOperatorBlockItem::new)
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<LiquidBlazeBurner> LIQUID_BLAZE_BURNER =
            REGISTRATE.block("liquid_blaze_burner", LiquidBlazeBurner::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.GRAY).luminance(LiquidBlazeBurner::getLight))
                    .transform(pickaxeOnly())
                    .addLayer(() -> RenderLayer::getCutoutMipped)
                    .tag(AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_BLASTING.tag, AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SMOKING.tag, AllTags.AllBlockTags.FAN_TRANSPARENT.tag, AllTags.AllBlockTags.PASSIVE_BOILER_HEATERS.tag)
                    .onRegister(movementBehaviour(new BlazeBurnerMovementBehaviour()))
                    .item(LiquidBlazeBurnerItem::new)
                    .build()
                    .register();

    public static final BlockEntry<Generator> GENERATOR =
            REGISTRATE.block("generator", Generator::new)
            .properties(p -> p.mapColor(MapColor.IRON_GRAY).nonOpaque().strength(4f,4f).nonOpaque().sounds(BlockSoundGroup.WOOD))
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .transform(BlockStressDefaults.setImpact(256.0))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Battery> BATTERY =
            REGISTRATE.block("battery",Battery::new)
                    .initialProperties(SharedProperties::stone)
                    .onRegister(CreateRegistrate.blockModel(()->BatteryModel::standard))
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<TimeInjector> TIME_INJECTOR =
            REGISTRATE.block("time_injector", TimeInjector::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p->p.nonOpaque())
                    .item(BlockItem::new)
                    .build()
                    .register();

    public static final BlockEntry<SpaceTeleporter> SPACE_TELEPORTER =
            REGISTRATE.block("space_teleporter",SpaceTeleporter::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p->p.nonOpaque())
                    .item(BlockItem::new)
                    .build()
                    .register();


    public static final BlockEntry<Motor> MOTOR =
            REGISTRATE.block("motor", Motor::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.ORANGE).solid().nonOpaque())
                    .transform(BlockStressDefaults.setCapacity(256.0))
                    .transform(BlockStressDefaults.setGeneratorSpeed(() -> Couple.create(0, 256)))
                    .item()
//                    .properties(p -> p.rarity(Rarity.EPIC))
                    .transform(customItemModel())
                    .register();

    public static final BlockEntry<MechanicalPump> MECHANICAL_PUMP =
            REGISTRATE.block("mechanical_pump", MechanicalPump::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.GRAY).solid().pistonBehavior(PistonBehavior.IGNORE).nonOpaque())
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .item(BlockItem::new)
                    .build()
                    .register();

    public static final BlockEntry<MeltingFurnace> MELTING_FURNACE =
            REGISTRATE.block("melting_furnace", MeltingFurnace::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.GRAY).solid().nonOpaque().sounds(ModSounds.ALLOY_BLOCK_SOUND_GROUP))
                    .blockstate(AssetLookup::partialBaseModel)
                    .addLayer(() -> RenderLayer::getCutoutMipped)
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<Electrolyzer> ELECTROLYZER =
            REGISTRATE.block("electrolyzer",Electrolyzer::new)
                    .initialProperties(SharedProperties::netheriteMetal)
                    .properties(p->p.mapColor(MapColor.GRAY).nonOpaque())
                    .addLayer(() -> RenderLayer::getCutoutMipped)
                    .item(BlockItem::new)
                    .build()
                    .register();

    public static final BlockEntry<WindmillBearingBlock> WINDMILL_BEARING =
            REGISTRATE.block("windmill_bearing", WindmillBearingBlock::new)
                    .properties(p -> p.mapColor(MapColor.SPRUCE_BROWN))
                    .transform(BuilderTransformers.bearing("windmill", "gearbox"))
                    .transform(BlockStressDefaults.setCapacity(512.0))
                    .transform(BlockStressDefaults.setGeneratorSpeed(WindmillBearingBlock::getSpeedRange))
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .register();

    //注册普通方块
    public  static final Block ALLOY_BLOCK=registerBlock("alloy_block",new Block(FabricBlockSettings.create().strength(8.0f, 20.0f).sounds(ModSounds.ALLOY_BLOCK_SOUND_GROUP).solid().requiresTool()));
    public static final Block ALLOY_CHEST=registerBlock("alloy_chest",new AlloyChest(FabricBlockSettings.create().strength(4.0f, 20.0f).sounds(ModSounds.ALLOY_BLOCK_SOUND_GROUP).solid().requiresTool()));
    public static final Block ASSEMBLER=registerBlock("assembler",new Assembler(FabricBlockSettings.create().strength(0.8f, 4.0f).sounds(BlockSoundGroup.WOOD).solid()));

    public static final Block REFINED_OIL = registerFluidBlock("refined_oil",ModFluids.REFINED_OIL,null);

    public static final Block ENERGY_OUTPUT = registerBlock("energy_output",new EnergyOutput());
    public static final Block ANDESITE_MACHINE = registerBlock("andesite_machine",new CasingBlock(FabricBlockSettings.copy(Blocks.BIRCH_WOOD)));
    public static final Block GLUE = registerFluidBlock("glue",ModFluids.GLUE,null);
    public  static final Block UNPACKING_TABLE=registerBlock("unpacking_table",new Block(FabricBlockSettings.create().strength(8.0f, 20.0f).sounds(ModSounds.ALLOY_BLOCK_SOUND_GROUP).solid().requiresTool()));
    public static final Block MOLTEN_IRON = registerMoltenMetalFluidBlock("molten_iron",ModFluids.MOLTEN_IRON,null);
    public static final Block MOLTEN_GOLD = registerMoltenMetalFluidBlock("molten_gold",ModFluids.MOLTEN_GOLD,null);
    public static final Block MOLTEN_COPPER = registerMoltenMetalFluidBlock("molten_copper",ModFluids.MOLTEN_COPPER,null);
    public static final Block MOLTEN_ZINC = registerMoltenMetalFluidBlock("molten_zinc",ModFluids.MOLTEN_ZINC,null);

    public static final Block MOLTEN_BRASS = registerMoltenMetalFluidBlock("molten_brass",ModFluids.MOLTEN_BRASS,null);
    public static final Block MOLTEN_ALLOY = registerMoltenMetalFluidBlock("molten_alloy",ModFluids.MOLTEN_ALLOY,null);

    public static final Block FROZEN_CORE=registerBlock("frozen_core",new FrozenCore(FabricBlockSettings.create().strength(0.8f, 2.0f).sounds(BlockSoundGroup.GLASS).solid()));

    public static final Block SALT_WATER = registerFluidBlock("salt_water",ModFluids.SALT_WATER,null);
    public static final Block CHLORINE = registerFluidBlock("chlorine",ModFluids.CHLORINE,null);
    public static final Block UNSTABLE_ROCK = registerBlock("unstable_rock",new UnstableRock(FabricBlockSettings.copy(Blocks.STONE)));
    public static final Block ALLOY_LAVA = registerFluidBlock("alloy_lava",ModFluids.ALLOY_LAVA,p->p.mapColor(MapColor.RED));
    public static final Block MARS_SAND = registerBlock("mars_sand",new FallingBlock(AbstractBlock.Settings.copy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_RED)));
    public static Block SUSPICIOUS_MARS_SAND = registerBlock("suspicious_mars_sand",new BrushableBlock(MARS_SAND,FabricBlockSettings.copy(Blocks.SUSPICIOUS_SAND), SoundEvents.ITEM_BRUSH_BRUSHING_SAND, SoundEvents.ITEM_BRUSH_BRUSHING_SAND_COMPLETE));
    public static final Block MOLTEN_DESH = registerMoltenMetalFluidBlock("molten_desh",ModFluids.MOLTEN_DESH,null);
    public static final Block IMPURE_MOLTEN_DESH = registerMoltenMetalFluidBlock("impure_molten_desh",ModFluids.IMPURE_MOLTEN_DESH,null);





    private static Block registerMoltenMetalFluidBlock(String name, FlowableFluid fluid,@Nullable Function<FabricBlockSettings,FabricBlockSettings> setting){

        return registerFluidBlock(name,fluid,(settings)-> {
                    FabricBlockSettings baseSetting = settings.luminance(15).mapColor(MapColor.RED);
                   return setting == null ? baseSetting : setting.apply(baseSetting);
                }
        );
    }

    private static Block registerFluidBlock(String name, FlowableFluid fluid,@Nullable Function<FabricBlockSettings,FabricBlockSettings> setting){
        FabricBlockSettings blockSettings = FabricBlockSettings.create().replaceable();
        return Registry.register(Registries.BLOCK,new Identifier(Asplor.MOD_ID,name),new FluidBlock(fluid,setting == null ? blockSettings : setting.apply(blockSettings)));
    }
    private static Block registerBlock(String name, Block block){
        registerBlockItem(name,block);
        return Registry.register(Registries.BLOCK,new Identifier(Asplor.MOD_ID,name),block);
    }
    private static Item registerBlockItem(String name, Block block){
        return Registry.register(Registries.ITEM,new Identifier(Asplor.MOD_ID,name),
                new BlockItem(block,new FabricItemSettings()));
    }



    public static void register(){}
}
