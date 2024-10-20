package com.ffsupver.asplor.screen.backpack;

import com.ffsupver.asplor.Asplor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BackpackLargeScreen extends BackpackBaseScreen<BackpackLargeHandler>{
    private static final int backgroundHeight=219;
    private static final int playerInventoryTitleYL=126;
    private static final Identifier TEXTURE=  new Identifier(Asplor.MOD_ID,"textures/gui/backpack.png");
    public BackpackLargeScreen(BackpackLargeHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, backgroundHeight, TEXTURE,playerInventoryTitleYL);
    }
}
