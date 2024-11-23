package com.ffsupver.asplor.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class AssemblerRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final List<Ingredient> recipeItems;

    public AssemblerRecipe(Identifier id, ItemStack output, List<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient()){
            return false;
        }
        return
                recipeItems.get(0).test(inventory.getStack(0))
                &&recipeItems.get(1).test(inventory.getStack(1));
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return (DefaultedList<Ingredient>) recipeItems;
    }

    public List<Ingredient> getRecipeItems() {
        return recipeItems;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<AssemblerRecipe>{
        public static final Type INSTANCE = new Type();
        public static final String ID = "assembler";
    }

    public static class Serializer implements RecipeSerializer<AssemblerRecipe>{
        public static final Serializer INSTANCE=new Serializer();
        public static final String ID="assembler";
        @Override
        public AssemblerRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json,"output"));

            JsonArray ingredients = JsonHelper.getArray(json,"ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(),Ingredient.EMPTY);
            for (int i=0;i<ingredients.size();i++){
                inputs.set(i,Ingredient.fromJson(ingredients.get(i)));
            }
            return new AssemblerRecipe(id,output,inputs);
        }

        @Override
        public AssemblerRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(),Ingredient.EMPTY);
            for (int i= 0;i<inputs.size();i++){
                inputs.set(i,Ingredient.fromPacket(buf));
            }
            ItemStack output = buf.readItemStack();
            return new  AssemblerRecipe(id,output,inputs);
        }

        @Override
        public void write(PacketByteBuf buf, AssemblerRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ingredient:recipe.getIngredients()){
                ingredient.write(buf);
            }
            buf.writeItemStack(recipe.getOutput(null));
        }




    }
}
