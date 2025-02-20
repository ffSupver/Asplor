package com.ffsupver.asplor.screen.rocket;

import com.ffsupver.asplor.entity.custom.rocket.CargoRocketEntity;
import com.ffsupver.asplor.screen.ModScreenHandlers;
import earth.terrarium.adastra.common.menus.base.BaseEntityContainerMenu;
import earth.terrarium.adastra.common.menus.slots.CustomSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;

public class CargoRocketScreenHandler extends BaseEntityContainerMenu<CargoRocketEntity> {

    public CargoRocketScreenHandler(int id, PlayerInventory inventory, CargoRocketEntity entity) {
        super(ModScreenHandlers.CARGO_ROCKET_SCREEN_HANDLER, id, inventory, entity);
    }
    public CargoRocketScreenHandler(int id, PlayerInventory inventory, PacketByteBuf buf) {
        this(id,inventory,(CargoRocketEntity) inventory.player.getWorld().getEntityById(buf.readVarInt()));
    }

    @Override
    protected int getContainerInputEnd() {
        return 58;
    }

    @Override
    protected int getInventoryStart() {
        return 58;
    }

    @Override
    public int getPlayerInvXOffset() {
        return 0;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 146;
    }

    @Override
    protected void addMenuSlots() {
        for(int i = 0; i < 4; ++i) {
            for(int j = 0; j < 5; ++j) {
                this.addSlot(new Slot(this.entity.inventory(), i * 5 + j + 2, 66 + j * 18, 15 + i * 18));
            }
        }
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 8; ++j) {
                this.addSlot(new Slot(this.entity.inventory(),  20 + i * 8 + j + 2, 12 + j * 18, 87 + i * 18));
            }
        }

        this.addSlot(new Slot((this.entity).inventory(), 0, 12, 24));
        this.addSlot(CustomSlot.noPlace((this.entity).inventory(), 1, 12, 54));
    }
}
