package com.ffsupver.asplor.compat.rei;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.AllRecipeTypes;
import com.ffsupver.asplor.block.alloy_mechanical_press.AlloyPressingRecipe;
import com.ffsupver.asplor.compat.rei.category.*;
import com.ffsupver.asplor.compat.rei.display.*;
import com.ffsupver.asplor.recipe.*;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;


public class AsplorREIClient implements REIClientPlugin {


    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(DividerRecipe.class, ModRecipes.DIVIDER_RECIPETYPE, DividerDisplay::new);

        // 注册 ShapelessRecipe，筛选出符合 MISC 分类的配方,并加入DividerCategory
        registry.registerRecipeFiller(ShapelessRecipe.class, RecipeType.CRAFTING, recipe -> {
            if (recipe instanceof ShapelessRecipe && isMiscCategory(recipe)&&recipe.getIngredients().size()==1) {
                return new DividerDisplay(recipe); // 用 DividerDisplay 显示
            }
            return null;
        });


        registry.registerRecipeFiller(AlloyPressingRecipe.class, (RecipeType<? super AlloyPressingRecipe>) AllRecipeTypes.ALLOY_PRESSING.getType(), AlloyPressingDisplay::new);
        registry.registerRecipeFiller(LiquidBlazeBurnerRecipe.class,ModRecipes.LIQUID_BLAZE_BURNER_RECIPETYPE, LiquidBlazeBurnerDisplay::new);
        registry.registerRecipeFiller(AssemblerRecipe.class,ModRecipes.ASSEMBLER_RECIPETYPE, AssemblerDisplay::new);
        registry.registerRecipeFiller(TimeInjectorRecipe.class,ModRecipes.TIME_INJECTOR_RECIPETYPE, TimeInjectorDisplay::new);
        registry.registerRecipeFiller(MeltRecipe.class,ModRecipes.MELT_RECIPETYPE, MeltingFurnaceDisplay::new);
        registry.registerRecipeFiller(ElectrolyzerRecipe.class,ModRecipes.ELECTROLYZER_RECIPETYPE,ElectrolyzeDisplay::new);
        registry.registerRecipeFiller(RefineryRecipe.class,ModRecipes.REFINERY_RECIPETYPE,RefineryDisplay::new);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new DividerCategory());
        registry.add(new AlloyPressingCategory());
        registry.add(new LiquidBlazeBurnerCategory());
        registry.add(new AssemblerCategory());
        registry.add(new TimeInjectorCategory());
        registry.add(new MeltingFurnaceCategory());
        registry.add(new ElectrolyzeCategory());
        registry.add(new RefineryCategory());

        registry.addWorkstations(DividerCategory.DIVIDER, EntryStacks.of(AllBlocks.DIVIDER));
        registry.addWorkstations(CategoryIdentifier.of(Create.ID,"pressing"),EntryStacks.of(AllBlocks.ALLOY_MECHANICAL_PRESS));
        registry.addWorkstations(AlloyPressingCategory.ALLOY_PRESSING,EntryStacks.of(AllBlocks.ALLOY_MECHANICAL_PRESS));
        registry.addWorkstations(LiquidBlazeBurnerCategory.LIQUID_BLAZE_BURNER,EntryStacks.of(AllBlocks.LIQUID_BLAZE_BURNER));
        registry.addWorkstations(AssemblerCategory.ASSEMBLER,EntryStacks.of(AllBlocks.ASSEMBLER));
        registry.addWorkstations(AssemblerCategory.ASSEMBLER,EntryStacks.of(AllItems.SUPER_GLUE));
        registry.addWorkstations(TimeInjectorCategory.TIME_INJECTOR, EntryStacks.of(AllBlocks.TIME_INJECTOR));
        registry.addWorkstations(MeltingFurnaceCategory.MELT, EntryStacks.of(AllBlocks.MELTING_FURNACE));
        registry.addWorkstations(ElectrolyzeCategory.ELECTROLYZE,EntryStacks.of(AllBlocks.ELECTROLYZER));
        registry.addWorkstations(RefineryCategory.REFINERY,EntryStacks.of(AllBlocks.REFINERY_CONTROLLER));



    }


    @Override
    public void registerScreens(ScreenRegistry registry) {
        REIClientPlugin.super.registerScreens(registry);
    }


    private boolean isMiscCategory(ShapelessRecipe recipe) {
        return recipe.getCategory().equals(CraftingRecipeCategory.MISC);
    }

}
