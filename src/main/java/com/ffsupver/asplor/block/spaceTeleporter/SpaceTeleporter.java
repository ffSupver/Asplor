package com.ffsupver.asplor.block.spaceTeleporter;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.ffsupver.asplor.item.ModItems;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpaceTeleporter extends Block implements IBE<SpaceTeleporterEntity> {
    public SpaceTeleporter(Settings settings) {
        super(settings);
    }
    @Override
    public Class<SpaceTeleporterEntity> getBlockEntityClass() {
        return SpaceTeleporterEntity.class;
    }

    @Override
    public BlockEntityType<? extends SpaceTeleporterEntity> getBlockEntityType() {
        return AllBlockEntityTypes.SPACE_TELEPORTER_ENTITY.get();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack handItemStack =player.getStackInHand(hand);
        if (handItemStack.isOf(ModItems.SPACE_TELEPORTER_ANCHOR)&&
                handItemStack.hasNbt()&&handItemStack.getNbt().contains("anchor",10)){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SpaceTeleporterEntity) {
                SpaceTeleporterEntity spaceTeleporter = (SpaceTeleporterEntity) blockEntity;
                NbtCompound anchorNbt = handItemStack.getNbt().getCompound("anchor");
                BlockPos posFromItem = new BlockPos(anchorNbt.getInt("x"), anchorNbt.getInt("y"), anchorNbt.getInt("z"));
                String dimensionIdFromItem = anchorNbt.getString("dimension");
                spaceTeleporter.setTargetPos(posFromItem);
                spaceTeleporter.setTargetDimension(dimensionIdFromItem);
                if (!player.isCreative()) {
                    player.setStackInHand(hand, ItemStack.EMPTY);
                }
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
