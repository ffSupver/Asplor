package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.block.smartMechanicalArm.ToolType;
import com.ffsupver.asplor.util.RenderUtil;
import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ToolItem extends Item {
    private PartialModel toolModel;
    private PartialModel toolWorkModel;
    private Identifier toolTypeId;
    private final int maxUsage;
    public static final String USAGE_DATA_KEY = "usage";

    public ToolItem(Settings settings, Identifier toolTypeId, int maxUsage) {
        super(settings.maxCount(1));
        this.toolModel = null;
        this.toolWorkModel = null;
        this.toolTypeId = toolTypeId;
        this.maxUsage = maxUsage;
    }

    public Identifier getToolTypeId() {
        return toolTypeId;
    }

    public int getMaxUsage() {
        return maxUsage;
    }

    public ItemStack setUse(ItemStack origin , int useRemain){
        NbtCompound originalNbt = origin.getOrCreateNbt();
        if (originalNbt.contains(USAGE_DATA_KEY,10)){
            int maxUsage = originalNbt.getCompound(USAGE_DATA_KEY).getInt("max");
            return setUsageData(origin,useRemain,maxUsage);
        }else if (origin.getItem() instanceof ToolItem toolItem){
            int maxUsage = toolItem.getMaxUsage();
            return setUsageData(origin,useRemain,maxUsage);
        }
        return origin;
    }

    public int getUse(ItemStack itemStack){
        NbtCompound nbt = itemStack.getOrCreateNbt();
        if (nbt.contains(USAGE_DATA_KEY,10)){
            return nbt.getCompound(USAGE_DATA_KEY).getInt("use");
        }else if (itemStack.getItem() instanceof ToolItem toolItem){
            return toolItem.getMaxUsage();
        }
        return 0;
    }

    private ItemStack setUsageData(ItemStack input,int usage,int maxUsage){
        NbtCompound inputNbt = input.getOrCreateNbt();
        int originUsage = 0;
        int originMaxUsage = 0;
        if (inputNbt.contains(USAGE_DATA_KEY,10)){
            NbtCompound originUsageNbt = inputNbt.getCompound(USAGE_DATA_KEY);
            originUsage = originUsageNbt.getInt("use");
            originMaxUsage = originUsageNbt.getInt("max");
        }
        NbtCompound usageData = new NbtCompound();
        usageData.putInt("max",maxUsage);
        usageData.putInt("use",Math.max(usage , 0));
        inputNbt.put(USAGE_DATA_KEY,usageData);
        input.setNbt(inputNbt);
        RenderUtil.addDescription(inputNbt,
                Text.translatable("description.asplor.tool_item.usage",usage,maxUsage).formatted(Formatting.AQUA),
                Text.translatable("description.asplor.tool_item.usage",originUsage,originMaxUsage).formatted(Formatting.AQUA));
        if (usage <= 0 && input.getItem() instanceof ToolItem toolItem){
            ToolType toolType = ToolType.getById(toolItem.getToolTypeId());
            return new ItemStack(toolType.getUsedToolItem(),1);
        }
        return input;
    }


    public void setModel(PartialModel toolModel,PartialModel toolWorkModel){
        this.toolModel=toolModel;
        this.toolWorkModel=toolWorkModel;
    }

    public PartialModel getToolModel(){
        return toolModel;
    }

    public PartialModel getToolWorkModel(){
        return toolWorkModel;
    }
}
