package com.ffsupver.asplor.block.alloyMechanicalPress;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.AllRecipeTypes;
import com.ffsupver.asplor.compat.rei.category.sequencedAssembly.SequencedAssemblySubCategoryTypes;
import com.simibubi.create.compat.recipeViewerCommon.SequencedAssemblySubCategoryType;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

public class AlloyPressingRecipe extends ProcessingRecipe<Inventory> implements IAssemblyRecipe {
    public AlloyPressingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(AllRecipeTypes.ALLOY_PRESSING, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 2;
    }

    @Override
    public Text getDescriptionForAssembly() {
        return Lang.translateDirect("recipe.assembly.alloy_pressing");
    }

    @Override
    public void addRequiredMachines(Set<ItemConvertible> list) {
            list.add(AllBlocks.ALLOY_MECHANICAL_PRESS.get());
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {}

    @Override
    public SequencedAssemblySubCategoryType getJEISubCategory() {
        return SequencedAssemblySubCategoryTypes.ALLOY_PRESSING;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        if (inv.isEmpty())
            return false;
        return ingredients.get(0)
                .test(inv.getStack(0));
    }
}
