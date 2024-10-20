package com.ffsupver.asplor.screen.backpack;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BackpackBaseScreen<T extends BackpackBaseHandler> extends HandledScreen<T> {
    private final Identifier TEXTURE;
    public static final Text title = Text.translatable("gui.asplor.backpack");

    private final int playerInventoryTitleYGet;

    public BackpackBaseScreen(T handler, PlayerInventory inventory, Text title, int backgroundHeight, Identifier TEXTURE,int playerInventoryTitleY) {
        super(handler, inventory, title);
        this.backgroundHeight=backgroundHeight;
        this.TEXTURE=TEXTURE;
        this.playerInventoryTitleYGet=playerInventoryTitleY;
    }

    @Override
    protected void init() {
        super.init();
        titleY=6;
        playerInventoryTitleY=playerInventoryTitleYGet;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        Identifier texture = TEXTURE;
        RenderSystem.setShaderTexture(0,texture);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(texture,x,y,0,0,backgroundWidth,backgroundHeight);

        renderDisableSlot(this.handler.getDisableIndexProperties(),context,x,y);
    }

    private void renderDisableSlot(List<Property> disableSlots, DrawContext context, int textureX, int textureY){
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        Identifier texture = TEXTURE;
        RenderSystem.setShaderTexture(0,texture);
        for (Property property : disableSlots) {
            int disableSlot = property.get();
            // -1代表没有设置
            if (disableSlot != -1) {
                if (disableSlot<9){
                    int y = backgroundHeight - 24;
                    int x = 7 + disableSlot * 18;
                    context.drawTexture(texture,x+textureX,y+textureY,backgroundWidth,0,18,18);
                }else if (disableSlot<36){
                    int y = backgroundHeight - 82 + (disableSlot - 9) / 9 * 18 ;
                    int x = 7 + (disableSlot % 9) * 18;
                    context.drawTexture(texture,x+textureX,y+textureY,backgroundWidth,0,18,18);

                }
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context,mouseX,mouseY);
    }
}
