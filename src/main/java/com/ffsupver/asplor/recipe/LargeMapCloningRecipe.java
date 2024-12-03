package com.ffsupver.asplor.recipe;

import com.ffsupver.asplor.item.ModItems;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class LargeMapCloningRecipe extends SpecialCraftingRecipe {
    public final static RecipeSerializer<LargeMapCloningRecipe> SERIALIZER = new SpecialRecipeSerializer<>(LargeMapCloningRecipe::new);
    public final static String ID = "crafting_special_large_map_cloning";
    public LargeMapCloningRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;

        for(int j = 0; j < recipeInputInventory.size(); ++j) {
            ItemStack itemStack2 = recipeInputInventory.getStack(j);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.isOf(ModItems.LARGE_MAP)) {
                    if (!itemStack.isEmpty()) {
                        return false;
                    }

                    itemStack = itemStack2;
                } else {
                    if (!itemStack2.isOf(ModItems.EMPTY_LARGE_MAP)) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !itemStack.isEmpty() && i > 0;
    }

    @Override
    public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager registryManager) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;

        for(int j = 0; j < recipeInputInventory.size(); ++j) {
            ItemStack itemStack2 = recipeInputInventory.getStack(j);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.isOf(ModItems.LARGE_MAP)) {
                    if (!itemStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    itemStack = itemStack2;
                } else {
                    if (!itemStack2.isOf(ModItems.EMPTY_LARGE_MAP)) {
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }

        if (!itemStack.isEmpty() && i >= 1) {
            return itemStack.copyWithCount(i + 1);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
