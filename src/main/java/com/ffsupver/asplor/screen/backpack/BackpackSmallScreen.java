package com.ffsupver.asplor.screen.backpack;

import com.ffsupver.asplor.Asplor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BackpackSmallScreen extends BackpackBaseScreen<BackpackSmallHandler>{
    private static final int backgroundHeight=165;
    private static final int playerInventoryTitleYS = 73;
    private static final Identifier TEXTURE=  new Identifier(Asplor.MOD_ID,"textures/gui/backpack_small.png");
    public BackpackSmallScreen(BackpackSmallHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, backgroundHeight, TEXTURE,playerInventoryTitleYS);
    }
}
