package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SchematicItem extends Item {
    public SchematicItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("description.asplor.schematic").formatted(Formatting.GRAY)
                .append(Text.translatable("description.asplor.schematic."+getSchematicFromItem(stack)).formatted(Formatting.WHITE)));
    }

    public static @Nullable String getSchematicFromItem(ItemStack itemStack){
        NbtCompound nbt = itemStack.getOrCreateNbt();
        if (nbt.contains("schematic", NbtElement.STRING_TYPE)){
            return nbt.getString("schematic");
        }
        return null;
    }

    public static ItemStack getSchematicItem(String schematic){
        ItemStack itemStack = ModItems.SCHEMATIC.getDefaultStack();
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putString("schematic",schematic);
        itemStack.setNbt(nbt);
        return itemStack;
    }
    public static ItemStack getSchematicShardItem(String schematic){
        ItemStack itemStack = ModItems.SCHEMATIC_SHARD.getDefaultStack();
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putString("schematic",schematic);
        itemStack.setNbt(nbt);
        return itemStack;
    }
}
