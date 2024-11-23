package com.ffsupver.asplor.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class TimeInjectorRecipe implements Recipe<FluidInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final Fluid inputFluid;
    private final List<Ingredient> recipeItems;


    public TimeInjectorRecipe(Identifier id, ItemStack output, Fluid inputFluid, List<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.inputFluid = inputFluid;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(FluidInventory inventory, World world) {
        return inventory.getStoredFluid(0).getFluid().equals(inputFluid)&&recipeItems.get(0).test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(FluidInventory inventory, DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output;
    }

    public Fluid getInputFluid() {
        return inputFluid;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return (DefaultedList<Ingredient>)recipeItems;
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
    public static class Type implements RecipeType<TimeInjectorRecipe>{
        public static final Type INSTANCE=new Type();
        public static final String ID = "time_injector";
    }
    public static class Serializer implements RecipeSerializer<TimeInjectorRecipe>{
        public static final Serializer INSTANCE=new Serializer();
        public static final String ID = "time_injector";

        @Override
        public TimeInjectorRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json,"output"));

            JsonArray ingredients = JsonHelper.getArray(json,"ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(),Ingredient.EMPTY);

            for (int i=0;i<ingredients.size();i++){
                inputs.set(i,Ingredient.fromJson(ingredients.get(i)));
            }

            String fluidId = JsonHelper.getString(JsonHelper.getObject(json,"fluid"), "fluid");
            Fluid fluid = Registries.FLUID.get(new Identifier(fluidId));


            return new TimeInjectorRecipe(id,output,fluid,inputs);
        }

        @Override
        public TimeInjectorRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(),Ingredient.EMPTY);
            for (int i= 0;i<inputs.size();i++){
                inputs.set(i,Ingredient.fromPacket(buf));
            }
            ItemStack output = buf.readItemStack();
            FluidVariant inputFluidVariant = FluidVariant.fromPacket(buf);

            return new TimeInjectorRecipe(id,output,inputFluidVariant.getFluid(),inputs);
        }

        @Override
        public void write(PacketByteBuf buf, TimeInjectorRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ingredient:recipe.getIngredients()){
                ingredient.write(buf);
            }
            buf.writeItemStack(recipe.getOutput(null));
            FluidVariant inputFluidVariant = FluidVariant.of(recipe.inputFluid);
            inputFluidVariant.toPacket(buf);
        }
    }
}
