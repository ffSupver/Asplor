package com.ffsupver.asplor.compat.rei.category;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.compat.rei.display.AssemblerDisplay;
import com.ffsupver.asplor.compat.rei.display.DividerDisplay;
import com.ffsupver.asplor.screen.assembler.AssemblerScreen;
import com.simibubi.create.AllItems;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class AssemblerCategory implements DisplayCategory<AssemblerDisplay> {
    public static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID,"textures/gui/assembler_rei.png");
    public static final CategoryIdentifier<AssemblerDisplay> ASSEMBLER = CategoryIdentifier.of(Asplor.MOD_ID,"assembler");

    @Override
    public CategoryIdentifier<? extends AssemblerDisplay> getCategoryIdentifier() {
        return ASSEMBLER;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("recipe.asplor.assembler");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(AllBlocks.ASSEMBLER);
    }
    @Override
    public List<Widget> setupDisplay(AssemblerDisplay display, me.shedaniel.math.Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX()-87,bounds.getCenterY()-36);
        List<Widget> widgets =new LinkedList<>();
//        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(TEXTURE,new me.shedaniel.math.Rectangle(startPoint.x,startPoint.y,175,70)));
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+26,startPoint.y+48))
                .entries(display.getInputEntries().get(0))
                .markInput());
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+44,startPoint.y+48))
                .entries(display.getInputEntries().get(1))
                .markInput());
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+76,startPoint.y+14))
                .entries(EntryIngredients.of(AllItems.SUPER_GLUE))
                .markInput());
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+132,startPoint.y+48))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 70;
    }
}
