package com.ffsupver.asplor.compat.rei.display;

import com.ffsupver.asplor.recipe.MeltRecipe;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ffsupver.asplor.compat.rei.category.MeltingFurnaceCategory.MELT;

public class MeltingFurnaceDisplay extends BasicDisplay {
    private String heatType;
    private MeltRecipe recipe;

    public MeltingFurnaceDisplay(MeltRecipe meltRecipe) {
        super(
                getInputList(meltRecipe)
                ,List.of(EntryIngredients.of(FluidStack.create(meltRecipe.getOutputFluid(),meltRecipe.getOutputAmount()/81)))
                );
        this.recipe=meltRecipe;
        this.heatType = meltRecipe.getHeatType();

    }

    public MeltRecipe getRecipe() {
        return recipe;
    }

    public String getHeatType() {
        return heatType;
    }

    private static List<EntryIngredient> getInputList(MeltRecipe recipe) {
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
        return MELT;
    }
}
