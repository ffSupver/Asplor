package com.ffsupver.asplor.item.item.singleItemCell;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.AEKeyFilter;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.items.storage.StorageCellTooltipComponent;
import appeng.util.ConfigInventory;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
                    tooltip.add(Text.translatable(item.getTranslationKey()).formatted(cellInventory.hasConfigItem() ? Formatting.GREEN : Formatting.AQUA)
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
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        List<GenericStack> content = new ArrayList<>();
        MEStorage inv = getConfigInventory(stack);

        StorageCell cell = StorageCells.getCellInventory(stack,null);
        if (cell instanceof SingleItemCellInventory singleCell){
            KeyCounter keyCounter = new KeyCounter();
            singleCell.getAvailableStacks(keyCounter);
            Iterator<Object2LongMap.Entry<AEKey>> itemIterator = keyCounter.iterator();
            if (itemIterator.hasNext()) {
                Object2LongMap.Entry<AEKey> entry = itemIterator.next();
                content.add(new GenericStack(entry.getKey(), entry.getLongValue()));
            }
        }else {
            Iterator<Object2LongMap.Entry<AEKey>> it = inv.getAvailableStacks().iterator();
            if (it.hasNext()) {
                Object2LongMap.Entry<AEKey> entry = it.next();
                content.add(new GenericStack(entry.getKey(), entry.getLongValue()));

            }
        }

        return Optional.of(new StorageCellTooltipComponent(List.of(),content,false,true));
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(IsItemFilter.INSTANCE,is,1);
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

    public static class IsItemFilter implements AEKeyFilter{
        public static IsItemFilter INSTANCE = new IsItemFilter();

        @Override
        public boolean matches(AEKey what) {
            return what instanceof AEItemKey;
        }
    }
}
