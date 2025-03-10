package com.ffsupver.asplor.compat.rei.display;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.compat.rei.category.DividerCategory;
import com.ffsupver.asplor.recipe.DividerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DividerDisplay extends BasicDisplay {
    public DividerDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public DividerDisplay(DividerRecipe recipe){
        super(getInputList(recipe),List.of(EntryIngredients.of(recipe.getOutput(null))));
    }

    public DividerDisplay(ShapelessRecipe shapelessRecipe) {
        super(getInputList(shapelessRecipe), List.of(EntryIngredients.of(shapelessRecipe.getOutput(null))));
    }





    private static List<EntryIngredient> getInputList(DividerRecipe recipe) {
        if(recipe==null) {
            Asplor.LOGGER.error("null recipe");
            return Collections.emptyList();
        }
        List<EntryIngredient> list = new ArrayList<>();
        list.add(EntryIngredients.ofIngredient(recipe.getIngredients().get(0)));
        return list;
    }

    private static List<EntryIngredient> getInputList(ShapelessRecipe recipe) {
        if (recipe == null) {
            System.out.println("null ShapelessRecipe");
            return Collections.emptyList();
        }
        List<EntryIngredient> list = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            list.add(EntryIngredients.ofIngredient(ingredient));
        }
        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return DividerCategory.DIVIDER;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return super.getDisplayLocation();
    }


}
