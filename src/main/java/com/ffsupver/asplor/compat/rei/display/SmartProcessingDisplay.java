package com.ffsupver.asplor.compat.rei.display;

import com.ffsupver.asplor.block.smartMechanicalArm.ToolType;
import com.ffsupver.asplor.compat.rei.category.SmartProcessingCategory;
import com.ffsupver.asplor.item.item.SchematicItem;
import com.ffsupver.asplor.recipe.SmartProcessingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartProcessingDisplay extends BasicDisplay {
    private List<ToolType> toolTypes;
    private String schematic;
    public SmartProcessingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public SmartProcessingDisplay(SmartProcessingRecipe recipe){
        super(List.of(EntryIngredients.ofIngredient(recipe.getIngredients().get(0))),List.of(EntryIngredients.of(recipe.getOutput(null))));
        this.toolTypes =recipe.getToolTypes();
        this.schematic = recipe.requireSchematic() ? recipe.getSchematic() : null;
    }

    public Map<ItemStack,Integer> getToolTypeCount(){
        Map<ItemStack,Integer> result = new HashMap<>();
        for (ToolType toolType : getToolTypes()){
            result.put(toolType.getToolItem().getDefaultStack(), (int) this.toolTypes.stream().filter(toolType1 -> toolType1.equals(toolType)).count());
        }
        return result;
    }

    private List<ToolType> getToolTypes(){
        return  this.toolTypes.stream().distinct().toList();
    }

    public boolean requireSchematic(){return this.schematic != null;}

    public ItemStack getSchematicItem(){
        return SchematicItem.getSchematicItem(schematic);
    }





    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SmartProcessingCategory.SMART_PROCESSING;
    }
}
