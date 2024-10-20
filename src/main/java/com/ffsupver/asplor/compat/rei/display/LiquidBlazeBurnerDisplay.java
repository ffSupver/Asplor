package com.ffsupver.asplor.compat.rei.display;

import com.ffsupver.asplor.compat.rei.category.LiquidBlazeBurnerCategory;
import com.ffsupver.asplor.recipe.LiquidBlazeBurnerRecipe;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class LiquidBlazeBurnerDisplay extends BasicDisplay {
    public int burnTime ;
    public long requireAmount;
    public String burnType;
    public Fluid fluid;
    public LiquidBlazeBurnerDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public LiquidBlazeBurnerDisplay(LiquidBlazeBurnerRecipe recipe){
        super(List.of(EntryIngredients.of(recipe.getInputFluid().getFluid())),List.of(EntryIngredients.of(ItemStack.EMPTY)));
        this.burnTime= recipe.getOutput(null).getCount();
        this.burnType = recipe.getHeatType();
        this.requireAmount =  recipe.getRequiredAmount();
        this.fluid = recipe.getInputFluid().getFluid();
    }

    public Fluid getFluid() {
        return fluid;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return LiquidBlazeBurnerCategory.LIQUID_BLAZE_BURNER;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return super.getDisplayLocation();
    }


}
