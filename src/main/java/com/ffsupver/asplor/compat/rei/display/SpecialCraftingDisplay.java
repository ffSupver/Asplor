package com.ffsupver.asplor.compat.rei.display;

import com.ffsupver.asplor.compat.rei.category.SpecialCraftingCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;

import java.util.ArrayList;
import java.util.List;

public class SpecialCraftingDisplay implements Display {
    private final boolean shapeless;
    private final List<EntryIngredient> input;
    private final List<EntryIngredient> output;

    public SpecialCraftingDisplay(boolean shapeless, List<EntryIngredient> input, List<EntryIngredient> output) {
        this.shapeless = shapeless;
        this.input = input;
        this.output = output;
    }
    public SpecialCraftingDisplay(List<EntryIngredient> input, List<EntryIngredient> output){
        this(true,input,output);
    }


    @Override
    public List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return output;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpecialCraftingCategory.IDENTIFIER;
    }

    public boolean isShapeless() {
        return shapeless;
    }
    public List<InputIngredient<EntryStack<?>>> getInputIngredients(){
        List<InputIngredient<EntryStack<?>>> list = new ArrayList<>(input.size());
        for (int i = 0, n = input.size(); i < n; i++) {
            list.add(InputIngredient.of(i,i,input.get(i)));
        }
        return list;
    }
}
