package com.ffsupver.asplor.recipe;

import com.ffsupver.asplor.Asplor;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeType<DividerRecipe> DIVIDER_RECIPETYPE = registerRecipeType(DividerRecipe.Type.ID,DividerRecipe.Type.INSTANCE);
    public static final RecipeType<LiquidBlazeBurnerRecipe> LIQUID_BLAZE_BURNER_RECIPETYPE = registerRecipeType(LiquidBlazeBurnerRecipe.Type.ID,LiquidBlazeBurnerRecipe.Type.INSTANCE);

    public static final RecipeType<AssemblerRecipe> ASSEMBLER_RECIPETYPE = registerRecipeType(AssemblerRecipe.Type.ID, AssemblerRecipe.Type.INSTANCE);
    public static final RecipeType<TimeInjectorRecipe> TIME_INJECTOR_RECIPETYPE = registerRecipeType(TimeInjectorRecipe.Type.ID,TimeInjectorRecipe.Type.INSTANCE);

    public static final RecipeType<MeltRecipe> MELT_RECIPETYPE = registerRecipeType(MeltRecipe.Type.ID,MeltRecipe.Type.INSTANCE);

    public static final RecipeType<ElectrolyzerRecipe> ELECTROLYZER_RECIPETYPE = registerRecipeType(ElectrolyzerRecipe.Type.ID,ElectrolyzerRecipe.Type.INSTANCE);
    public static final RecipeType<RefineryRecipe> REFINERY_RECIPETYPE = registerRecipeType(RefineryRecipe.Type.ID,RefineryRecipe.Type.INSTANCE);

    public static final RecipeType<SmartProcessingRecipe> SMART_PROCESSING_RECIPETYPE = registerRecipeType(SmartProcessingRecipe.Type.ID,SmartProcessingRecipe.Type.INSTANCE);

private static <T extends Recipe<?>> RecipeType<T>  registerRecipeType(String name, RecipeType<T> recipeType){
    return Registry.register(Registries.RECIPE_TYPE,new Identifier(Asplor.MOD_ID,name),recipeType);
}
public static void registerRecipes(){
    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID,DividerRecipe.Serializer.ID),
            DividerRecipe.Serializer.INSTANCE);
    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID,LiquidBlazeBurnerRecipe.Serializer.ID),
            LiquidBlazeBurnerRecipe.Serializer.INSTANCE);
    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID, AssemblerRecipe.Serializer.ID),
            AssemblerRecipe.Serializer.INSTANCE);
    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID,TimeInjectorRecipe.Serializer.ID),
            TimeInjectorRecipe.Serializer.INSTANCE);
    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID,MeltRecipe.Serializer.ID),
            MeltRecipe.Serializer.INSTANCE);
    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID,ElectrolyzerRecipe.Serializer.ID),
            ElectrolyzerRecipe.Serializer.INSTANCE);
    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID,RefineryRecipe.Serializer.ID),
            RefineryRecipe.Serializer.INSTANCE);
    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID,SmartProcessingRecipe.Serializer.ID),
            SmartProcessingRecipe.Serializer.INSTANCE);



    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID,LargeMapCloningRecipe.ID),LargeMapCloningRecipe.SERIALIZER);
    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID,MysteriousPaperCloningRecipe.ID),MysteriousPaperCloningRecipe.SERIALIZER);
    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID,SchematicCraftingRecipe.ID),SchematicCraftingRecipe.SERIALIZER);
    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID,MeteoriteDivideRecipe.ID),MeteoriteDivideRecipe.SERIALIZER);
}


}
