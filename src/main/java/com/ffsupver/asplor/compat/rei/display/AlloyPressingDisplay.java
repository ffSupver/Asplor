package com.ffsupver.asplor.compat.rei.display;

import com.ffsupver.asplor.block.alloyMechanicalPress.AlloyPressingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ffsupver.asplor.compat.rei.category.AlloyPressingCategory.ALLOY_PRESSING;

public class AlloyPressingDisplay extends BasicDisplay {
//    public AlloyPressingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
//        super(inputs, outputs);
//    }

    public AlloyPressingDisplay(AlloyPressingRecipe alloyPressingRecipe) {
        super(
//                getInputList(alloyPressingRecipe)
                convertIngredientsToEntryIngredients(alloyPressingRecipe.getIngredients())
                ,List.of(EntryIngredients.of(alloyPressingRecipe.getOutput(null)))
                );

    }


    private static List<EntryIngredient> convertIngredientsToEntryIngredients(List<Ingredient> ingredients) {
        return ingredients.stream()
                .map(ingredient -> EntryIngredients.ofItemStacks(Arrays.asList(ingredient.getMatchingStacks())))
                .toList();
    }


    private static List<EntryIngredient> getInputList(AlloyPressingRecipe recipe) {
        if(recipe==null) {
            System.out.println("null recipe");
            return Collections.emptyList();
        }
        List<EntryIngredient> list = new ArrayList<>();
        list.add(EntryIngredients.ofIngredient(recipe.getIngredients().get(0)));
        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ALLOY_PRESSING;
    }
}
