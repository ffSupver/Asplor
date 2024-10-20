package com.ffsupver.asplor.screen.backpack;

import com.ffsupver.asplor.screen.ModScreenHandlers;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;

public class BackpackLargeHandler extends BackpackBaseHandler{
    private static final int BACKPACK_SIZE  =54;
    public BackpackLargeHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, EquipmentSlot.CHEST);
    }

    public BackpackLargeHandler(int syncId, PlayerInventory playerInventory, @Nullable EquipmentSlot equipmentSlot) {
        super(syncId, playerInventory, equipmentSlot, BACKPACK_SIZE, ModScreenHandlers.BACKPACK_LARGE_SCREEN_HANDLER);
    }
}
