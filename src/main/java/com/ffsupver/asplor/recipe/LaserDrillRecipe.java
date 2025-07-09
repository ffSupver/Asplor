package com.ffsupver.asplor.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class LaserDrillRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final List<ItemStack> outputs;
    private final RegistryKey<Biome> biome;

    public LaserDrillRecipe(Identifier id, List<ItemStack> outputs, RegistryKey<Biome> biome) {
        this.id = id;
        this.outputs = outputs;
        this.biome = biome;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        NbtCompound nbt = inventory.getStack(0).getOrCreateNbt();
        boolean biomeMatch = nbt.getString("biome").equals(biome.getValue().toString());
        return biomeMatch;
    }
    public static Inventory generatorTestInv(RegistryKey<Biome> biomeKey){
        ItemStack result = Items.PAPER.getDefaultStack();
        NbtCompound nbt = result.getOrCreateNbt();
        nbt.putString("biome",biomeKey.getValue().toString());
        result.setNbt(nbt);
        SimpleInventory inventory = new SimpleInventory(1);
        inventory.setStack(0,result);
        return inventory;
    }

    public List<ItemStack> getOutputs(){
        return outputs;
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
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

    public static class Type implements RecipeType<LaserDrillRecipe>{
        public static final Type INSTANCE = new Type();
        public static final String ID="laser_drill";
    }
    public static class Serializer implements RecipeSerializer<LaserDrillRecipe>{
        public static final String ID="laser_drill";
        public static final Serializer INSTANCE = new Serializer();
        @Override
        public LaserDrillRecipe read(Identifier id, JsonObject json) {
            String biome = JsonHelper.getString(json,"biome");
            RegistryKey<Biome> biomeKey = RegistryKey.of(RegistryKeys.BIOME,new Identifier(biome));
            JsonArray outputArray = JsonHelper.getArray(json,"output");
            ArrayList<ItemStack> outputs = new ArrayList<>();
            for (JsonElement o : outputArray){
               outputs.add(ShapedRecipe.outputFromJson((JsonObject) o));
            }
            return new LaserDrillRecipe(id,outputs,biomeKey);
        }

        @Override
        public LaserDrillRecipe read(Identifier id, PacketByteBuf buf) {
            RegistryKey<Biome> biome = RegistryKey.of(RegistryKeys.BIOME,buf.readIdentifier());
            int size = buf.readInt();
            ArrayList<ItemStack> outputs = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                outputs.add(buf.readItemStack());
            }
            return new LaserDrillRecipe(id,outputs,biome);
        }

        @Override
        public void write(PacketByteBuf buf, LaserDrillRecipe recipe) {
            buf.writeIdentifier(recipe.biome.getValue());
            buf.writeInt(recipe.outputs.size());
            for (ItemStack o : recipe.outputs){
                buf.writeItemStack(o);
            }
        }
    }

}
