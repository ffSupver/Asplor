package com.ffsupver.asplor.compat.rei.category;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.compat.rei.display.LiquidBlazeBurnerDisplay;
import com.ffsupver.asplor.recipe.LiquidBlazeBurnerRecipe;
import com.ffsupver.asplor.util.REIFluidDisplay;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LiquidBlazeBurnerCategory implements DisplayCategory<LiquidBlazeBurnerDisplay> {
    public static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID,"textures/gui/liquid_blaze_burner.png");
   public static final CategoryIdentifier<LiquidBlazeBurnerDisplay> LIQUID_BLAZE_BURNER = CategoryIdentifier.of(Asplor.MOD_ID,"liquid_blaze_burner");

    @Override
    public CategoryIdentifier<? extends LiquidBlazeBurnerDisplay> getCategoryIdentifier() {
        return LIQUID_BLAZE_BURNER;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("recipe.asplor.liquid_blaze_burner");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(AllBlocks.LIQUID_BLAZE_BURNER);
    }

    @Override
    public List<Widget> setupDisplay(LiquidBlazeBurnerDisplay display, me.shedaniel.math.Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX()-87,bounds.getCenterY()-24);
        List<Widget> widgets =new LinkedList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(TEXTURE,new me.shedaniel.math.Rectangle(startPoint.x,startPoint.y,175,47)));
        widgets.add(Widgets.createLabel(new  me.shedaniel.math.Point(startPoint.x+110,startPoint.y+10),Text.translatable("recipe.asplor.liquid_blaze_burner_burn_type_" + display.burnType)));
        widgets.add(Widgets.createLabel(new  me.shedaniel.math.Point(startPoint.x+90,startPoint.y+30),
                Text.translatable("recipe.asplor.liquid_blaze_burner_description",display.requireAmount, display.burnTime)));
        widgets.add(Widgets.createSlot(new me.shedaniel.math.Point(startPoint.x+20,startPoint.y+10))
                .entries(display.getInputEntries().get(0))
                .markInput());
        REIFluidDisplay.addFluidTooltip(widgets,List.of(FluidIngredient.fromFluid(display.getFluid(),display.requireAmount*81)), Collections.emptyList());

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 48;
    }

    @Override
    public int getDisplayWidth(LiquidBlazeBurnerDisplay display) {
        return 175;
    }
}
