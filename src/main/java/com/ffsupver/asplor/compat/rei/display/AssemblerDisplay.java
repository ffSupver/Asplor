package com.ffsupver.asplor.compat.rei.display;

import com.ffsupver.asplor.compat.rei.category.AssemblerCategory;
import com.ffsupver.asplor.recipe.AssemblerRecipe;
import com.ffsupver.asplor.recipe.DividerRecipe;
import com.simibubi.create.AllItems;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssemblerDisplay extends BasicDisplay {
    public AssemblerDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public AssemblerDisplay(AssemblerRecipe recipe){
        super(getInputList(recipe),List.of(EntryIngredients.of(recipe.getOutput(null))));
    }

    private static List<EntryIngredient> getInputList(AssemblerRecipe recipe) {
        if(recipe==null) {
            System.out.println("null recipe");
            return Collections.emptyList();
        }
        List<EntryIngredient> list = new ArrayList<>();
        for (Ingredient ingredient:recipe.getIngredients())
            list.add(EntryIngredients.ofIngredient(ingredient));
        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AssemblerCategory.ASSEMBLER;
    }
}
