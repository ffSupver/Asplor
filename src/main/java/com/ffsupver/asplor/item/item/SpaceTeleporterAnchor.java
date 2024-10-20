package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.screen.assembler.AssemblerScreenHandler;
import net.minecraft.client.font.Font;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.text.Format;

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
                addDescription(
                        originNbt,
                        Text.translatable("description.asplor.anchor",dimensionId,posToAnchor.getX(),posToAnchor.getY(),posToAnchor.getZ()).formatted(Formatting.AQUA),
                        Text.translatable("description.asplor.anchor",orginDimensionId,originBlockPos.getX(),originBlockPos.getY(),originBlockPos.getZ()).formatted(Formatting.AQUA)
                );
                return ActionResult.SUCCESS;
            }
        }
        return super.useOnBlock(context);
    }
    private NbtCompound addDescription(NbtCompound nbt, Text text, Text textToReplace){
        NbtCompound displayNBT = nbt.getCompound("display");
        NbtList loreList = displayNBT.contains("Lore", 9) ? displayNBT.getList("Lore", 8) : new NbtList();
        if (!textToReplace.equals(Text.empty())){
            for (int i = 0; i < loreList.size(); i++) {
                String existingText =loreList.getString(i);
                if (existingText != null && existingText.equals(Text.Serializer.toJson(textToReplace))) {
                    loreList.set(i, NbtString.of(Text.Serializer.toJson(text))); // 替换为新文本
                    return nbt; // 替换后直接返回
                }
            }
        }

        loreList.add(NbtString.of(Text.Serializer.toJson(text)));
        displayNBT.put("Lore", loreList);
        nbt.put("display", displayNBT);
        return nbt;
    }
}
