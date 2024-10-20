package com.ffsupver.asplor.compat.rei.category;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.compat.rei.display.AlloyPressingDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class AlloyPressingCategory implements DisplayCategory<AlloyPressingDisplay> {
    public static final CategoryIdentifier<? extends AlloyPressingDisplay> ALLOY_PRESSING = CategoryIdentifier.of(Asplor.MOD_ID,"alloy_pressing");
    public static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID,"textures/gui/alloy_pressing.png");

    @Override
    public CategoryIdentifier<? extends AlloyPressingDisplay> getCategoryIdentifier() {
        return ALLOY_PRESSING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("recipe.asplor.alloy_pressing");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(AllBlocks.ALLOY_MECHANICAL_PRESS);
    }

    @Override
    public List<Widget> setupDisplay(AlloyPressingDisplay display, me.shedaniel.math.Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX()-87,bounds.getCenterY()-24);
        List<Widget> widgets =new LinkedList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(TEXTURE,new me.shedaniel.math.Rectangle(startPoint.x,startPoint.y,175,47)));

        widgets.add(Widgets.createArrow(new  me.shedaniel.math.Point(startPoint.x+70,startPoint.y+14)));
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+26,startPoint.y+14))
                .entries(display.getInputEntries().get(0))
                .markInput());
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+126,startPoint.y+14))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());

        return widgets;


    }
    @Override
    public int getDisplayHeight() {
        return 48;
    }

    @Override
    public int getDisplayWidth(AlloyPressingDisplay display) {
        return 175;
    }

}
