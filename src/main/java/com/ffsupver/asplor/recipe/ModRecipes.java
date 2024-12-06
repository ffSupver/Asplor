package com.ffsupver.asplor.recipe;

import com.ffsupver.asplor.Asplor;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeType<DividerRecipe> DIVIDER_RECIPETYPE =
            Registry.register(Registries.RECIPE_TYPE,
                    new Identifier(Asplor.MOD_ID,DividerRecipe.Type.ID),
            DividerRecipe.Type.INSTANCE);
    public static final RecipeType<LiquidBlazeBurnerRecipe> LIQUID_BLAZE_BURNER_RECIPETYPE =
            Registry.register(Registries.RECIPE_TYPE,
            new Identifier(Asplor.MOD_ID,LiquidBlazeBurnerRecipe.Type.ID),
            LiquidBlazeBurnerRecipe.Type.INSTANCE);

    public static final RecipeType<AssemblerRecipe> ASSEMBLER_RECIPETYPE =
            Registry.register(Registries.RECIPE_TYPE,
                    new Identifier(Asplor.MOD_ID,AssemblerRecipe.Type.ID),
                    AssemblerRecipe.Type.INSTANCE);
    public static final RecipeType<TimeInjectorRecipe> TIME_INJECTOR_RECIPETYPE =
            Registry.register(Registries.RECIPE_TYPE,
                    new Identifier(Asplor.MOD_ID,TimeInjectorRecipe.Type.ID),
                    TimeInjectorRecipe.Type.INSTANCE);

    public static final RecipeType<MeltRecipe> MELT_RECIPETYPE =
            Registry.register(Registries.RECIPE_TYPE,
                    new Identifier(Asplor.MOD_ID,MeltRecipe.Type.ID),
                    MeltRecipe.Type.INSTANCE);

    public static final RecipeType<ElectrolyzerRecipe> ELECTROLYZER_RECIPETYPE =
            Registry.register(Registries.RECIPE_TYPE,
                    new Identifier(Asplor.MOD_ID,ElectrolyzerRecipe.Type.ID),
                    ElectrolyzerRecipe.Type.INSTANCE);
    public static final RecipeType<RefineryRecipe> REFINERY_RECIPETYPE =
            Registry.register(Registries.RECIPE_TYPE,
                    new Identifier(Asplor.MOD_ID,RefineryRecipe.Type.ID),
                    RefineryRecipe.Type.INSTANCE);

    public static final RecipeType<SmartProcessingRecipe> SMART_PROCESSING_RECIPETYPE =
            Registry.register(Registries.RECIPE_TYPE,
                    new Identifier(Asplor.MOD_ID,SmartProcessingRecipe.Type.ID),
                    SmartProcessingRecipe.Type.INSTANCE);

private static <T extends Inventory> RecipeType<Recipe<T>>  registerRecipeType(String name,RecipeType<Recipe<T>> recipeType){
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
}

private static <T extends Inventory> void  registerRecipeSerializer(BaseRecipeSerializer<Recipe<T>> recipeRecipeSerializer){
    Registry.register(Registries.RECIPE_SERIALIZER,new Identifier(Asplor.MOD_ID,recipeRecipeSerializer.getId()),recipeRecipeSerializer.getInstance());
}
}
