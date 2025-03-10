package com.ffsupver.asplor.recipe;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.item.item.MeteoriteFragmentItem;
import com.ffsupver.asplor.networking.packet.worldAdder.PlanetCreatingData;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class MeteoriteDivideRecipe extends DividerRecipe {
    public final static RecipeSerializer<MeteoriteDivideRecipe> SERIALIZER = new MeteoriteDivideRecipeSerializer(MeteoriteDivideRecipe::new);
    public final static String ID = "meteorite_divide";
    public MeteoriteDivideRecipe(Identifier id) {
        super(id, ModItems.METEORITE_FRAGMENT.getDefaultStack(),List.of(Ingredient.ofItems(AllBlocks.METEORITE.asItem())));
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        return inventory.getStack(0).isOf(AllBlocks.METEORITE.asItem());
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        if (inventory.getStack(0).isOf(AllBlocks.METEORITE.asItem())){
            ItemStack result = ModItems.METEORITE_FRAGMENT.getDefaultStack();
            if (result.getItem() instanceof MeteoriteFragmentItem meteoriteFragmentItem){
                Random random = Random.create();
                result = meteoriteFragmentItem.putPlanetData(result, PlanetCreatingData.generateRandomPlanetData(random,10));
                return result;
            }
        }
        return super.craft(inventory, registryManager);
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return ModItems.METEORITE_FRAGMENT.getDefaultStack();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return DefaultedList.ofSize(1,Ingredient.ofStacks(AllBlocks.METEORITE.asItem().getDefaultStack()));
    }

    public static class MeteoriteDivideRecipeSerializer implements RecipeSerializer<MeteoriteDivideRecipe>{
        private final Factory factory;
        public MeteoriteDivideRecipeSerializer(Factory factory){
            this.factory = factory;
        }

        @Override
        public MeteoriteDivideRecipe read(Identifier id, JsonObject json) {
            return factory.create(id);
        }

        @Override
        public MeteoriteDivideRecipe read(Identifier id, PacketByteBuf buf) {
            return factory.create(id);
        }

        @Override
        public void write(PacketByteBuf buf, MeteoriteDivideRecipe recipe) {

        }

        @FunctionalInterface
        public interface Factory{
            MeteoriteDivideRecipe create(Identifier id);
        }
    }
}
