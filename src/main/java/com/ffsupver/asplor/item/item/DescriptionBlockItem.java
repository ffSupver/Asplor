package com.ffsupver.asplor.item.item;

import earth.terrarium.adastra.common.utils.TooltipUtils;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DescriptionBlockItem extends BlockItem {
    private final Text description;
    public DescriptionBlockItem(Block block, Settings settings,Text description) {
        super(block, settings);
        this.description = description;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        TooltipUtils.addDescriptionComponent(tooltip,description.copy().formatted(Formatting.GRAY));
    }
}
