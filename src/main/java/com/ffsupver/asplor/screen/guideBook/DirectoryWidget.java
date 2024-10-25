package com.ffsupver.asplor.screen.guideBook;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class DirectoryWidget extends ButtonWidget {
    private final boolean playPageTurnSound;
    protected DirectoryWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier, boolean playPageTurnSound) {
        super(x, y, width, height, message, onPress, narrationSupplier);
        this.playPageTurnSound = playPageTurnSound;
    }

    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = 0;
        int j = 192;
        if (this.isSelected()) {
            i += 23;
        }


        context.drawTexture(BookScreen.BOOK_TEXTURE, this.getX(), this.getY(), i, j, 23, 13);
    }

    public void playDownSound(SoundManager soundManager) {
        if (this.playPageTurnSound) {
            soundManager.play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
        }
    }


}
