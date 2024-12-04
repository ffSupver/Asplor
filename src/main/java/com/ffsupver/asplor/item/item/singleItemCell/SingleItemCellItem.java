package com.ffsupver.asplor.item.item.singleItemCell;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.util.ConfigInventory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SingleItemCellItem extends AEBaseItem implements ICellWorkbenchItem {
    private final long maxStorageCount;
    private final double idleDrain;
    public SingleItemCellItem(Settings properties, long maxCountK, double idleDrain) {
        super(properties);
        this.maxStorageCount = maxCountK * 1024 * 8;
        this.idleDrain = idleDrain;
    }

    public double getIdleDrain() {
        return idleDrain;
    }

    public long getMaxStorageCount() {
        return maxStorageCount;
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack is) {
        String fz = is.getOrCreateNbt().getString("FuzzyMode");
        if (fz.isEmpty()) {
            return FuzzyMode.IGNORE_ALL;
        } else {
            try {
                return FuzzyMode.valueOf(fz);
            } catch (Throwable var4) {
                return FuzzyMode.IGNORE_ALL;
            }
        }
    }

    @Override
    public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
        is.getOrCreateNbt().putString("FuzzyMode", fzMode.name());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        StorageCell inventory = StorageCells.getCellInventory(stack, null);
        if (inventory != null) {
            if (inventory instanceof SingleItemCellInventory cellInventory){
               Item item = cellInventory.getStorageItem();
               long count = cellInventory.getCount();
                if (item != Items.AIR){
                    tooltip.add(Text.translatable(item.getTranslationKey()).formatted(Formatting.AQUA)
                                    .append(Text.literal(" : ").formatted(Formatting.GOLD))
                            .append(Text.literal(String.valueOf(count)).formatted(Formatting.WHITE))
                            .append(Text.literal("/").formatted(Formatting.GOLD))
                            .append(Text.literal(String.valueOf(maxStorageCount)).formatted(Formatting.WHITE))
                    );
                }
            }
        }
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(null,is,1);
    }

    public static class CellHandler implements ICellHandler {

        @Override
        public boolean isCell(ItemStack itemStack) {
            return itemStack.getItem() instanceof SingleItemCellItem;
        }

        @Override
        public @Nullable StorageCell getCellInventory(ItemStack itemStack, @Nullable ISaveProvider iSaveProvider) {
            if (isCell(itemStack)){
               return SingleItemCellInventory.createCellInventory(itemStack,iSaveProvider);
            }
            return null;
        }
    }
}
