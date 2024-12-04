package com.ffsupver.asplor.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
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

public class ElectrolyzerRecipe implements Recipe<FluidInventory> {
    private final Identifier id;
    private final Fluid outputFluidA;
    private final long outputAmountA;
    private final Fluid outputFluidB;
    private final long outputAmountB;
    private final FluidInventory recipeFluids;



    public ElectrolyzerRecipe(Identifier id, FluidInventory recipeFluids, Fluid outputFluidA, long outputAmountA, Fluid outputFluidB, long outputAmountB) {
        this.id = id;
        this.outputFluidA = outputFluidA;
        this.outputAmountA = outputAmountA;
        this.recipeFluids =recipeFluids;
        this.outputFluidB = outputFluidB;
        this.outputAmountB = outputAmountB;
    }

    @Override
    public boolean matches(FluidInventory inventory, World world) {
        return fluidMatches(inventory,recipeFluids,0);
    }

    private boolean fluidMatches(FluidInventory test,FluidInventory recipe ,int slot){
        return test.getStoredFluid(slot)==recipe.getStoredFluid(slot) && test.getStoredAmount(slot) >= recipe.getStoredAmount(slot);
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

    public Fluid getOutputFluidA() {
        return outputFluidA;
    }

    public long getOutputAmountA() {
        return outputAmountA;
    }

    public Fluid getOutputFluidB() {
        return outputFluidB;
    }

    public long getOutputAmountB() {
        return outputAmountB;
    }

    public FluidInventory getRecipeFluids() {
        return recipeFluids;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }



    public static class Type implements RecipeType<ElectrolyzerRecipe>{
        public static final Type INSTANCE=new Type();
        public static final String ID = "electrolyzer";
    }

    public static class Serializer implements RecipeSerializer<ElectrolyzerRecipe>{
        public static final Serializer INSTANCE=new Serializer();
        public static final String ID = "electrolyzer";

        @Override
        public ElectrolyzerRecipe read(Identifier id, JsonObject json) {

            JsonObject ingredient = JsonHelper.getObject(json,"ingredient");
            FluidInventory inputs = new FluidInventory(0,2);

            String inputFluidId = JsonHelper.getString(ingredient, "fluid");
            Fluid inputFluid = Registries.FLUID.get(new Identifier(inputFluidId));
            long inputFluidAmount = JsonHelper.getLong(ingredient,"amount");
            inputs.insertFluid(0,FluidVariant.of(inputFluid),inputFluidAmount);


            JsonArray outputFluidDatas = JsonHelper.getArray(json,"outputs");


            String fluidIdA = JsonHelper.getString(outputFluidDatas.get(0).getAsJsonObject(), "fluid");
            Fluid fluidA = Registries.FLUID.get(new Identifier(fluidIdA));
            long fluidAmountA = JsonHelper.getLong(outputFluidDatas.get(0).getAsJsonObject(), "amount");

            String fluidIdB = JsonHelper.getString(outputFluidDatas.get(1).getAsJsonObject(), "fluid");
            Fluid fluidB = Registries.FLUID.get(new Identifier(fluidIdB));
            long fluidAmountB = JsonHelper.getLong(outputFluidDatas.get(1).getAsJsonObject(), "amount");


            return new ElectrolyzerRecipe(id,inputs,fluidA,fluidAmountA,fluidB,fluidAmountB);
        }

        @Override
        public ElectrolyzerRecipe read(Identifier id, PacketByteBuf buf) {
            // 读取输入流体
            FluidInventory inputs = new FluidInventory(0, 2);

            // 读取输入流体的类型和数量
            Fluid inputFluid = Registries.FLUID.get(buf.readIdentifier());
            long inputFluidAmount = buf.readLong();
            inputs.insertFluid(0, FluidVariant.of(inputFluid), inputFluidAmount);

            // 读取输出流体A的类型和数量
            Fluid fluidA = Registries.FLUID.get(buf.readIdentifier());
            long fluidAmountA = buf.readLong();

            // 读取输出流体B的类型和数量
            Fluid fluidB = Registries.FLUID.get(buf.readIdentifier());
            long fluidAmountB = buf.readLong();

            // 创建并返回 ElectrolyzerRecipe
            return new ElectrolyzerRecipe(id, inputs, fluidA, fluidAmountA, fluidB, fluidAmountB);
        }

        @Override
        public void write(PacketByteBuf buf, ElectrolyzerRecipe recipe) {
            // 写入输入流体的类型和数量
            FluidVariant inputFluidVariant = recipe.getRecipeFluids().getStoredFluid(0);
            buf.writeIdentifier(Registries.FLUID.getId(inputFluidVariant.getFluid()));
            buf.writeLong(recipe.getRecipeFluids().getStoredAmount(0));

            // 写入输出流体A的类型和数量
            buf.writeIdentifier(Registries.FLUID.getId(recipe.getOutputFluidA()));
            buf.writeLong(recipe.getOutputAmountA());

            // 写入输出流体B的类型和数量
            buf.writeIdentifier(Registries.FLUID.getId(recipe.getOutputFluidB()));
            buf.writeLong(recipe.getOutputAmountB());

        }
    }

}
