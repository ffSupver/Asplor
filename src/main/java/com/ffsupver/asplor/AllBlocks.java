package com.ffsupver.asplor;

import com.ffsupver.asplor.block.ConnectModel;
import com.ffsupver.asplor.block.airlockSwitch.AirlockSwitch;
import com.ffsupver.asplor.block.airlockSwitch.AirlockSwitchItem;
import com.ffsupver.asplor.block.alloyChest.AlloyChest;
import com.ffsupver.asplor.block.alloyDepot.AlloyDepot;
import com.ffsupver.asplor.block.alloyMechanicalPress.AlloyMechanicalPress;
import com.ffsupver.asplor.block.battery.Battery;
import com.ffsupver.asplor.block.battery.BatteryModel;
import com.ffsupver.asplor.block.blocks.*;
import com.ffsupver.asplor.block.chunkLoader.ChunkLoader;
import com.ffsupver.asplor.block.divider.Divider;
import com.ffsupver.asplor.block.electrolyzer.Electrolyzer;
import com.ffsupver.asplor.block.energyOutputer.EnergyOutput;
import com.ffsupver.asplor.block.generator.Generator;
import com.ffsupver.asplor.block.lightningAbsorber.LightningAbsorber;
import com.ffsupver.asplor.block.liquid_blaze_burner.LiquidBlazeBurner;
import com.ffsupver.asplor.block.liquid_blaze_burner.LiquidBlazeBurnerItem;
import com.ffsupver.asplor.block.mechanicalPump.MechanicalPump;
import com.ffsupver.asplor.block.meltingFurnace.LargeMeltingFurnaceController;
import com.ffsupver.asplor.block.meltingFurnace.LargeMeltingFurnaceFluidPort;
import com.ffsupver.asplor.block.meltingFurnace.LargeMeltingFurnaceItemPort;
import com.ffsupver.asplor.block.meltingFurnace.MeltingFurnace;
import com.ffsupver.asplor.block.motor.Motor;
import com.ffsupver.asplor.block.planetLocator.PlanetLocator;
import com.ffsupver.asplor.block.refinery.RefineryController;
import com.ffsupver.asplor.block.refinery.RefineryInput;
import com.ffsupver.asplor.block.refinery.RefineryOutput;
import com.ffsupver.asplor.block.rocketCargoLoader.RocketCargoLoader;
import com.ffsupver.asplor.block.rocketFuelLoader.RocketFuelLoader;
import com.ffsupver.asplor.block.smartMechanicalArm.SmartMechanicalArm;
import com.ffsupver.asplor.block.smartMechanicalArm.SmartMechanicalArmItem;
import com.ffsupver.asplor.block.smartMechanicalArm.ToolGear;
import com.ffsupver.asplor.block.spaceTeleporter.SpaceTeleporter;
import com.ffsupver.asplor.block.theNetherReturner.TheNetherReturner;
import com.ffsupver.asplor.block.timeInjector.TimeInjector;
import com.ffsupver.asplor.block.windmill.WindmillBearingBlock;
import com.ffsupver.asplor.fluid.ModFluids;
import com.ffsupver.asplor.item.item.DescriptionBlockItem;
import com.ffsupver.asplor.sound.ModSounds;
import com.simibubi.create.AllTags;
import com.simibubi.create.CreateClient;
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
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.ffsupver.asplor.Asplor.REGISTRATE;
import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static net.minecraft.block.Blocks.*;

public class AllBlocks {
    private static final Function<AbstractBlock.Settings,AbstractBlock.Settings> REFINERY_SETTING = settings ->
            settings.strength(4,16).requiresTool().mapColor(MapColor.BLACK).sounds(BlockSoundGroup.DEEPSLATE_BRICKS).allowsSpawning(Blocks::never);
    private static final Function<AbstractBlock.Settings,AbstractBlock.Settings> ALLOY_SETTING = settings ->
            settings.mapColor(MapColor.GRAY).sounds(ModSounds.ALLOY_BLOCK_SOUND_GROUP).strength(8.0f, 20.0f).requiresTool();


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
                    .properties(p->p.nonOpaque().requiresTool())
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

