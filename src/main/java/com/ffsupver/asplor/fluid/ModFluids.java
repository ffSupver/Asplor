package com.ffsupver.asplor.fluid;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.fluid.moltenMetal.*;
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



    public static void register(){
    }
    public static void registerRenders(){
        registerRender(REFINED_OIL,FLOWING_REFINED_OIL,Identifier.of("minecraft","block/water_still"),0x101703);
        registerRender(GLUE,FLOWING_GLUE,Identifier.of("minecraft","block/water_still"),0x4fab5b);
        registerRender(SALT_WATER,FLOWING_SALT_WATER,Identifier.of("minecraft","block/water_still"),0x5096E9);
        registerRender(CHLORINE,FLOWING_CHLORINE,Identifier.of("minecraft","block/water_still"),0xA4C947);
        registerRender(ALLOY_LAVA,FLOWING_ALLOY_LAVA,Identifier.of(Asplor.MOD_ID,"block/lava_still"),0xD3D5EB);

        registerMoltenMetalRender(MOLTEN_IRON,FLOWING_MOLTEN_IRON,0xFF442B);
        registerMoltenMetalRender(MOLTEN_GOLD,FLOWING_MOLTEN_GOLD,0xFFD648);
        registerMoltenMetalRender(MOLTEN_COPPER,FLOWING_MOLTEN_COPPER,0xFF5E44);
        registerMoltenMetalRender(MOLTEN_ZINC,FLOWING_MOLTEN_ZINC,0xA9B823);
        registerMoltenMetalRender(MOLTEN_BRASS,FLOWING_MOLTEN_BRASS,0xFFB925);
        registerMoltenMetalRender(MOLTEN_ALLOY,FLOWING_MOLTEN_ALLOY,0xD3D5EB);



    }
    private static void registerRender(FlowableFluid still,FlowableFluid flowing,Identifier path,int color){
        FluidRenderHandlerRegistry.INSTANCE.register(still,flowing,new SimpleFluidRenderHandler(path,path,color));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),still,flowing);
    }
    private static <T extends Fluid> T registerFluid(String id, T value) {
        return (T) Registry.register(Registries.FLUID, new Identifier(Asplor.MOD_ID,id), value);
    }

    private static void registerMoltenMetalRender(FlowableFluid still,FlowableFluid flowing,int color){
        registerRender(still,flowing,Identifier.of(Asplor.MOD_ID,"block/molten_metal_still"),color);
    }
}
