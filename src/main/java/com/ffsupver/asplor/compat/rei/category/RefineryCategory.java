package com.ffsupver.asplor.compat.rei.category;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.compat.rei.display.RefineryDisplay;
import com.ffsupver.asplor.util.REIFluidDisplay;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RefineryCategory implements DisplayCategory<RefineryDisplay> {
    private static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID,"textures/gui/refinery.png");
    public static final CategoryIdentifier<RefineryDisplay> REFINERY = CategoryIdentifier.of(Asplor.MOD_ID,"refinery");

    @Override
    public CategoryIdentifier<? extends RefineryDisplay> getCategoryIdentifier() {
        return REFINERY;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("recipe.asplor.refinery");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(AllBlocks.REFINERY_CONTROLLER);
    }
    @Override
    public List<Widget> setupDisplay(RefineryDisplay display, me.shedaniel.math.Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX()-87,bounds.getCenterY()-92);

        List<Widget> widgets =new LinkedList<>();
        widgets.add(Widgets.createTexturedWidget(TEXTURE,startPoint.x,startPoint.y,176,176));
        widgets.add(Widgets.createTexturedWidget(TEXTURE,startPoint.x+38,startPoint.y+114,176,0,37,31));
        widgets.add(Widgets.createTexturedWidget(TEXTURE,startPoint.x+76,startPoint.y+128,176,31,37,8));

        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+49,startPoint.y+125))
                .entries(display.getInputEntries().get(0))
                .markInput());
        int offsetY =0 ;
        for ( int i = display.getOutputEntries().size() - 1;i >=0;i--){
            EntryIngredient outputEntry = display.getOutputEntries().get(i);
            if (i > 0){
                widgets.add(Widgets.createTexturedWidget(TEXTURE, startPoint.x + 84, startPoint.y + 111 + offsetY, 176, 40, 4, 20));
            }
            widgets.add(Widgets.createTexturedWidget(TEXTURE,startPoint.x+88,startPoint.y+128+offsetY,188,31,25,8));
            widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+125,startPoint.y+125+offsetY))
                    .entries(outputEntry)
                    .markOutput());
            offsetY -=20;
        }

        List<FluidStack> outputList = new ArrayList<>();
        for (Fluid outputFluid : display.getRecipe().getOutputFluids()){
            FluidStack ouputFluidStack = new  FluidStack(outputFluid,display.getRecipe().getOutputFluidAmounts().get(outputFluid));
            outputList.add(ouputFluidStack);
        }

        REIFluidDisplay.addFluidTooltip(widgets,List.of(FluidIngredient.fromFluid(display.getRecipe().getInputFluid(),display.getRecipe().getInputFluidAmount())),
                outputList );
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 176;
    }
}
