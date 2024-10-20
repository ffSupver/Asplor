package com.ffsupver.asplor.compat.rei.category;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.compat.rei.display.MeltingFurnaceDisplay;
import com.ffsupver.asplor.compat.rei.renderer.MeltRecipeRenderer;
import com.ffsupver.asplor.recipe.MeltRecipe;
import com.ffsupver.asplor.util.REIFluidDisplay;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
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

public class MeltingFurnaceCategory implements DisplayCategory<MeltingFurnaceDisplay> {
    public static final CategoryIdentifier<? extends MeltingFurnaceDisplay> MELT = CategoryIdentifier.of(Asplor.MOD_ID,"melt");
    public static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID,"textures/gui/melt.png");

    @Override
    public CategoryIdentifier<? extends MeltingFurnaceDisplay> getCategoryIdentifier() {
        return MELT;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("recipe.asplor.melt");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(AllBlocks.MELTING_FURNACE);
    }

    @Override
    public List<Widget> setupDisplay(MeltingFurnaceDisplay display, me.shedaniel.math.Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX()-87,bounds.getCenterY()-24);
        List<Widget> widgets =new LinkedList<>();
        widgets.add(Widgets.createTexturedWidget(TEXTURE,new me.shedaniel.math.Rectangle(startPoint.x,startPoint.y,175,57)));

        widgets.add(Widgets.wrapRenderer(new me.shedaniel.math.Rectangle(startPoint.x,startPoint.y,175,57),
                new MeltRecipeRenderer(getDisplayWidth(null),startPoint.x,startPoint.y,display.getHeatType())));

        widgets.add(Widgets.createArrow(new  me.shedaniel.math.Point(startPoint.x+99,startPoint.y+20)));
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+73,startPoint.y+20))
                .entries(display.getInputEntries().get(0))
                .markInput());

        Slot outputSlot = Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+128,startPoint.y+20))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput();
        widgets.add(outputSlot);
        MeltRecipe recipe = display.getRecipe();
        REIFluidDisplay.addFluidTooltip(widgets,Collections.emptyList(),
                List.of(new FluidStack(recipe.getOutputFluid(),recipe.getOutputAmount())));
        return widgets;
    }
    @Override
    public int getDisplayHeight() {
        return 58;
    }

    @Override
    public int getDisplayWidth(MeltingFurnaceDisplay display) {
        return 175;
    }

}
