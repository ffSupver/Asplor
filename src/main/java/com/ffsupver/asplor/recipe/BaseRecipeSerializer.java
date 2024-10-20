package com.ffsupver.asplor.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;

public  interface  BaseRecipeSerializer<T extends Recipe<?>> extends RecipeSerializer<T> {
    String getId();
    BaseRecipeSerializer<T> getInstance();
}
