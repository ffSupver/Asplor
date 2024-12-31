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

import static com.ffsupver.asplor.item.item.SchematicItem.getSchematicFromItem;
import static com.ffsupver.asplor.item.item.SchematicItem.getSchematicItem;

public class SchematicCraftingRecipe extends SpecialCraftingRecipe {
    public final static RecipeSerializer<SchematicCraftingRecipe> SERIALIZER = new SpecialRecipeSerializer<>(SchematicCraftingRecipe::new);
    public final static String ID = "crafting_special_schematic_crafting";
    public SchematicCraftingRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        ItemStack itemStack = inventory.getStack(0);
        String schematic = getSchematicFromItem(itemStack);
        if (!itemStack.isOf(ModItems.SCHEMATIC_SHARD) || itemStack.isEmpty() || schematic == null){
            return false;
        }
        int count = 1;
        for (int i = 1 ;i < inventory.size();i++){
            ItemStack testStack = inventory.getStack(i);
            if (!testStack.isOf(ModItems.SCHEMATIC_SHARD) || !schematic.equals(getSchematicFromItem(testStack))){
                return false;
            }
            count++;
        }
        return count >= 9;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack itemStack = inventory.getStack(0);
        String schematic = getSchematicFromItem(itemStack);
        if (!itemStack.isOf(ModItems.SCHEMATIC_SHARD) || itemStack.isEmpty() || schematic == null){
            return ItemStack.EMPTY;
        }
        int count = 1;
        for (int i = 1 ;i < inventory.size();i++){
            ItemStack testStack = inventory.getStack(i);
            if (!testStack.isOf(ModItems.SCHEMATIC_SHARD) || !schematic.equals(getSchematicFromItem(testStack))){
                return ItemStack.EMPTY;
            }
            count++;
        }
        return count >= 9 ? getSchematicItem(schematic) : ItemStack.EMPTY;
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
