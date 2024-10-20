package com.ffsupver.asplor.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class LiquidBlazeBurnerRecipe implements Recipe<FluidInventory> {
    private final Identifier id;
    private final int output;
    private final FluidVariant inputFluid;
    private final long requiredAmount;
    private final String heatType;

    public LiquidBlazeBurnerRecipe(Identifier id, FluidVariant inputFluid, long requiredAmount,int output ,String heatType) {
        this.id = id;
        this.output = output;
        this.inputFluid = inputFluid;
        this.requiredAmount = requiredAmount;
        this.heatType=heatType;
    }

    @Override
    public boolean matches(FluidInventory inventory, World world) {
        if (world.isClient()) {
            return false;
        }

//        System.out.println("Recipe mache"+inventory+"  "+inventory.canExtractFluid(inputFluid, requiredAmount)+
//                " need  "+inputFluid+" "+requiredAmount);

        // 检查是否有足够的液体
        return inventory.canExtractFluid(0,inputFluid, requiredAmount);
    }

    private ItemStack getOutputItemStack(){
        Item heatTypeItem = switch (heatType) {
            case "normal" -> Items.BLAZE_POWDER;
            case "super" -> Items.BLAZE_ROD;
            default -> Items.BLAZE_POWDER;
        };
        return new ItemStack(heatTypeItem,output);
    }

    @Override
    public ItemStack craft(FluidInventory inventory, DynamicRegistryManager registryManager) {
        // 消耗液体并输出整数（物品表示）
        inventory.extractFluid(0,requiredAmount);
       return getOutputItemStack(); // 返回整数作为物品堆的数量
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }
    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return DefaultedList.of(); // 没有固体物品成分
    }

    public FluidVariant getInputFluid() {
        return inputFluid;
    }

    public long getRequiredAmount() {
        return requiredAmount;
    }
    public String getHeatType(){
        return heatType;
}
    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return getOutputItemStack();
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
    public static class Type implements RecipeType<LiquidBlazeBurnerRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "liquid_blaze_burner";
    }

    public static class Serializer implements RecipeSerializer<LiquidBlazeBurnerRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "liquid_blaze_burner";

        public static FluidVariant fluidVariantFromJson(JsonObject json) {
            // 从 JSON 获取流体 ID
            String fluidId = JsonHelper.getString(json, "fluid");
            Fluid fluid = Registries.FLUID.get(new Identifier(fluidId));

            // 检查是否有 NBT 数据
            NbtCompound nbt = null;
            if (json.has("nbt")) {
                try {
                    nbt = StringNbtReader.parse(JsonHelper.getString(json, "nbt"));
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            return FluidVariant.of(fluid, nbt);
        }
        @Override
        public LiquidBlazeBurnerRecipe read(Identifier id, JsonObject json) {
            // 解析 JSON 中的流体和输出整数
            FluidVariant inputFluid = fluidVariantFromJson(JsonHelper.getObject(json, "fluid"));
            long requiredAmount = JsonHelper.getLong(json, "amount");
            int output = JsonHelper.getInt(json, "output");
            String heatType = JsonHelper.getString(json,"heat_type");
            return new LiquidBlazeBurnerRecipe(id, inputFluid, requiredAmount, output,heatType);
        }

        @Override
        public LiquidBlazeBurnerRecipe read(Identifier id, PacketByteBuf buf) {
            // 从网络包中读取数据
            FluidVariant inputFluid = FluidVariant.fromPacket(buf);
            long requiredAmount = buf.readLong();
            int output = buf.readInt();
            String heatType = buf.readString();
            return new LiquidBlazeBurnerRecipe(id, inputFluid, requiredAmount, output,heatType);
        }

        @Override
        public void write(PacketByteBuf buf, LiquidBlazeBurnerRecipe recipe) {
            // 写入配方数据到网络包
            recipe.inputFluid.toPacket(buf);
            buf.writeLong(recipe.requiredAmount);
            buf.writeInt(recipe.output);
            buf.writeString(recipe.heatType);
        }
    }
}

