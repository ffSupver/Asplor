package com.ffsupver.asplor.screen.ranger;

import com.ffsupver.asplor.entity.custom.Ranger;
import com.ffsupver.asplor.screen.ModScreenHandlers;
import earth.terrarium.adastra.common.menus.base.BaseEntityContainerMenu;
import earth.terrarium.adastra.common.menus.slots.CustomSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;

public class RangerHandler extends BaseEntityContainerMenu<Ranger> {
        public RangerHandler(int id, PlayerInventory inventory, Ranger entity) {
            super(ModScreenHandlers.RANGER_SCREEN_HANDLER, id, inventory, entity);
        }

        public RangerHandler(int id, PlayerInventory inventory, PacketByteBuf buf) {
            super(ModScreenHandlers.RANGER_SCREEN_HANDLER, id, inventory, (Ranger) inventory.player.getWorld().getEntityById(buf.readVarInt()));
        }

        protected int getContainerInputEnd() {
            return 18;
        }

        protected int getInventoryStart() {
            return 18;
        }

        public int getPlayerInvXOffset() {
            return 0;
        }

        public int getPlayerInvYOffset() {
            return 99;
        }

        protected void addMenuSlots() {
            for(int i = 0; i < 4; ++i) {
                for(int j = 0; j < 4; ++j) {
                    this.addSlot(new Slot((this.entity).inventory(), i * 4 + j + 2, 78 + j * 18, 16 + i * 18));
                }
            }

            this.addSlot(new Slot((this.entity).inventory(), 0, 12, 26));
            this.addSlot(CustomSlot.noPlace((this.entity).inventory(), 1, 12, 56));
        }
}
