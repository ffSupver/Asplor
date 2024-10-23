package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.util.RenderUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class SpaceTeleporterAnchor extends Item {
    public SpaceTeleporterAnchor(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer().isSneaking()){
            ItemStack itemStack = context.getStack();
            if (itemStack.isOf(ModItems.SPACE_TELEPORTER_ANCHOR)){
                NbtCompound originNbt = itemStack.getOrCreateNbt();

                //读取先前存储的数据
                BlockPos originBlockPos = new BlockPos(0,0,0);
                String orginDimensionId = "minecraft:none";
                if (originNbt.contains("anchor",10)){
                    originBlockPos = new BlockPos(
                            originNbt.getCompound("anchor").getInt("x"),
                            originNbt.getCompound("anchor").getInt("y"),
                            originNbt.getCompound("anchor").getInt("z")
                    );
                    orginDimensionId = originNbt.getCompound("anchor").getString("dimension");
                }


                BlockPos posToAnchor = context.getBlockPos();
                NbtCompound blockPos = new NbtCompound();
                String dimensionId = context.getWorld().getRegistryKey().getValue().toString();
                blockPos.putInt("x",posToAnchor.getX());
                blockPos.putInt("y",posToAnchor.getY());
                blockPos.putInt("z",posToAnchor.getZ());
                blockPos.putString("dimension",dimensionId);
                originNbt.put("anchor",blockPos);
                itemStack.setNbt(originNbt);
                context.getPlayer().setStackInHand(context.getHand(),itemStack);
                //加入描述
                RenderUtil.addDescription(
                        originNbt,
                        Text.translatable("description.asplor.anchor",dimensionId,posToAnchor.getX(),posToAnchor.getY(),posToAnchor.getZ()).formatted(Formatting.AQUA),
                        Text.translatable("description.asplor.anchor",orginDimensionId,originBlockPos.getX(),originBlockPos.getY(),originBlockPos.getZ()).formatted(Formatting.AQUA)
                );
                return ActionResult.SUCCESS;
            }
        }
        return super.useOnBlock(context);
    }

}
