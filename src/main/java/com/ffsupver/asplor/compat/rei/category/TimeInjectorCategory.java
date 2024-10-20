package com.ffsupver.asplor.compat.rei.category;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.compat.rei.display.TimeInjectorDisplay;
import com.ffsupver.asplor.util.REIFluidDisplay;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import io.github.fabricators_of_create.porting_lib.util.FluidUnit;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TimeInjectorCategory implements DisplayCategory<TimeInjectorDisplay> {
    public static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID,"textures/gui/time_injector.png");
    public static final CategoryIdentifier<TimeInjectorDisplay> TIME_INJECTOR = CategoryIdentifier.of(Asplor.MOD_ID,"time_injector");

    @Override
    public CategoryIdentifier<? extends TimeInjectorDisplay> getCategoryIdentifier() {
        return TIME_INJECTOR;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("recipe.asplor.time_injector");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(AllBlocks.TIME_INJECTOR);
    }
    @Override
    public List<Widget> setupDisplay(TimeInjectorDisplay display, me.shedaniel.math.Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX()-87,bounds.getCenterY()-36);
        List<Widget> widgets =new LinkedList<>();
        widgets.add(Widgets.createTexturedWidget(TEXTURE,new me.shedaniel.math.Rectangle(startPoint.x,startPoint.y,175,50)));
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+20,startPoint.y+8))
                .entries(display.getInputEntries().get(0))
                .markInput());
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+20,startPoint.y+27))
                .entries(display.getInputEntries().get(1))
                .markInput());
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+132,startPoint.y+27))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());
        REIFluidDisplay.addFluidTooltip(widgets,List.of(FluidIngredient.fromFluid(display.getInputFluid(), 1000*81)), Collections.emptyList());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 50;
    }
}
