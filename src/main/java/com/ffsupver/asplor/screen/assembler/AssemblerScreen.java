package com.ffsupver.asplor.screen.assembler;

import com.ffsupver.asplor.Asplor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class AssemblerScreen extends ForgingScreen<AssemblerScreenHandler> {
   public static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID,"textures/gui/assembler.png");
    private static final Identifier EMPTY_SUPER_GLUE = new Identifier(Asplor.MOD_ID,"textures/gui/empty_super_glue.png");
    public AssemblerScreen(AssemblerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title,TEXTURE);

    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        super.drawBackground(context,delta,mouseX,mouseY);
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        MatrixStack ms = context.getMatrices();
//        InventoryScreen.drawEntity(context,this.x + 141, this.y + 75, 25,
//                (new Quaternionf()).rotationXYZ(0.43633232F, 0.0F, 3.1415927F),
//                (Quaternionf) null, new ArmorStandEntity(this.client.world, 0.0, 0.0, 0.0)
//                );

    }

    @Override
    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
        if (isOutputEmpty()){
            context.drawTexture(TEXTURE, x + 99, y + 46, this.backgroundWidth, 0, 28, 21);
        }
        if (isSuperGlueEmpty()){
            context.drawTexture(TEXTURE,x+76,y+14,this.backgroundWidth,21,16,16);
        }
    }

    private boolean isSuperGlueEmpty() {
        return !this.handler.getSlot(2).hasStack();
    }

    private boolean isOutputEmpty(){
        return !this.handler.getSlot(this.handler.getResultSlotIndex()).hasStack();
    }
}
