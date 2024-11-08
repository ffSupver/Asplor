package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.util.NbtUtil;
import com.ffsupver.asplor.util.RenderUtil;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmartMechanicalArmItem extends BlockItem {
    public SmartMechanicalArmItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        ItemStack itemStack = context.getStack();
        NbtCompound itemStackNbt = itemStack.getOrCreateNbt();
        PlayerEntity player = context.getPlayer();
        if (player != null && !player.isSneaking()) {
            if (blockEntity != null) {
                if (blockEntity instanceof DepotBlockEntity) {
                    itemStackNbt.put("target", NbtUtil.writeBlockPosToNbt(pos));
                    BlockPos oldBlockPos = new BlockPos(0, 0, 0);
                    if (itemStackNbt.contains("target", 10)) {
                        oldBlockPos = NbtUtil.readBlockPosFromNbt(itemStackNbt.getCompound("target"));
                    }
                    RenderUtil.addDescription(
                            itemStackNbt,
                            Text.translatable("description.asplor.smart_mechanical_arm.target").append(Text.translatable("description.asplor.location", pos.getX(), pos.getY(), pos.getZ())),
                            Text.translatable("description.asplor.smart_mechanical_arm.target").append(Text.translatable("description.asplor.location", oldBlockPos.getX(), oldBlockPos.getY(), oldBlockPos.getZ()))
                    );
                    itemStack.setNbt(itemStackNbt);
                    player.setStackInHand(context.getHand(), itemStack);
                    return ActionResult.SUCCESS;
                }
                if (blockEntity instanceof ToolGearEntity) {
                    NbtList toolList = new NbtList();
                    if (itemStackNbt.contains("tools", 9)) {
                        toolList = itemStackNbt.getList("tools", 10);

                    }
                    RenderUtil.addDescription(itemStackNbt,
                            Text.translatable("description.asplor.smart_mechanical_arm.tools").append(Text.translatable("description.asplor.location", pos.getX(), pos.getY(), pos.getZ())),
                            Text.translatable("description.asplor.smart_mechanical_arm.tools").append(Text.translatable("description.asplor.location", pos.getX(), pos.getY(), pos.getZ()))
                    );
                    NbtCompound toolPosNbt = NbtUtil.writeBlockPosToNbt(pos);
                    if (!toolList.contains(toolPosNbt)) {
                        toolList.add(toolPosNbt);
                    }
                    itemStackNbt.put("tools", toolList);
                    itemStack.setNbt(itemStackNbt);
                    player.setStackInHand(context.getHand(), itemStack);
                    return ActionResult.SUCCESS;
                }
            }
        }else {
            NbtCompound emptyNbt = new NbtCompound();
            itemStack.setNbt(emptyNbt);
            player.setStackInHand(context.getHand(),itemStack);
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }

    @Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        if (super.place(context, state)){
            BlockPos pos = context.getBlockPos();
            if (context.getWorld().getBlockEntity(pos) instanceof SmartMechanicalArmEntity smartMechanicalArmEntity) {
                NbtCompound itemStackNbt = context.getStack().getOrCreateNbt();
                if (itemStackNbt.contains("tools", 9)) {
                    NbtList toolNbtList = itemStackNbt.getList("tools",10);
                    for (NbtElement element : toolNbtList){
                        BlockPos toolPos = NbtUtil.readBlockPosFromNbt((NbtCompound) element);
                        if (pos.getY() == toolPos.getY() && (Math.abs(pos.getZ() - toolPos.getZ())+Math.abs(pos.getX() - toolPos.getX()) < 5)){
                            smartMechanicalArmEntity.addToolPos(toolPos);
                        }
                    }
                }
                if (itemStackNbt.contains("target",10)){
                    BlockPos targetPos = NbtUtil.readBlockPosFromNbt(itemStackNbt.getCompound("target"));
                    if (pos.getY() == targetPos.getY() && (Math.abs(pos.getZ() - targetPos.getZ())+Math.abs(pos.getX() - targetPos.getX()) < 5)){
                        smartMechanicalArmEntity.setTargetPos(targetPos);
                    }

                }
            }
            return true;
        }
        return false;
    }
}
