package com.ffsupver.asplor.compat.rei.category.sequencedAssembly;

import com.ffsupver.asplor.compat.rei.renderer.AnimatedAlloyPress;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ReiSequencedAssemblySubCategory {
    public static class AssemblyAlloyPressing extends com.simibubi.create.compat.rei.category.sequencedAssembly.ReiSequencedAssemblySubCategory {

        AnimatedAlloyPress press;

        public AssemblyAlloyPressing() {
            super(25);
            press = new AnimatedAlloyPress(false);
        }

        @Override
        public void draw(SequencedRecipe<?> recipe, DrawContext graphics, double mouseX, double mouseY, int index) {
            MatrixStack ms = graphics.getMatrices();
            press.offset = index;
            ms.push();
            ms.translate(-5, 50, 0);
            ms.scale(.6f, .6f, .6f);
            press.draw(graphics, getWidth() / 2, 0);
            ms.pop();
        }

    }
}
