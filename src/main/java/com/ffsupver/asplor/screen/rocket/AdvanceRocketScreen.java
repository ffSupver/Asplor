package com.ffsupver.asplor.screen.rocket;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.entity.custom.rocket.AdvanceRocketEntity;
import earth.terrarium.adastra.client.screens.base.VehicleScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class AdvanceRocketScreen extends VehicleScreen<AdvanceRocketScreenHandler, AdvanceRocketEntity> {
    public static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID, "textures/gui/advance_rocket.png");


    public AdvanceRocketScreen(AdvanceRocketScreenHandler menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component, TEXTURE, 177, 228);
    }

    @Override
    protected void drawBackground(@NotNull DrawContext graphics, float partialTick, int mouseX, int mouseY) {
        super.drawBackground(graphics,partialTick,mouseX,mouseY);
        this.drawFluidBar(graphics, mouseX, mouseY, 37, 45, this.entity.fluid(), this.entity.fluidContainer().getTankCapacity(0));
    }

}
