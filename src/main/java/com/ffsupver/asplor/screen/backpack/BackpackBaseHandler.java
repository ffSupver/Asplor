package com.ffsupver.asplor.screen.backpack;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BackpackBaseHandler extends ScreenHandler {
    public static final Text title = Text.translatable("gui.asplor.backpack");

    private DefaultedList<ItemStack> inventory ;
    private final SimpleInventory simpleInventory ;
    private ItemStack backpack;
    private EquipmentSlot equipmentSlot;
    public static String backpackDataKey="backpack";
    public static String backpackTypeKey = "type";
    public int backpackSize;
    public ArrayList<Integer> disableItemIndexes;

    private final List<Property> disableIndexProperties = new ArrayList<>();


    public BackpackBaseHandler(int syncId, PlayerInventory playerInventory, @Nullable EquipmentSlot equipmentSlot, int backpackSize, ScreenHandlerType type) {
        super(type,syncId);

        disableItemIndexes = new ArrayList<>();
        for (int i=0 ;i<36;i++){
            Property property = Property.create();
            property.set(-1);
            this.addProperty(property);
            this.disableIndexProperties.add(property);
            // 将 disableItemIndexes 的每个元素绑定为 Property，便于同步
            if (isBackpackItem(playerInventory,i)){
                disableItemIndexes.add(i);
                this.disableIndexProperties.get(i).set(i);
            }
        }


        this.equipmentSlot = equipmentSlot;
        PlayerEntity player = playerInventory.player;
        this.backpack=player.getEquippedStack(equipmentSlot);

        this.backpackSize = backpackSize;

        this.inventory=DefaultedList.ofSize(backpackSize,ItemStack.EMPTY);
        this.simpleInventory=new SimpleInventory(backpackSize);

        if (backpack.hasNbt()) {
            readInventoryFromNbt(backpack.getNbt().getCompound(backpackDataKey),inventory);
        }

        for (int i=0;i<inventory.size();i++){
            simpleInventory.setStack(i,inventory.get(i));
        }

        simpleInventory.onOpen(player);
        for (int l=0;l<simpleInventory.size()/9;l++){
            for (int i =0 ;i<9;i++){
                addSlot(new BackpackSlot(simpleInventory,i+l*9,8+i*18,18+l*18,this));
            }
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

    }


    private void readInventoryFromNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks){
        NbtList nbtList = nbt.getList("Items", 10);

        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if (j >= 0 && j < stacks.size()) {
                stacks.set(j, ItemStack.fromNbt(nbtCompound));
            }
        }
    }

    private NbtCompound writeInventoryToNbt(DefaultedList<ItemStack> stacks){
        NbtList itemsList = new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i,simpleInventory.getStack(i));
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                NbtCompound stackNbt = stack.writeNbt(new NbtCompound());
                stackNbt.putByte("Slot", (byte) i);
                itemsList.add(stackNbt);
            }
        }
        NbtCompound nbt = new NbtCompound();
        nbt.put("Items",itemsList);
        return nbt;
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        int playInventoryY = 30+this.simpleInventory.size()/9*18;
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(
                isBackpackItem(playerInventory,l + i * 9 + 9)?
                        new BackpackItemSlot(playerInventory,l + i * 9 + 9, 8 + l * 18, playInventoryY + i * 18,this):
                        new PlayerInventorySlot<>(playerInventory, l + i * 9 + 9, 8 + l * 18, playInventoryY + i * 18,this)
                );
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        int playHotbarY = 88+this.simpleInventory.size()/9*18;
        for (int i = 0; i < 9; ++i) {
            this.addSlot(
                    isBackpackItem(playerInventory,i)?
                            new BackpackItemSlot<>(playerInventory,i,8 + i * 18, playHotbarY,this):
                            new PlayerInventorySlot<>(playerInventory, i, 8 + i * 18, playHotbarY,this)
            );
        }
    }

    private boolean isBackpackItem(PlayerInventory playerInventory,int index){
        return playerInventory.getStack(index).hasNbt() && playerInventory.getStack(index).getNbt().contains(backpackDataKey);
    }

    public static boolean isBackpackItem(ItemStack itemStack){
        return itemStack.hasNbt() && itemStack.getNbt().contains(backpackDataKey);
    }

    protected void updateBackpackNbt() {
        if (backpack.hasNbt()) {
            NbtCompound nbt = backpack.getNbt();
            NbtCompound inventoryNbt = writeInventoryToNbt(inventory);
            if (nbt != null && nbt.contains(backpackDataKey, 10) && nbt.getCompound(backpackDataKey).contains(backpackTypeKey, 8)){
                inventoryNbt.putString(backpackTypeKey,nbt.getCompound(backpackDataKey).getString(backpackTypeKey));
            }
            nbt.put(backpackDataKey, inventoryNbt); // 更新 NBT 中的 inventory 部分
            backpack.setNbt(nbt); // 重新设置背包的 NBT
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

    protected void addDisableIndex(Integer index){
            if (!this.disableItemIndexes.contains(index)){
                this.disableItemIndexes.add(index);
                this.updateDisableIndexProperties();
            }
    }

    public ArrayList<Integer> getDisableItemIndexes() {
        return this.disableItemIndexes;
    }

    // 更新 disableIndexProperties 的值
    private void updateDisableIndexProperties() {
        for (int i = 0; i < disableItemIndexes.size(); i++) {
            disableIndexProperties.get(i).set(disableItemIndexes.get(i));
        }
    }

    public List<Property> getDisableIndexProperties() {
        return disableIndexProperties;
    }

    @Override
    public void syncState() {
        super.syncState();
        this.updateDisableIndexProperties();
    }


    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        player.equipStack(equipmentSlot,backpack);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.simpleInventory.canPlayerUse(player);
    }

    private  class  BackpackSlot<T extends BackpackBaseHandler> extends Slot {
        private final T backpackHandler;

        public BackpackSlot(Inventory inventory, int index, int x, int y, T backpackHandler) {
            super(inventory, index, x, y);
            this.backpackHandler = backpackHandler;
        }


        @Override
        public void markDirty() {
            backpackHandler.updateBackpackNbt();
            super.markDirty();
        }
    }

    private class  BackpackItemSlot<T extends BackpackBaseHandler> extends Slot{

        private final T backpackHandler;
        public BackpackItemSlot(Inventory inventory, int index, int x, int y, T backpackHandler) {
            super(inventory, index, x, y);
            this.backpackHandler=backpackHandler;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return false;
        }

        @Override
        public boolean canBeHighlighted() {
            return false;
        }

        @Override
        public boolean canTakePartial(PlayerEntity player) {
            return false;
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
        }

        @Override
        public ItemStack getStack() {
            return ItemStack.EMPTY;
        }

        @Override
        public void setStack(ItemStack stack) {
        }

        @Override
        public ItemStack takeStack(int amount) {
            return ItemStack.EMPTY;
        }
    }
    private class PlayerInventorySlot<T extends BackpackBaseHandler> extends Slot{

        private final T backpackHandler;
        public PlayerInventorySlot(Inventory inventory, int index, int x, int y, T backpackHandler) {
            super(inventory, index, x, y);
            this.backpackHandler = backpackHandler;
        }

        private boolean isDisabled(){
            for (Property property : this.backpackHandler.getDisableIndexProperties()){
                if(property.get()==this.getIndex()){
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return !isDisabled();
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return !isDisabled() && super.canTakeItems(playerEntity);
        }

        @Override
        public boolean canTakePartial(PlayerEntity player) {
            return !isDisabled();
        }



        @Override
        public boolean canBeHighlighted() {
           return !isDisabled();
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            if (!isDisabled()) {
                super.onTakeItem(player, stack);
            }
        }

        @Override
        public ItemStack getStack() {
            ItemStack stack = super.getStack();
            if (isBackpackItem(stack)){
                backpackHandler.addDisableIndex(this.getIndex());
                return ItemStack.EMPTY;
            }else {
                return stack;
            }
        }
    }
}
