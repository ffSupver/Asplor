package com.ffsupver.asplor.compat.rei.renderer;

import com.simibubi.create.compat.rei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import net.minecraft.client.gui.DrawContext;


public class MeltRecipeRenderer implements Renderer {
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();
    private final AnimatedMeltingFurnace meltingFurnace = new AnimatedMeltingFurnace();
    private final int displayWidth;
    private final int startX;
    private final int startY;
private final String heatType;
    public MeltRecipeRenderer(int displayWidth, int startX , int startY, String heatType) {
        this.displayWidth = displayWidth;
        this.startX = startX;
        this.startY = startY;
        this.heatType = heatType;
    }

    @Override
    public void render(DrawContext graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        BlazeBurnerBlock.HeatLevel heatLevel = switch (heatType){
            case "normal" -> BlazeBurnerBlock.HeatLevel.KINDLED;
            case "super" -> BlazeBurnerBlock.HeatLevel.SEETHING;
            default -> BlazeBurnerBlock.HeatLevel.NONE;
        };
        heater.withHeat(heatLevel)
                .draw(graphics, startX+ displayWidth/ 8 - 5, startY+12);
        meltingFurnace.draw(graphics,startX+ displayWidth/ 8 - 5, startY-8);
    }

}
