package com.ffsupver.asplor.screen.alloyChest;

import com.ffsupver.asplor.Asplor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AlloyChestScreen extends HandledScreen<AlloyChestScreenHandler> {

//    protected int titleX;
//    protected int titleY;
//    protected Text title;
    private static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID,"textures/gui/alloy_chest.png");
    public AlloyChestScreen(AlloyChestScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight=219;
//        this.title=title;
    }

    @Override
    protected void init() {
        super.init();
        titleY=6;
        playerInventoryTitleY=126;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0,TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(TEXTURE,x,y,0,0,backgroundWidth,backgroundHeight);
//        context.drawText(textRenderer,title,8,6,0,false);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context,mouseX,mouseY);
    }
}
