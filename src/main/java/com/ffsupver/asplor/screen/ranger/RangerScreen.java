package com.ffsupver.asplor.screen.ranger;

import com.ffsupver.asplor.entity.custom.Ranger;
import earth.terrarium.adastra.client.screens.base.VehicleScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class RangerScreen  extends VehicleScreen<RangerHandler, Ranger> {
        public static final Identifier TEXTURE = new Identifier("ad_astra", "textures/gui/container/rover.png");

        public RangerScreen(RangerHandler menu, PlayerInventory inventory, Text component) {
            super(menu, inventory, component, TEXTURE, 177, 181);
        }

        protected void drawBackground(@NotNull DrawContext graphics, float partialTick, int mouseX, int mouseY) {
            super.drawBackground(graphics, partialTick, mouseX, mouseY);
            this.drawFluidBar(graphics, mouseX, mouseY, 37, 57, this.entity.fluid(), this.entity.fluidContainer().getTankCapacity(0));
        }

}
