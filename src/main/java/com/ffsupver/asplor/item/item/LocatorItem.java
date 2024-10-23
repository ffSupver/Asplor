package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.util.RenderUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class LocatorItem extends Item {
    public static final String LOCATION_DATA_KEY = "location";
    public LocatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getPlayer().isSneaking() && context.getWorld().getDimensionKey().getValue().equals(new Identifier("minecraft","overworld"))){
            ItemStack itemStack = context.getStack();


            NbtCompound originNbt = itemStack.getOrCreateNbt();

            //读取先前存储的数据
            BlockPos originBlockPos = new BlockPos(0,0,0);
            if (originNbt.contains(LOCATION_DATA_KEY,10)){
                originBlockPos = new BlockPos(
                        originNbt.getCompound(LOCATION_DATA_KEY).getInt("x"),
                        originNbt.getCompound(LOCATION_DATA_KEY).getInt("y"),
                        originNbt.getCompound(LOCATION_DATA_KEY).getInt("z")
                );
            }

            BlockPos targetPos = context.getBlockPos();
            NbtCompound targetPosNbt = new NbtCompound();
            targetPosNbt.putInt("x",targetPos.getX());
            targetPosNbt.putInt("y",targetPos.getY() + 1);
            targetPosNbt.putInt("z",targetPos.getZ());

            originNbt.put(LOCATION_DATA_KEY,targetPosNbt);
            itemStack.setNbt(originNbt);
            context.getPlayer().setStackInHand(context.getHand(),itemStack);

            //加入描述
            RenderUtil.addDescription(
                    originNbt,
                    Text.translatable("description.asplor.location",targetPos.getX(),targetPos.getY(),targetPos.getZ()).formatted(Formatting.AQUA),
                    Text.translatable("description.asplor.location",originBlockPos.getX(),originBlockPos.getY(),originBlockPos.getZ()).formatted(Formatting.AQUA)
            );

        }
        return super.useOnBlock(context);
    }
}
