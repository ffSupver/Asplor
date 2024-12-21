package com.ffsupver.asplor.block.airlockSwitch;

import com.ffsupver.asplor.util.NbtUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AirlockSwitchItem extends BlockItem {
    public AirlockSwitchItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();
        if (player != null && !player.isSneaking() && context.getWorld().getBlockEntity(pos) instanceof AirlockSwitchEntity){
                NbtCompound nbt = stack.getOrCreateNbt();
                nbt.put("pair", NbtUtil.writeBlockPosToNbt(pos));
                stack.setNbt(nbt);
                player.setStackInHand(context.getHand(),stack);
                return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }

    @Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        boolean placed = super.place(context, state);
        ItemStack stack = context.getStack();
        NbtCompound stackNbt = stack.getOrCreateNbt();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        if (!world.isClient() && placed && stackNbt.contains("pair",10)){
           if (world.getBlockEntity(pos) instanceof AirlockSwitchEntity airlockSwitchEntity){
               BlockPos pairPos = NbtUtil.readBlockPosFromNbt(stackNbt.getCompound("pair"));
               airlockSwitchEntity.setPairPos(pairPos);
               if (world.getBlockEntity(pairPos) instanceof AirlockSwitchEntity pairEntity){
                   pairEntity.setPairPos(pos);
               }
               stack.removeSubNbt("pair");
               context.getPlayer().setStackInHand(context.getHand(),stack);
           }
        }
        return placed;
    }
}
