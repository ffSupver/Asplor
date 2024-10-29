package com.ffsupver.asplor.compat.rei.display;

import com.ffsupver.asplor.compat.rei.category.RefineryCategory;
import com.ffsupver.asplor.recipe.RefineryRecipe;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.fluid.Fluid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RefineryDisplay extends BasicDisplay {
    private RefineryRecipe recipe;
    public RefineryDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public RefineryDisplay(RefineryRecipe recipe){
        super(getInput(recipe),getOutputList(recipe));
        this.recipe =recipe;
    }

    public RefineryRecipe getRecipe() {
        return recipe;
    }

    private static List<EntryIngredient> getInput(RefineryRecipe recipe) {
        if(recipe==null) {
            System.out.println("null recipe");
            return Collections.emptyList();
        }
        FluidStack inputFluidStack = FluidStack.create(recipe.getInputFluid(),recipe.getInputFluidAmount());
        List<EntryIngredient> list = new ArrayList<>();
        list.add(EntryIngredients.of(inputFluidStack));

        return list;
    }

    private static List<EntryIngredient> getOutputList(RefineryRecipe recipe){
        if(recipe==null) {
            System.out.println("null recipe");
            return Collections.emptyList();
        }
        List<EntryIngredient> list = new ArrayList<>();
        for (Fluid outputFluid : recipe.getOutputFluids()){
            FluidStack outputFluidStack = FluidStack.create(outputFluid,recipe.getOutputFluidAmounts().get(outputFluid));
            list.add(EntryIngredients.of(outputFluid));
        }
        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return RefineryCategory.REFINERY;
    }
}
