package com.ffsupver.asplor.compat.rei.category;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.compat.rei.display.SmartProcessingDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SmartProcessingCategory implements DisplayCategory<SmartProcessingDisplay> {
    public static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID,"textures/gui/smart_processing.png");
    public static final CategoryIdentifier<SmartProcessingDisplay> SMART_PROCESSING = CategoryIdentifier.of(Asplor.MOD_ID,"smart_processing");

    @Override
    public CategoryIdentifier<? extends SmartProcessingDisplay> getCategoryIdentifier() {
        return SMART_PROCESSING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("recipe.asplor.smart_processing");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(AllBlocks.SMART_MECHANICAL_ARM);
    }
    @Override
    public List<Widget> setupDisplay(SmartProcessingDisplay display, me.shedaniel.math.Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX()-87,bounds.getCenterY()-36);
        List<Widget> widgets =new LinkedList<>();
        widgets.add(Widgets.createTexturedWidget(TEXTURE,new me.shedaniel.math.Rectangle(startPoint.x,startPoint.y,175,67)));
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+20,startPoint.y+43))
                .entries(display.getInputEntries().get(0))
                .disableBackground()
                .markInput());

        if (display.requireSchematic()){
            addOtherInputSlot(widgets,startPoint,18,19,23,display.getSchematicItem());
        }

        Map<ItemStack,Integer> tools = display.getToolTypeCount();
        int toolOrder = 0;
        for (ItemStack itemStack : tools.keySet()){
            int offsetX = 38 + toolOrder*20;
//            widgets.add(Widgets.createTexturedWidget(TEXTURE,startPoint.x+offsetX,startPoint.y+4,176,0,18,29));
            widgets.add(Widgets.createLabel(new me.shedaniel.math.Point(startPoint.x+offsetX+13,startPoint.y+24),Text.literal(tools.get(itemStack).toString())));
            addOtherInputSlot(widgets,startPoint,29,offsetX,4,itemStack);
            toolOrder += 1;
        }

        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+132,startPoint.y+43))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());
        return widgets;
    }

    private void addOtherInputSlot(List<Widget> widgets, Point startPoint, int v, int offsetX, int offsetY, ItemStack itemStack){
        widgets.add(Widgets.createTexturedWidget(TEXTURE,startPoint.x+offsetX,startPoint.y+offsetY,176,0,18,v));
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+offsetX+1,startPoint.y+1+offsetY))
                .entries(EntryIngredients.of(itemStack))
                .disableBackground()
                .markInput());
    }

    @Override
    public int getDisplayHeight() {
        return 67;
    }
}
