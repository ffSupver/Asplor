package com.ffsupver.asplor.item.item.singleItemCell;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.util.ConfigInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public class SingleItemCellInventory implements StorageCell {
    private boolean isPersisted = true;
    private final ISaveProvider container;
    private final ItemStack item;
    private long count;
    private Item storageItem;
    private Item configItem;
    private final ConfigInventory configInventory;
    private final long maxCount;
    public SingleItemCellInventory(ItemStack item, ISaveProvider container) {
        this.container = container;
        this.item = item;
        this.configInventory = ((SingleItemCellItem) item.getItem()).getConfigInventory(item);
        this.configItem = Items.AIR;
        this.maxCount = ((SingleItemCellItem) item.getItem()).getMaxStorageCount();
        readStorageFromItemStack(item);
    }

    public static SingleItemCellInventory createCellInventory(ItemStack item, ISaveProvider container){
        if (item.getItem() instanceof SingleItemCellItem){
            return new SingleItemCellInventory(item,container);
        }
        return null;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (this.storageItem != Items.AIR){
            AEItemKey key = AEItemKey.of(this.storageItem);
            out.add(key,this.count);
        }
        StorageCell.super.getAvailableStacks(out);
    }

    private void readStorageFromItemStack(ItemStack item){

        NbtCompound itemNbt = item.getOrCreateNbt();
         if (itemNbt.contains("item",10)) {
            this.storageItem = ItemStack.fromNbt(itemNbt.getCompound("item")).getItem();
        }else {
            this.storageItem = Items.AIR;
        }
        if (itemNbt.contains("count",4)){
            this.count = itemNbt.getLong("count");
        }else {
            this.count = 0;
        }


        if (!this.configInventory.isEmpty()){
            configItem =  ((AEItemKey)configInventory.getStack(0).what()).getItem();
            if (!configItem.equals(Items.AIR) && !configItem.equals(this.storageItem)) {
                this.storageItem = configItem;
                this.count = 0L;
            }
        }
    }


    private NbtCompound getTag() {
        return this.item.getOrCreateNbt();
    }

    @Override
    public CellState getStatus() {
        if (this.count <= 0){
            return CellState.EMPTY;
        }
        double state = 1f/this.count ;
        if (state > 0.00001){
            return CellState.NOT_EMPTY;
        }else if (state > 0.000000001){
            return CellState.TYPES_FULL;
        }else{
            return CellState.FULL;
        }
    }

    @Override
    public double getIdleDrain() {
        return 20;
    }

    @Override
    public void persist() {
        if (!this.isPersisted){
            getTag().remove("item");
            getTag().remove("count");
            if (this.configItem.equals(Items.AIR) && this.count <= 0){
                this.storageItem = Items.AIR;
            }
            if (!storageItem.equals(Items.AIR) && !(count <= 0)){
                getTag().put("item", storageItem.getDefaultStack().writeNbt(new NbtCompound()));
                getTag().putLong("count", count);
            }
            this.isPersisted = true;
        }
    }

    @Override
    public Text getDescription() {
        return item.getName();
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        System.out.println("insert "+what+" "+amount+" "+mode+" "+source+" fit "+storageItem);
        if ( what instanceof AEItemKey whatItem && !whatItem.hasTag()){
            boolean isEmpty = this.storageItem.equals(Items.AIR) &&  whatItem.getItem().getMaxCount() > 1;
            boolean sameItem = this.storageItem.equals(whatItem.getItem());
            if (mode == Actionable.MODULATE){
                if (isEmpty) {
                        this.storageItem = whatItem.getItem();
                        this.count = amount;

                } else if (sameItem) {
                    long newCount = this.count + amount;
                    this.count = Math.min(newCount, maxCount);
                }
                this.isPersisted = false;
            }


            return isEmpty || sameItem ? amount : 0L;
        }
        return 0;
    }

    public Item getStorageItem() {
        return storageItem;
    }

    public long getCount() {
        return count;
    }

    @Override
    public boolean canFitInsideCell() {
        return StorageCell.super.canFitInsideCell();
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        System.out.println("extract "+what+" "+amount+" "+mode+" "+source+" is air "
                +this.storageItem.equals(Items.AIR)+" ");
        if (what instanceof AEItemKey whatItem && (this.storageItem.equals(Items.AIR) || this.storageItem.equals(whatItem.getItem())) && !whatItem.hasTag()){
            if (whatItem.getItem().equals(this.storageItem)){
                long result = Math.min(amount, count);
                if (mode == Actionable.MODULATE){
                    if (amount < count) {
                        this.count -= amount;
                        this.isPersisted = false;
                    } else {
                        this.count = 0L;
                        this.isPersisted = false;
                    }
                }
                return result;
            }
        }
        return 0;
    }
}
