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

public class MeltRecipe implements Recipe<FluidInventory> {
    private final Identifier id;
    private final Fluid outputFluid;
    private final long outputAmount;
    private final List<Ingredient> recipeItems;

    private final int processTime;
    private final String heatType;

    public MeltRecipe(Identifier id, Fluid outputFluid, long outputAmount, List<Ingredient> recipeItems, int processTime, String heatType) {
        this.id = id;
        this.outputFluid = outputFluid;
        this.outputAmount = outputAmount;
        this.recipeItems = recipeItems;
        this.processTime = processTime;
        this.heatType = heatType;
    }

    @Override
    public boolean matches(FluidInventory inventory, World world) {
        return recipeItems.get(0).test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(FluidInventory inventory, DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public Fluid getOutputFluid() {
        return outputFluid;
    }

    public long getOutputAmount() {
        return outputAmount;
    }

    public int getProcessTime() {
        return processTime;
    }

    public String getHeatType() {
        return heatType;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return (DefaultedList<Ingredient>) recipeItems;
    }

    public static class Type implements RecipeType<MeltRecipe>{
        public static final Type INSTANCE=new Type();
        public static final String ID = "melt";
    }

    public static class Serializer implements RecipeSerializer<MeltRecipe>{
        public static final Serializer INSTANCE=new Serializer();
        public static final String ID = "melt";

        @Override
        public MeltRecipe read(Identifier id, JsonObject json) {

            JsonArray ingredients = JsonHelper.getArray(json,"ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(),Ingredient.EMPTY);

            for (int i=0;i<ingredients.size();i++){
                inputs.set(i,Ingredient.fromJson(ingredients.get(i)));
            }

            JsonObject outputFluidData = JsonHelper.getObject(json,"output");

            String fluidId = JsonHelper.getString(outputFluidData, "fluid");
            Fluid fluid = Registries.FLUID.get(new Identifier(fluidId));
            long fluidAmount = JsonHelper.getLong(outputFluidData,"amount");

            int processTime = JsonHelper.getInt(json,"process_time");
            String heatType = JsonHelper.getString(json,"heat_type");

            return new MeltRecipe(id,fluid,fluidAmount,inputs,processTime, heatType);
        }

        @Override
        public MeltRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(),Ingredient.EMPTY);
            for (int i= 0;i<inputs.size();i++){
                inputs.set(i,Ingredient.fromPacket(buf));
            }
            ItemStack output = buf.readItemStack();
            FluidVariant outputFluidVariant = FluidVariant.fromPacket(buf);
            long outputFluidAmount = buf.readLong();
            int processTime = buf.readInt();
            String heatType = buf.readString();

            return new MeltRecipe(id,outputFluidVariant.getFluid(),outputFluidAmount,inputs, processTime,heatType );
        }

        @Override
        public void write(PacketByteBuf buf, MeltRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ingredient:recipe.getIngredients()){
                ingredient.write(buf);
            }
            buf.writeItemStack(recipe.getOutput(null));
            FluidVariant outputFluidVariant = FluidVariant.of(recipe.getOutputFluid());
            long outputFluidAmount = recipe.getOutputAmount();
            int processTime = recipe.getProcessTime();
            String heatType = recipe.getHeatType();
            outputFluidVariant.toPacket(buf);
            buf.writeLong(outputFluidAmount);
            buf.writeInt(processTime);
            buf.writeString(heatType);

        }
    }

}
