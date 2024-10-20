package com.ffsupver.asplor.compat.rei.category;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.compat.rei.display.ElectrolyzeDisplay;
import com.ffsupver.asplor.util.REIFluidDisplay;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
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

public class ElectrolyzeCategory implements DisplayCategory<ElectrolyzeDisplay> {
    public static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID,"textures/gui/electrolyze.png");
   public static final CategoryIdentifier<ElectrolyzeDisplay> ELECTROLYZE = CategoryIdentifier.of(Asplor.MOD_ID,"electrolyze");

    @Override
    public CategoryIdentifier<? extends ElectrolyzeDisplay> getCategoryIdentifier() {
        return ELECTROLYZE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("recipe.asplor.electrolyze");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(AllBlocks.ELECTROLYZER);
    }

    @Override
    public List<Widget> setupDisplay(ElectrolyzeDisplay display, me.shedaniel.math.Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX()-87,bounds.getCenterY()-24);
        List<Widget> widgets =new LinkedList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(TEXTURE,new me.shedaniel.math.Rectangle(startPoint.x,startPoint.y,175,47)));
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+20,startPoint.y+10))
                .entries(display.getInputEntries().get(0))
                .markInput());
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+148,startPoint.y+5))
                .entries(display.getOutputEntries().get(0))
                .markOutput());
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+148,startPoint.y+25))
                .entries(display.getOutputEntries().get(1))
                .markOutput());
        System.out.println(" "+display.outputFluids.get(0).getAmount()+" "+display.outputFluids.get(1).getAmount());
        REIFluidDisplay.addFluidTooltip(widgets,List.of(FluidIngredient.fromFluid(display.inputFluid.getFluid(),display.inputFluid.getAmount())),
                List.of(new FluidStack(display.outputFluids.get(0).getFluid(),display.outputFluids.get(0).getAmount()),
                        new FluidStack(display.outputFluids.get(1).getFluid(),display.outputFluids.get(1).getAmount())));



        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 48;
    }

    @Override
    public int getDisplayWidth(ElectrolyzeDisplay display) {
        return 175;
    }
}
