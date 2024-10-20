package com.ffsupver.asplor.screen.alloyChest;

import com.ffsupver.asplor.block.alloyChest.AlloyChestEntity;
import com.ffsupver.asplor.screen.ModScreenHandlers;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class AlloyChestScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final AlloyChestEntity blockEntity;
    public  AlloyChestScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf){
        this(syncId,playerInventory,playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(54));
    }
    public AlloyChestScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity,PropertyDelegate arrayPropertyDelegate){
        super(ModScreenHandlers.ALLOY_CHEST_SCREEN_HANDLER,syncId);
        checkSize((Inventory) blockEntity,54);
        this.inventory = (Inventory) blockEntity;
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate =arrayPropertyDelegate;
        this.blockEntity=(AlloyChestEntity) blockEntity;
        for (int l=0;l<6;l++){
            for (int i =0 ;i<9;i++){
                addSlot(new Slot(inventory,i+l*9,8+i*18,18+l*18));
            }
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(arrayPropertyDelegate);
    }




    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 138 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 196));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
}
