package com.ffsupver.asplor.compat.rei.renderer;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.AllPartialModels;
import com.simibubi.create.compat.rei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class AnimatedAlloyPress extends AnimatedKinetics {
    private boolean basin;

    public AnimatedAlloyPress(boolean basin) {
        this.basin = basin;
    }

    @Override
    public void draw(DrawContext graphics, int xOffset, int yOffset) {
        MatrixStack matrixStack = graphics.getMatrices();
        matrixStack.push();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(-15.5f));
        matrixStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(22.5f));
        int scale = basin ? 23 : 24;

        blockElement(shaft(Direction.Axis.Z))
                .rotateBlock(0, 0, getCurrentAngle())
                .scale(scale)
                .render(graphics);

        blockElement(AllBlocks.ALLOY_MECHANICAL_PRESS.getDefaultState())
                .scale(scale)
                .render(graphics);

        blockElement(AllPartialModels.ALLOY_MECHANICAL_PRESS_HEAD)
                .atLocal(0, -getAnimatedHeadOffset(), 0)
                .scale(scale)
                .render(graphics);

        if (basin)
            blockElement(com.simibubi.create.AllBlocks.BASIN.getDefaultState())
                    .atLocal(0, 1.65, 0)
                    .scale(scale)
                    .render(graphics);

        matrixStack.pop();
    }

    private float getAnimatedHeadOffset() {
        float cycle = (AnimationTickHolder.getRenderTime() - offset * 8) % 30;
        if (cycle < 10) {
            float progress = cycle / 10;
            return -(progress * progress * progress);
        }
        if (cycle < 15)
            return -1;
        if (cycle < 20)
            return -1 + (1 - ((20 - cycle) / 5));
        return 0;
    }
}
