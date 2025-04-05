package com.ffsupver.asplor.fluid;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.fluid.moltenMetal.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModFluids {
    private static final Identifier WATER_STILL_TEXTURE = Identifier.of("minecraft", "block/water_still");
    private static final Identifier WATER_FLOW_TEXTURE = Identifier.of("minecraft", "block/water_flow");



    public static final FlowableFluid REFINED_OIL = registerFluid("refined_oil",new RefinedOilFluid.Still());
    public static final FlowableFluid FLOWING_REFINED_OIL = registerFluid("flowing_refined_oil",new RefinedOilFluid.Flowing());
    public static final FlowableFluid GLUE = registerFluid("glue",new Glue.Still());
    public static final FlowableFluid FLOWING_GLUE = registerFluid("flowing_glue",new Glue.Flowing());
    public static final FlowableFluid SALT_WATER = registerFluid("salt_water",new SaltWaterFluid.Still());
    public static final FlowableFluid FLOWING_SALT_WATER = registerFluid("flowing_salt_water",new SaltWaterFluid.Flowing());
    public static final FlowableFluid CHLORINE = registerFluid("chlorine",new Chlorine.Still());
    public static final FlowableFluid FLOWING_CHLORINE = registerFluid("flowing_chlorine",new Chlorine.Flowing());
    public static final FlowableFluid ALLOY_LAVA = registerFluid("alloy_lava",new AlloyLava.Still());
    public static final FlowableFluid FLOWING_ALLOY_LAVA = registerFluid("flowing_alloy_lava",new AlloyLava.Flowing());
    public static final FlowableFluid HYDROCHLORIC_ACID = registerFluid("hydrochloric_acid",new HydrochloricAcidFluid.Still());
    public static final FlowableFluid FLOWING_HYDROCHLORIC_ACID = registerFluid("flowing_hydrochloric_acid",new HydrochloricAcidFluid.Flowing());
    public static final FlowableFluid CHEESE = registerFluid("cheese",new Cheese.Still());
    public static final FlowableFluid FLOWING_CHEESE = registerFluid("flowing_cheese",new Cheese.Flowing());

    //Molten Metal

    public static final FlowableFluid MOLTEN_IRON = registerFluid("molten_iron",new MoltenIron.Still());
    public static final FlowableFluid FLOWING_MOLTEN_IRON = registerFluid("flowing_molten_iron",new MoltenIron.Flowing());

    public static final FlowableFluid MOLTEN_GOLD = registerFluid("molten_gold",new MoltenGold.Still());
    public static final FlowableFluid FLOWING_MOLTEN_GOLD = registerFluid("flowing_molten_gold",new MoltenGold.Flowing());
    public static final FlowableFluid MOLTEN_COPPER = registerFluid("molten_copper",new MoltenCopper.Still());
    public static final FlowableFluid FLOWING_MOLTEN_COPPER = registerFluid("flowing_molten_copper",new MoltenCopper.Flowing());
    public static final FlowableFluid MOLTEN_ZINC = registerFluid("molten_zinc",new MoltenZinc.Still());
    public static final FlowableFluid FLOWING_MOLTEN_ZINC = registerFluid("flowing_molten_zinc",new MoltenZinc.Flowing());
    public static final FlowableFluid MOLTEN_BRASS = registerFluid("molten_brass",new MoltenBrass.Still());
    public static final FlowableFluid FLOWING_MOLTEN_BRASS = registerFluid("flowing_molten_brass",new MoltenBrass.Flowing());
    public static final FlowableFluid MOLTEN_ALLOY = registerFluid("molten_alloy",new MoltenAlloy.Still());
    public static final FlowableFluid FLOWING_MOLTEN_ALLOY = registerFluid("flowing_molten_alloy",new MoltenAlloy.Flowing());
    public static final FlowableFluid MOLTEN_DESH = registerFluid("molten_desh",new MoltenDesh.Still());
    public static final FlowableFluid FLOWING_MOLTEN_DESH = registerFluid("flowing_molten_desh",new MoltenDesh.Flowing());
    public static final FlowableFluid IMPURE_MOLTEN_DESH = registerFluid("impure_molten_desh",new ImpureMoltenDesh.Still());
    public static final FlowableFluid FLOWING_IMPURE_MOLTEN_DESH = registerFluid("flowing_impure_molten_desh",new ImpureMoltenDesh.Flowing());
    public static final FlowableFluid CONCENTRATED_OIL = registerFluid("concentrated_oil",new ConcentratedOilFluid.Still());
    public static final FlowableFluid FLOWING_CONCENTRATED_OIL = registerFluid("flowing_concentrated_oil",new ConcentratedOilFluid.Flowing());
    public static final FlowableFluid HEAVY_OIL = registerFluid("heavy_oil",new HeavyOilFluid.Still());
    public static final FlowableFluid FLOWING_HEAVY_OIL = registerFluid("flowing_heavy_oil",new HeavyOilFluid.Flowing());
    public static final FlowableFluid LIGHT_OIL = registerFluid("light_oil",new LightOilFluid.Still());
    public static final FlowableFluid FLOWING_LIGHT_OIL = registerFluid("flowing_light_oil",new LightOilFluid.Flowing());
    public static final FlowableFluid MOLTEN_OSTRUM = registerFluid("molten_ostrum",new MoltenOstrum.Still());
    public static final FlowableFluid FLOWING_MOLTEN_OSTRUM = registerFluid("flowing_molten_ostrum",new MoltenOstrum.Flowing());
    public static final FlowableFluid MOLTEN_CALORITE = registerFluid("molten_calorite",new MoltenCalorite.Still());
    public static final FlowableFluid FLOWING_MOLTEN_CALORITE = registerFluid("flowing_molten_calorite",new MoltenCalorite.Flowing());


    public static void register(){
    }
    @Environment(EnvType.CLIENT)
    public static void registerRenders(){
        registerWaterRender(REFINED_OIL,FLOWING_REFINED_OIL,0x101703);
        registerWaterRender(GLUE,FLOWING_GLUE,0x4fab5b);
        registerWaterRender(SALT_WATER,FLOWING_SALT_WATER,0x5096E9);
        registerWaterRender(CHLORINE,FLOWING_CHLORINE,0xA4C947);
        registerRender(ALLOY_LAVA,FLOWING_ALLOY_LAVA,Identifier.of(Asplor.MOD_ID,"block/lava_still"),Identifier.of(Asplor.MOD_ID,"block/lava_flow"),0xD3D5EB);
        registerWaterRender(HYDROCHLORIC_ACID,FLOWING_HYDROCHLORIC_ACID,0xB3FBFF);
        registerWaterRender(CONCENTRATED_OIL,FLOWING_CONCENTRATED_OIL,0x101701);
        registerWaterRender(HEAVY_OIL, FLOWING_HEAVY_OIL, 0x6E2D07);
        registerWaterRender(LIGHT_OIL,FLOWING_LIGHT_OIL,0xF5B110);
        registerRender(CHEESE,FLOWING_CHEESE, Identifier.of(Asplor.MOD_ID,"block/cheese_still"),Identifier.of(Asplor.MOD_ID,"block/cheese_flow"),0xFFF1A0);




        registerMoltenMetalRender(MOLTEN_IRON,FLOWING_MOLTEN_IRON,0xFF442B);
        registerMoltenMetalRender(MOLTEN_GOLD,FLOWING_MOLTEN_GOLD,0xFFD648);
        registerMoltenMetalRender(MOLTEN_COPPER,FLOWING_MOLTEN_COPPER,0xFF5E44);
        registerMoltenMetalRender(MOLTEN_ZINC,FLOWING_MOLTEN_ZINC,0xA9B823);
        registerMoltenMetalRender(MOLTEN_BRASS,FLOWING_MOLTEN_BRASS,0xFFB925);
        registerMoltenMetalRender(MOLTEN_ALLOY,FLOWING_MOLTEN_ALLOY,0xD3D5EB);
        registerMoltenMetalRender(MOLTEN_DESH,FLOWING_MOLTEN_DESH,0xE8660F);
        registerMoltenMetalRender(IMPURE_MOLTEN_DESH,FLOWING_IMPURE_MOLTEN_DESH,0xD44B07);
        registerMoltenMetalRender(MOLTEN_OSTRUM,FLOWING_MOLTEN_OSTRUM,0x6E1A43);
        registerMoltenMetalRender(MOLTEN_CALORITE,FLOWING_MOLTEN_CALORITE,0xAA0943);



    }
    @Environment(EnvType.CLIENT)
    private static void registerWaterRender(FlowableFluid still,FlowableFluid flowing,int colorRGB){
        registerRender(still,flowing,WATER_STILL_TEXTURE,WATER_FLOW_TEXTURE,colorRGB);
    }




    @Environment(EnvType.CLIENT)
    private static void registerRender(FlowableFluid still, FlowableFluid flowing, Identifier stillPath,Identifier flowingPath, int color){
        FluidRenderHandlerRegistry.INSTANCE.register(still,flowing,new SimpleFluidRenderHandler(stillPath,flowingPath,flowingPath,color));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),still,flowing);
    }



    private static <T extends Fluid> T registerFluid(String id, T value) {
        return (T) Registry.register(Registries.FLUID, new Identifier(Asplor.MOD_ID,id), value);
    }

    private static void registerMoltenMetalRender(FlowableFluid still,FlowableFluid flowing,int color){
        registerRender(still,flowing,Identifier.of(Asplor.MOD_ID,"block/molten_metal_still"),Identifier.of(Asplor.MOD_ID,"block/molten_metal_flow"),color);
    }
}
