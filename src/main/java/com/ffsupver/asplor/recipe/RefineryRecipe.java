package com.ffsupver.asplor.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RefineryRecipe implements Recipe<FluidInventory> {
    private final Fluid inputFluid;
    private final Long inputFluidAmount;
    private final ArrayList<Fluid> outputFluids;
    private final Map<Fluid,Long> outputFluidAmounts;
    private final Identifier id;

    public RefineryRecipe(Identifier id,Fluid inputFluid, Long inputFluidAmount, ArrayList<Fluid> outputFluids, Map<Fluid, Long> outputFluidAmounts) {
        this.id = id;
        this.inputFluid = inputFluid;
        this.inputFluidAmount = inputFluidAmount;
        this.outputFluids = outputFluids;
        this.outputFluidAmounts = outputFluidAmounts;
    }

    @Override
    public boolean matches(FluidInventory inventory, World world) {
        return inventory.getStoredFluid(0).getFluid().equals(inputFluid) && inventory.getStoredAmount(0) >= inputFluidAmount;
    }

    @Override
    public ItemStack craft(FluidInventory inventory, DynamicRegistryManager registryManager) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    public Fluid getInputFluid() {
        return inputFluid;
    }

    public Long getInputFluidAmount() {
        return inputFluidAmount;
    }

    public ArrayList<Fluid> getOutputFluids() {
        return outputFluids;
    }

    public Map<Fluid, Long> getOutputFluidAmounts() {
        return outputFluidAmounts;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return null;
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
    public static class Type implements RecipeType<RefineryRecipe>{
        public static final Type INSTANCE=new Type();
        public static final String ID = "refinery";
    }
    public static class Serializer implements RecipeSerializer<RefineryRecipe>{
        public static final Serializer INSTANCE=new Serializer();
        public static final String ID = "refinery";

        @Override
        public RefineryRecipe read(Identifier id, JsonObject json) {

            JsonObject ingredient = JsonHelper.getObject(json,"input");
            Fluid input = Registries.FLUID.get(new Identifier(JsonHelper.getString(ingredient, "fluid")));
            Long inputAmount = JsonHelper.getLong(ingredient,"amount");


            JsonArray outputData = JsonHelper.getArray(json,"outputs");
            ArrayList<Fluid> outputs = new ArrayList<>();
            Map<Fluid,Long> outputAmounts = new HashMap<>();
            for (JsonElement element : outputData){
                JsonObject outputJson = element.getAsJsonObject();
                Fluid outputFluid = Registries.FLUID.get(new Identifier(JsonHelper.getString(outputJson, "fluid")));
                outputs.add(outputFluid);
                outputAmounts.put(outputFluid, JsonHelper.getLong(outputJson,"amount"));
            }


            return new RefineryRecipe(id,input,inputAmount,outputs,outputAmounts);
        }

        @Override
        public RefineryRecipe read(Identifier id, PacketByteBuf buf) {
            // 读取输入
            Fluid input = Registries.FLUID.get(buf.readIdentifier());
            long inputAmount = buf.readLong();

            // 读取输出流体及其对应的量
            int outputCount = buf.readInt();
            ArrayList<Fluid> outputs = new ArrayList<>();
            Map<Fluid, Long> outputAmounts = new HashMap<>();

            for (int i = 0; i < outputCount; i++) {
                Fluid outputFluid = Registries.FLUID.get(buf.readIdentifier());
                long outputAmount = buf.readLong();
                outputs.add(outputFluid);
                outputAmounts.put(outputFluid, outputAmount);
            }

            return new RefineryRecipe(id, input, inputAmount, outputs, outputAmounts);
        }

        @Override
        public void write(PacketByteBuf buf, RefineryRecipe recipe) {
            // 写入输入
            buf.writeIdentifier(Registries.FLUID.getId(recipe.getInputFluid()));
            buf.writeLong(recipe.getInputFluidAmount());

            // 写入输出流体及其对应的量
            buf.writeInt(recipe.getOutputFluids().size());
            for (Fluid outputFluid : recipe.getOutputFluids()) {
                buf.writeIdentifier(Registries.FLUID.getId(outputFluid));
                buf.writeLong(recipe.getOutputFluidAmounts().get(outputFluid));
            }
        }
    }
}
