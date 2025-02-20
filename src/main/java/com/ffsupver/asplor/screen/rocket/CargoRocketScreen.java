package com.ffsupver.asplor.screen.rocket;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.entity.custom.rocket.CargoRocketEntity;
import earth.terrarium.adastra.client.screens.base.VehicleScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class CargoRocketScreen extends VehicleScreen<CargoRocketScreenHandler, CargoRocketEntity> {
    public static final Identifier TEXTURE = new Identifier(Asplor.MOD_ID, "textures/gui/cargo_rocket.png");


    public CargoRocketScreen(CargoRocketScreenHandler menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component, TEXTURE, 177, 228);
    }

    @Override
    protected void drawBackground(@NotNull DrawContext graphics, float partialTick, int mouseX, int mouseY) {
        super.drawBackground(graphics,partialTick,mouseX,mouseY);
        this.drawFluidBar(graphics, mouseX, mouseY, 37, 55, this.entity.fluid(), this.entity.fluidContainer().getTankCapacity(0));
    }

}
