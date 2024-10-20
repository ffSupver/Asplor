package com.ffsupver.asplor.block.divider;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;

import java.util.Arrays;
import java.util.List;

public class ShapelessRecipeTesterInventory implements RecipeInputInventory {
    private final List<ItemStack> inputStacks;

    public ShapelessRecipeTesterInventory(ItemStack... stacks) {
        this.inputStacks = Arrays.asList(stacks);
    }

    @Override
    public int getWidth() {
        return 1; // 这里假设你的输入是单列的
    }

    @Override
    public int getHeight() {
        return inputStacks.size(); // 根据输入物品数量确定高度
    }

    @Override
    public List<ItemStack> getInputStacks() {
        return inputStacks;
    }

    @Override
    public int size() {
        return inputStacks.size();
    }

    @Override
    public boolean isEmpty() {
        return inputStacks.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return inputStacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = inputStacks.get(slot);
        return stack.split(amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = inputStacks.get(slot);
        inputStacks.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inputStacks.set(slot, stack);
    }

    @Override
    public void markDirty() {
        // 如果需要额外处理标记脏数据，可以在此实现
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true; // 控制玩家是否可以使用此配方输入
    }

    @Override
    public void clear() {
        inputStacks.clear();
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {

    }
}
