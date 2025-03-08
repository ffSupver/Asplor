package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.networking.packet.worldAdder.PlanetCreatingData;
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

public class MeteoriteFragmentItem extends Item {
    public static final String DATA_KEY = "planet";
    public MeteoriteFragmentItem(Settings settings) {
        super(settings);
    }

    public ItemStack putPlanetData(ItemStack itemStack,PlanetCreatingData planetCreatingData){
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.put(DATA_KEY,planetCreatingData.toNbt());
        itemStack.setNbt(nbt);
        return itemStack;
    }

    public PlanetCreatingData getPlanetData(ItemStack itemStack){
        NbtCompound nbt = itemStack.getOrCreateNbt();
        PlanetCreatingData planetCreatingData = new PlanetCreatingData();
        if (nbt.contains(DATA_KEY, NbtElement.COMPOUND_TYPE)){
            NbtCompound pdNbt = nbt.getCompound(DATA_KEY);
            planetCreatingData.fromNbt(pdNbt);
        }
        return planetCreatingData;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        PlanetCreatingData planetCreatingData = getPlanetData(stack);
        if (planetCreatingData.oxygen != null){
            tooltip.add(Text.translatable( planetCreatingData.oxygen ? "description.asplor.meteorite.has_oxygen" : "description.asplor.meteorite.no_oxygen").formatted(Formatting.GRAY));
        }
        if (planetCreatingData.temperature != null){
            tooltip.add(Text.translatable( "description.asplor.meteorite.temperature",planetCreatingData.temperature).formatted(Formatting.GRAY));
        }
        if (planetCreatingData.gravity != null){
            tooltip.add(Text.translatable( "description.asplor.meteorite.gravity",planetCreatingData.gravity).formatted(Formatting.GRAY));
        }
        if (planetCreatingData.solarPower != null){
            tooltip.add(Text.translatable( "description.asplor.meteorite.solar_power",planetCreatingData.solarPower).formatted(Formatting.GRAY));
        }
    }
}
