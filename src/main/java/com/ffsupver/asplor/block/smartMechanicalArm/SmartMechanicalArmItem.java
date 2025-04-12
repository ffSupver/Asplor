package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.block.alloyDepot.AlloyDepotEntity;
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
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                if (blockEntity instanceof DepotBlockEntity || blockEntity instanceof AlloyDepotEntity) {
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

                List<BlockPos> toolPosList = getToolPosList(itemStackNbt);
                for (BlockPos toolPos : toolPosList){
                    if (pos.getY() == toolPos.getY() && (Math.abs(pos.getZ() - toolPos.getZ())+Math.abs(pos.getX() - toolPos.getX()) < 5)){
                        smartMechanicalArmEntity.addToolPos(toolPos);
                    }
                }


                Optional<BlockPos> targetPosOptional = getTargetPos(itemStackNbt);
                targetPosOptional.ifPresent(targetPos->{
                    if (pos.getY() == targetPos.getY() && (Math.abs(pos.getZ() - targetPos.getZ())+Math.abs(pos.getX() - targetPos.getX()) < 5)){
                        smartMechanicalArmEntity.setTargetPos(targetPos);
                    }
                });
            }


            ItemStack itemStack = context.getStack();
            PlayerEntity player = context.getPlayer();
            if (itemStack.isOf(AllBlocks.SMART_MECHANICAL_ARM.asItem())){
                itemStack.removeSubNbt("tools");
                itemStack.removeSubNbt("target");
                itemStack.removeSubNbt("display");
            }
            if (player != null){
                player.setStackInHand(context.getHand(), itemStack);
            }
            return true;
        }
        return false;
    }

    private static List<BlockPos> getToolPosList(NbtCompound nbt){
        List<BlockPos> result = new ArrayList<>();
        if (nbt.contains("tools", 9)) {
            NbtList toolNbtList = nbt.getList("tools",10);
            result.addAll(NbtUtil.readBlockPosListFromNbt(toolNbtList));
        }
        return result;
    }

    private static Optional<BlockPos> getTargetPos(NbtCompound nbt){
        if (nbt.contains("target",10)){
            BlockPos targetPos = NbtUtil.readBlockPosFromNbt(nbt.getCompound("target"));
            return Optional.of(targetPos);
        }
        return Optional.empty();
    }
}
