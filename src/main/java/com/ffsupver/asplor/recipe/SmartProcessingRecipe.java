package com.ffsupver.asplor.recipe;

import com.ffsupver.asplor.block.smartMechanicalArm.ToolType;
import com.ffsupver.asplor.util.RenderUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;

public class SmartProcessingRecipe implements Recipe<Inventory> {
    public static final String PROCESSING_INDEX_KEY = "processing_index";
    private final Identifier id;
    private final Ingredient input;
    private final ItemStack output;
    private final ArrayList<ToolType> toolTypes;
    private final Item processItem;
    private final String schematic;

    public SmartProcessingRecipe(Identifier id, Ingredient input, ItemStack output, ArrayList<ToolType> toolTypes, Item processItem, String schematic) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.toolTypes = toolTypes;
        this.processItem = processItem;
        this.schematic = schematic;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return input.test(inventory.getStack(0)) || inventory.getStack(0).isOf(processItem);
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    public ArrayList<ToolType> getToolTypes() {
        return toolTypes;
    }

    public String getSchematic(){return schematic;}
    public boolean requireSchematic(){return schematic != null;}


    public ToolType getToolType(ItemStack itemStack){
        NbtCompound itemStackNbt = itemStack.getOrCreateNbt();
        if (!itemStack.isOf(processItem) || !itemStackNbt.contains(PROCESSING_INDEX_KEY,3)){
            return getToolTypes().get(0);
        }else {
            int index = itemStackNbt.getInt(PROCESSING_INDEX_KEY);
            if (index >= getToolTypes().size()){
                return getToolTypes().get(getToolTypes().size()-1);
            }else if(index < 0){
                return getToolTypes().get(0);
            }else {
                return getToolTypes().get(index);
            }
        }
    }

    public ItemStack process(ItemStack itemStack){
        NbtCompound itemStackNbt = itemStack.getOrCreateNbt();
        if (!itemStack.isOf(processItem) || !itemStackNbt.contains(PROCESSING_INDEX_KEY,3)){
            ItemStack output = new ItemStack(processItem,1);
            NbtCompound outputNbt = output.getOrCreateNbt();
            if (getToolTypes().size() > 1){
                outputNbt.putInt(PROCESSING_INDEX_KEY, 1);
                RenderUtil.addDescription(outputNbt,
                        Text.translatable("description.asplor.smart_processing.next").append(toolTypes.get(1).getRecipeText()),
                        Text.translatable("description.asplor.smart_processing.next").append(toolTypes.get(1).getRecipeText()));
                output.setNbt(outputNbt);
                return output;
            }else {
                return getOutput(null);
            }
        }else {
            int index = itemStackNbt.getInt(PROCESSING_INDEX_KEY);
            if (index < getToolTypes().size()-1){
                itemStackNbt.putInt(PROCESSING_INDEX_KEY,index+1);
                itemStack.setNbt(itemStackNbt);
                RenderUtil.addDescription(itemStackNbt,
                        Text.translatable("description.asplor.smart_processing.next").append(toolTypes.get(index+1).getRecipeText()),
                        Text.translatable("description.asplor.smart_processing.next").append(toolTypes.get(index).getRecipeText()));
                return itemStack;
            }else {
                return getOutput(null);
            }
        }
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output;
    }

    public Item getProcessItem() {
        return processItem;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients =DefaultedList.ofSize(1);
        ingredients.add(input);
        return ingredients;
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
    public static class Type implements RecipeType<SmartProcessingRecipe>{
        public static final Type INSTANCE = new Type();
        public static final String ID = "smart_processing";
    }
    public static class Serializer implements RecipeSerializer<SmartProcessingRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "smart_processing";

        @Override
        public SmartProcessingRecipe read(Identifier id, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(json.getAsJsonObject("ingredient"));
            ItemStack output = ShapedRecipe.outputFromJson(json.getAsJsonObject("output"));
            Item processItem = ShapedRecipe.getItem(json.getAsJsonObject("process_item"));
            String schematic = JsonHelper.getString(json,"schematic",null);
            JsonArray toolTypeDataList = JsonHelper.getArray(json,"tool_types");
            ArrayList<ToolType> toolTypes = new ArrayList<>();
            for (JsonElement toolTypeData :toolTypeDataList){
                Identifier toolTypeId = new Identifier(toolTypeData.getAsString());
                toolTypes.add(ToolType.getById(toolTypeId));
            }

            return new SmartProcessingRecipe(id,ingredient,output,toolTypes,processItem,schematic);
        }

        @Override
        public SmartProcessingRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient ingredient = Ingredient.fromPacket(buf);
            ItemStack output = buf.readItemStack();
            Item processItem = buf.readItemStack().getItem();

            boolean hasSchematic = buf.readBoolean();
            String schematic = null;
            if (hasSchematic){
                schematic = buf.readString();
            }

            int toolTypeCount = buf.readInt();
            ArrayList<ToolType> toolTypes = new ArrayList<>();
            for (int i = 0;i<toolTypeCount;i++){
                Identifier toolTypeId = buf.readIdentifier();
                toolTypes.add(ToolType.getById(toolTypeId));
            }

            return new SmartProcessingRecipe(id,ingredient,output,toolTypes,processItem,schematic);
        }

        @Override
        public void write(PacketByteBuf buf, SmartProcessingRecipe recipe) {
            recipe.getIngredients().get(0).write(buf);
            buf.writeItemStack(recipe.getOutput(null));
            buf.writeItemStack(recipe.getProcessItem().getDefaultStack());

            boolean hasSchematic = recipe.getSchematic() == null;
            buf.writeBoolean(hasSchematic);
            if (hasSchematic){
                buf.writeString(recipe.getSchematic());
            }

            buf.writeInt(recipe.getToolTypes().size());
            for (ToolType toolType : recipe.getToolTypes()){
                buf.writeIdentifier(toolType.getId());
            }
        }
    }
}
