package com.ffsupver.asplor.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;


public class RenderUtil {
    public static Box createRenderBoundingBox(BlockPos pos, Double renderDistance) {
        return new Box(pos.getX()+0.5+renderDistance,pos.getY()+0.5+renderDistance,pos.getZ()+0.5+renderDistance,
                pos.getX()+0.5-renderDistance,pos.getY()+0.5-renderDistance,pos.getZ()+0.5-renderDistance);
    }


    public static NbtCompound addDescription(NbtCompound nbt, Text text, Text textToReplace){
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
