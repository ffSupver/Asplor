package com.ffsupver.asplor.compat.rei.renderer;

import com.ffsupver.asplor.AllBlocks;
import com.simibubi.create.compat.rei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class AnimatedMeltingFurnace extends AnimatedKinetics {
    @Override
    public void draw(DrawContext graphics, int xOffset, int yOffset) {
        MatrixStack matrixStack = graphics.getMatrices();
        matrixStack.push();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-15.5f));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(22.5f));
        int scale = 23;


        blockElement(AllBlocks.MELTING_FURNACE.getDefaultState())
                .atLocal(0, 1.65, 0)
                .scale(scale)
                .render(graphics);


        matrixStack.pop();
    }

}
