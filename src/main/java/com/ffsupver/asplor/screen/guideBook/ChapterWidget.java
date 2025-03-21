package com.ffsupver.asplor.screen.guideBook;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)

public class ChapterWidget extends ButtonWidget {
    private static final int WIDTH = 23;
    private static final int HEIGHT = 18;
    private final ItemStack displayItem;
    private final Text description;
    private final TextRenderer textRenderer;
    protected ChapterWidget(int x, int y, PressAction onPress, ItemStack displayItem, Text description, TextRenderer textRenderer) {
        super(x, y, WIDTH, HEIGHT,description, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.displayItem = displayItem;
        this.description = description;
        this.textRenderer = textRenderer;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(GuideBookScreen.GUIDE_BOOK_TEXTURE,getX(),getY(), this.isSelected() ? 26 : 0,218,WIDTH,HEIGHT);
        context.drawItem(displayItem,getX() + 3,getY() + 1);
        if (mouseX >= getX() && mouseX <= getX() + WIDTH && mouseY >= getY() && mouseY <= getY() + HEIGHT){
            context.drawTooltip(textRenderer,description,mouseX,mouseY);
        }
    }

    @Override
    public void setTooltip(@Nullable Tooltip tooltip) {
        super.setTooltip(tooltip);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
    }
}
