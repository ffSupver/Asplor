package com.ffsupver.asplor.compat.rei.display;

import com.ffsupver.asplor.compat.rei.category.ElectrolyzeCategory;
import com.ffsupver.asplor.recipe.ElectrolyzerRecipe;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ElectrolyzeDisplay extends BasicDisplay {
    public FluidStack inputFluid;
    public List<FluidStack> outputFluids;


    public ElectrolyzeDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public ElectrolyzeDisplay(ElectrolyzerRecipe recipe){
        super(List.of(EntryIngredients.of(getInputFluid(recipe))),
                getOutputList(getOutputFluids(recipe)));
        this.inputFluid=getInputFluid(recipe);
        this.outputFluids=getOutputFluids(recipe);
    }

    public static FluidStack getInputFluid(ElectrolyzerRecipe recipe){
        return FluidStack.create(recipe.getRecipeFluids().getStoredFluid(0).getFluid(),
                recipe.getRecipeFluids().getStoredAmount(0));
    }

    public static List<FluidStack> getOutputFluids(ElectrolyzerRecipe recipe){
        return List.of(FluidStack.create(recipe.getOutputFluidA(),recipe.getOutputAmountA()),
                FluidStack.create(recipe.getOutputFluidB(),recipe.getOutputAmountB()));
    }

    public static List<EntryIngredient> getOutputList(List<FluidStack> fluidStacks){
        ArrayList<EntryIngredient> output = new ArrayList<>();
        for (FluidStack fluidStack  : fluidStacks){
            output.add(EntryIngredients.of(fluidStack));
        }
        return output;
    }



    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ElectrolyzeCategory.ELECTROLYZE;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return super.getDisplayLocation();
    }


}
