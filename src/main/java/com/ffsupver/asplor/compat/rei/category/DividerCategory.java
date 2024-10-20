package com.ffsupver.asplor.compat.rei.category;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.compat.rei.display.DividerDisplay;
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

public class DividerCategory implements DisplayCategory<DividerDisplay> {
    public static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID,"textures/gui/divider.png");
   public static final CategoryIdentifier<DividerDisplay> DIVIDER = CategoryIdentifier.of(Asplor.MOD_ID,"divider");

    @Override
    public CategoryIdentifier<? extends DividerDisplay> getCategoryIdentifier() {
        return DIVIDER;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("recipe.asplor.divider");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(AllBlocks.DIVIDER);
    }

    @Override
    public List<Widget> setupDisplay(DividerDisplay display, me.shedaniel.math.Rectangle bounds) {
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
    public int getDisplayWidth(DividerDisplay display) {
        return 175;
    }
}
