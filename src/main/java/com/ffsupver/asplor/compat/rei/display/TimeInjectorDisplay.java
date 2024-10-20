package com.ffsupver.asplor.compat.rei.display;

import com.ffsupver.asplor.compat.rei.category.TimeInjectorCategory;
import com.ffsupver.asplor.recipe.TimeInjectorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.fluid.Fluid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeInjectorDisplay extends BasicDisplay {
    private Fluid inputFluid;
    public TimeInjectorDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public TimeInjectorDisplay(TimeInjectorRecipe recipe){
        super(getInputList(recipe),List.of(EntryIngredients.of(recipe.getOutput(null))));
        this.inputFluid =recipe.getInputFluid();
    }

    public Fluid getInputFluid() {
        return inputFluid;
    }

    private static List<EntryIngredient> getInputList(TimeInjectorRecipe recipe) {
        if(recipe==null) {
            System.out.println("null recipe");
            return Collections.emptyList();
        }
        List<EntryIngredient> list = new ArrayList<>();
        list.add(EntryIngredients.of(recipe.getInputFluid()));
        list.add(EntryIngredients.ofIngredient(recipe.getIngredients().get(0)));

        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return TimeInjectorCategory.TIME_INJECTOR;
    }
}