    public static final BlockEntry<TheNetherReturner> THE_NETHER_RETURNER =
            REGISTRATE.block("the_nether_returner", TheNetherReturner::new)
                    .properties(p -> p.mapColor(MapColor.YELLOW).pistonBehavior(PistonBehavior.IGNORE).strength(-1,18000))
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<RefineryController> REFINERY_CONTROLLER =
            REGISTRATE.block("refinery_controller", RefineryController::new)
                    .properties(p -> REFINERY_SETTING.apply(p).pistonBehavior(PistonBehavior.IGNORE))
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<RefineryOutput> REFINERY_OUTPUT =
            REGISTRATE.block("refinery_output", RefineryOutput::new)
                    .properties(p -> REFINERY_SETTING.apply(p).pistonBehavior(PistonBehavior.IGNORE))
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<RefineryInput> REFINERY_INPUT =
            REGISTRATE.block("refinery_input", RefineryInput::new)
                    .properties(p -> REFINERY_SETTING.apply(p).pistonBehavior(PistonBehavior.IGNORE))
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<SmartMechanicalArm> SMART_MECHANICAL_ARM =
            REGISTRATE.block("smart_mechanical_arm", SmartMechanicalArm::new)
                    .properties(p -> p.requiresTool().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.METAL).strength(3.0f,8.0f))
                    .transform(BlockStressDefaults.setImpact(8.0))
                    .item(SmartMechanicalArmItem::new)
                    .build()
                    .register();
    public static final BlockEntry<ToolGear> TOOL_GEAR =
            REGISTRATE.block("tool_gear",ToolGear::new)
                    .properties(p -> p.requiresTool().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.METAL).strength(3.0f,8.0f))
                    .addLayer(()->RenderLayer::getCutoutMipped)
                    .item(BlockItem::new)
                    .build()
                    .register();

    public static final BlockEntry<ChunkLoader> CHUNK_LOADER =
            REGISTRATE.block("chunk_loader",ChunkLoader::new)
                    .properties(p->p.mapColor(MapColor.GOLD).sounds(BlockSoundGroup.METAL).strength(2.5F,4.0F).requiresTool())
                    .addLayer(()->RenderLayer::getCutoutMipped)
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<LargeMeltingFurnaceController> LARGE_MELTING_FURNACE_CONTROLLER =
            REGISTRATE.block("large_melting_furnace_controller",LargeMeltingFurnaceController::new)
                    .properties(ALLOY_SETTING::apply)
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<LargeMeltingFurnaceFluidPort> LARGE_MELTING_FURNACE_FLUID_PORT =
            REGISTRATE.block("large_melting_furnace_fluid_port", LargeMeltingFurnaceFluidPort::new)
                    .properties(ALLOY_SETTING::apply)
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<LargeMeltingFurnaceItemPort> LARGE_MELTING_FURNACE_ITEM_PORT =
            REGISTRATE.block("large_melting_furnace_item_port", LargeMeltingFurnaceItemPort::new)
                    .properties(ALLOY_SETTING::apply)
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<RocketFuelLoader> ROCKET_FUEL_LOADER =
            REGISTRATE.block("rocket_fuel_loader", RocketFuelLoader::new)
                    .properties(ALLOY_SETTING::apply)
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<RocketCargoLoader> ROCKET_CARGO_LOADER =
            REGISTRATE.block("rocket_cargo_loader", RocketCargoLoader::new)
                    .properties(ALLOY_SETTING::apply)
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<AlloyDepot> ALLOY_DEPOT =
            REGISTRATE.block("alloy_depot", AlloyDepot::new)
                    .properties(p->ALLOY_SETTING.apply(p).nonOpaque())
                    .addLayer(() -> RenderLayer::getCutoutMipped)
                    .item(BlockItem::new)
                    .build()
                    .register();
    public static final BlockEntry<LightningAbsorber> LIGHTNING_ABSORBER =
            REGISTRATE.block("lightning_absorber", LightningAbsorber::new)
                    .properties(p -> FabricBlockSettings.copy(LIGHTNING_ROD))
                    .addLayer(() -> RenderLayer::getCutoutMipped)
                    .item((lightningAbsorber, settings) -> new DescriptionBlockItem(lightningAbsorber,settings,Text.translatable("description.asplor.lightning_absorber")))
                    .build()
                    .register();
    public static final BlockEntry<PlanetLocator> PLANET_LOCATOR =
            REGISTRATE.block("planet_locator", PlanetLocator::new)
                    .properties(p -> FabricBlockSettings.copy(IRON_BLOCK))
                    .addLayer(() -> RenderLayer::getCutoutMipped)
                    .item((lightningAbsorber, settings) -> new DescriptionBlockItem(lightningAbsorber,settings,Text.translatable("description.asplor.planet_locator")))
                    .build()
                    .register();




    //注册普通方块
    public  static final Block ALLOY_BLOCK=registerBlock("alloy_block",new Block(FabricBlockSettings.create().mapColor(MapColor.GRAY).strength(8.0f, 20.0f).sounds(ModSounds.ALLOY_BLOCK_SOUND_GROUP).solid().requiresTool()));
    public static final Block ALLOY_CHEST=registerBlock("alloy_chest",new AlloyChest(FabricBlockSettings.create().mapColor(MapColor.GRAY).strength(4.0f, 20.0f).sounds(ModSounds.ALLOY_BLOCK_SOUND_GROUP).solid().requiresTool()));
    public static final Block ASSEMBLER=registerBlock("assembler",new Assembler(FabricBlockSettings.create().mapColor(MapColor.BLUE).strength(0.8f, 4.0f).sounds(BlockSoundGroup.WOOD).solid()));

    public static final Block REFINED_OIL = registerFluidBlock("refined_oil",ModFluids.REFINED_OIL,settings -> settings.mapColor(MapColor.BLACK));

    public static final Block ENERGY_OUTPUT = registerBlock("energy_output",new EnergyOutput());
    public static final Block ANDESITE_MACHINE = registerBlock("andesite_machine",new CasingBlock(FabricBlockSettings.copy(BIRCH_WOOD)));
    public static final Block GLUE = registerFluidBlock("glue",ModFluids.GLUE,settings -> settings.mapColor(MapColor.GREEN));
    public  static final Block UNPACKING_TABLE=registerBlock("unpacking_table",new Block(FabricBlockSettings.create().mapColor(MapColor.GRAY).strength(8.0f, 20.0f).sounds(ModSounds.ALLOY_BLOCK_SOUND_GROUP).solid().requiresTool()));
    public static final Block MOLTEN_IRON = registerMoltenMetalFluidBlock("molten_iron",ModFluids.MOLTEN_IRON,null);
    public static final Block MOLTEN_GOLD = registerMoltenMetalFluidBlock("molten_gold",ModFluids.MOLTEN_GOLD,null);
    public static final Block MOLTEN_COPPER = registerMoltenMetalFluidBlock("molten_copper",ModFluids.MOLTEN_COPPER,null);
    public static final Block MOLTEN_ZINC = registerMoltenMetalFluidBlock("molten_zinc",ModFluids.MOLTEN_ZINC,null);

    public static final Block MOLTEN_BRASS = registerMoltenMetalFluidBlock("molten_brass",ModFluids.MOLTEN_BRASS,null);
    public static final Block MOLTEN_ALLOY = registerMoltenMetalFluidBlock("molten_alloy",ModFluids.MOLTEN_ALLOY,null);

    public static final Block FROZEN_CORE=registerBlock("frozen_core",new FrozenCore(FabricBlockSettings.create().mapColor(MapColor.PALE_PURPLE).strength(0.8f, 2.0f).sounds(BlockSoundGroup.GLASS).solid()));

    public static final Block SALT_WATER = registerFluidBlock("salt_water",ModFluids.SALT_WATER,settings -> settings.mapColor(MapColor.WATER_BLUE));
    public static final Block CHLORINE = registerFluidBlock("chlorine",ModFluids.CHLORINE,settings -> settings.mapColor(MapColor.LICHEN_GREEN));
    public static final Block UNSTABLE_ROCK = registerBlock("unstable_rock",new UnstableRock(FabricBlockSettings.copy(STONE)));
    public static final Block ALLOY_LAVA = registerFluidBlock("alloy_lava",ModFluids.ALLOY_LAVA,p->p.mapColor(MapColor.RED).luminance(15));
    public static final Block MARS_SAND = registerBlock("mars_sand",new FallingBlock(AbstractBlock.Settings.copy(SAND).mapColor(MapColor.TERRACOTTA_RED)));
    public static Block SUSPICIOUS_MARS_SAND = registerBlock("suspicious_mars_sand",new BrushableBlock(MARS_SAND,FabricBlockSettings.copy(Blocks.SUSPICIOUS_SAND), SoundEvents.ITEM_BRUSH_BRUSHING_SAND, SoundEvents.ITEM_BRUSH_BRUSHING_SAND_COMPLETE));
    public static final Block MOLTEN_DESH = registerMoltenMetalFluidBlock("molten_desh",ModFluids.MOLTEN_DESH,null);
    public static final Block IMPURE_MOLTEN_DESH = registerMoltenMetalFluidBlock("impure_molten_desh",ModFluids.IMPURE_MOLTEN_DESH,null);
    public static final Block MOLTEN_OSTRUM = registerMoltenMetalFluidBlock("molten_ostrum",ModFluids.MOLTEN_OSTRUM,null);

    public static final Block FLINT_BLOCK = registerBlock("flint_block",new Block(FabricBlockSettings.copy(IRON_BLOCK).mapColor(MapColor.BLACK).strength(1.5F, 2.0F).requiresTool()));
    public static final Block HYDROCHLORIC_ACID = registerFluidBlock("hydrochloric_acid",ModFluids.HYDROCHLORIC_ACID,settings -> settings.mapColor(MapColor.LIGHT_BLUE));
    public static final Block CONCENTRATED_OIL = registerFluidBlock("concentrated_oil",ModFluids.CONCENTRATED_OIL,settings ->  settings.mapColor(MapColor.BLACK));
    public static final Block REFINERY_BRICKS = registerBlock("refinery_bricks",new Block(REFINERY_SETTING.apply(FabricBlockSettings.create())));
    public static final Block REFINERY_GLASS = registerBlock("refinery_glass",new GlassBlock(REFINERY_SETTING.apply(FabricBlockSettings.create()).suffocates(Blocks::never).blockVision(Blocks::never).solidBlock(Blocks::never).sounds(BlockSoundGroup.GLASS).nonOpaque()));
    public static final Block HEAVY_OIL = registerFluidBlock("heavy_oil",ModFluids.HEAVY_OIL,settings -> settings.mapColor(MapColor.BROWN));
    public static final Block LIGHT_OIL = registerFluidBlock("light_oil",ModFluids.LIGHT_OIL,settings -> settings.mapColor(MapColor.YELLOW));
    public static final Block AllOY_CASING = registerBlock("alloy_casing",new CasingBlock(FabricBlockSettings.create().strength(2.0f,7.0f).sounds(BlockSoundGroup.METAL).requiresTool()));
    public static final Block AllOY_MACHINE = registerBlock("alloy_machine",new CasingBlock(FabricBlockSettings.create().strength(2.0f,7.0f).sounds(BlockSoundGroup.METAL).requiresTool()));
    public static final Block GOLD_ORCHID = registerBlock("gold_orchid",new GoldOrchidBlock(FabricBlockSettings.copy(BEETROOTS)));
    public static final Block FARM_MOON_SAND = registerBlock("farm_moon_sand",new FarmMoonSandBlock(FabricBlockSettings.copy(SAND).mapColor(MapColor.STONE_GRAY).ticksRandomly()));
    public static final Block IRON_AIRLOCK_SWITCH = registerBlock("iron_airlock_switch",new AirlockSwitch(FabricBlockSettings.copy(IRON_BLOCK)),AirlockSwitchItem::new);
    public static final Block IRON_PLATING_AIRLOCK_SWITCH = registerBlock("iron_plating_airlock_switch",new AirlockSwitch(AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).instrument(Instrument.IRON_XYLOPHONE).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.COPPER)),AirlockSwitchItem::new);
    public static final Block POLISHED_CUT_CALCITE_AIRLOCK_SWITCH = registerBlock("polished_cut_calcite_airlock_switch",new AirlockSwitch(AbstractBlock.Settings.copy(CALCITE)),AirlockSwitchItem::new);
    public  static final Block CHARGED_ALLOY_BLOCK=registerBlock("charged_alloy_block",new Block(FabricBlockSettings.create().mapColor(MapColor.GRAY).strength(8.0f, 20.0f).sounds(ModSounds.ALLOY_BLOCK_SOUND_GROUP).solid().requiresTool()),(block,setting)->new DescriptionBlockItem(block,setting, Text.translatable("description.asplor.charged_alloy_block")));
    public static final Block ASTRA_DIABASE_DUST = registerBlock("outer_space/astra_diabase_dust",new FallingBlock(FabricBlockSettings.copy(SAND)));
    public static final Block ASTRA_DIABASE_STONE = registerBlock("outer_space/astra_diabase_stone",new Block(FabricBlockSettings.copy(STONE)));
    public static final Block ASTRA_DIABASE_COBBLESTONE = registerBlock("outer_space/astra_diabase_cobblestone",new Block(FabricBlockSettings.copy(COBBLESTONE)));
    public static final Block ASTRA_DIABASE_COPPER_ORE = registerBlock("outer_space/astra_diabase_copper_ore",new Block(FabricBlockSettings.copy(IRON_ORE).strength(1.6f)));
    public static final Block ASTRA_DIABASE_DIRT = registerBlock("outer_space/astra_diabase_dirt",new Block(FabricBlockSettings.copy(DIRT)));
    public static final Block ASTRA_IRON_ORE = registerBlock("outer_space/astra_iron_ore",new Block(FabricBlockSettings.copy(IRON_ORE)));

    public static final Block ASTRA_SILVER_ORE = registerBlock("outer_space/astra_silver_ore",new Block(FabricBlockSettings.copy(IRON_ORE)));
    public static final Block METEORITE = registerBlock("meteorite",new Block(FabricBlockSettings.copy(STONE)));
    public static final Block GLACIO_ETRUIM_ORE = registerBlock("glacio_etrium_ore",new Block(FabricBlockSettings.copy(STONE).strength(4.0f)));

    private static Block registerMoltenMetalFluidBlock(String name, FlowableFluid fluid,@Nullable Function<FabricBlockSettings,FabricBlockSettings> setting){
        FabricBlockSettings baseSetting = FabricBlockSettings.create().replaceable().luminance(15).mapColor(MapColor.RED);
        return Registry.register(Registries.BLOCK,new Identifier(Asplor.MOD_ID,name),new MoltenMetalBlock(fluid,setting == null ? baseSetting : setting.apply(baseSetting)));
    }

    private static Block registerFluidBlock(String name, FlowableFluid fluid,@Nullable Function<FabricBlockSettings,FabricBlockSettings> setting){
        FabricBlockSettings blockSettings = FabricBlockSettings.create().replaceable();
        return Registry.register(Registries.BLOCK,new Identifier(Asplor.MOD_ID,name),new FluidBlock(fluid,setting == null ? blockSettings : setting.apply(blockSettings)));
    }
    private static Block registerBlock(String name, Block block){
        return registerBlock(name,block,name,new BlockItem(block,new FabricItemSettings()));
    }
    private static Block registerBlock(String name, Block block,String itemName, Item blockItem){
        registerBlockItem(itemName,blockItem);
        return registerBlockWithoutItem(name,block);
    }

    private static <T extends BlockItem> Block registerBlock(String name, Block block, BiFunction<Block, Item.Settings, T> blockItemFactory){
        return registerBlock(name,block,name, blockItemFactory.apply(block,new FabricItemSettings()));
    }

    private static Block registerBlockWithoutItem(String name,Block block){
        return Registry.register(Registries.BLOCK,new Identifier(Asplor.MOD_ID,name),block);
    }
    private static Item registerBlockItem(String name,Item blockItem){
        return Registry.register(Registries.ITEM,new Identifier(Asplor.MOD_ID,name),
                blockItem);
    }




    public static void register(){}

    public static void registerRender(){
        registerRenderLayer(REFINERY_GLASS,RenderLayer.getCutoutMipped());
        registerRenderLayer(GOLD_ORCHID,RenderLayer.getCutoutMipped());


        registerConnectTexture(AllOY_CASING,"alloy");
        registerConnectTexture(REFINERY_GLASS,"refinery");


        CreateClient.MODEL_SWAPPER.getCustomBlockModels().register(Registries.BLOCK.getId(REFINERY_GLASS), ConnectModel::new);
        CreateClient.MODEL_SWAPPER.getCustomBlockModels().register(Registries.BLOCK.getId(AllOY_CASING), ConnectModel::new);
    }

    private static void registerConnectTexture(Block block,String name){
        ConnectModel.registerConnectBlocks(block,new Identifier(Asplor.MOD_ID,name));
    }

    private static void registerRenderLayer(Block block,RenderLayer renderLayer){
        BlockRenderLayerMap.INSTANCE.putBlock(block,renderLayer);
    }
}
