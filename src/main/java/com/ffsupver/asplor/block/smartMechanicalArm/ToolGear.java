package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ToolGear extends Block implements IBE<ToolGearEntity> {
    public ToolGear(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0,0,0,16/16.0,14/16.0,16/16.0);
    }

    @Override
    public Class<ToolGearEntity> getBlockEntityClass() {
        return ToolGearEntity.class;
    }

    @Override
    public BlockEntityType<? extends ToolGearEntity> getBlockEntityType() {
        return AllBlockEntityTypes.TOOL_GEAR_ENTITY.get();
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        IBE.onRemove(state, world, pos, newState);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.isSneaking()){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ToolGearEntity toolGearEntity) {
                   ItemStackHandler itemStackHandler = (ItemStackHandler) toolGearEntity.getItemStorage(hit.getSide());
                    if (itemStackHandler != null) {
                        if (player.getStackInHand(hand).isEmpty() && !itemStackHandler.empty()){
                            ItemStack getStack = ItemStack.EMPTY;
                            for (int i = 0;i < itemStackHandler.getSlotCount();i++){
                                if (!itemStackHandler.getStackInSlot(i).isEmpty()){
                                    getStack = itemStackHandler.getStackInSlot(i);
                                    break;
                                }
                            }
                            if (!getStack.isEmpty()){
                                try (Transaction t = Transaction.openOuter()) {
                                    if (itemStackHandler.extract(ItemVariant.of(getStack), getStack.getCount(), t) > 0) {
                                        player.setStackInHand(hand, getStack);
                                    }
                                    t.commit();
                                }
                                return ActionResult.SUCCESS;
                            }
                        }else {
                            ItemStack handItemStack = player.getStackInHand(hand);
                            long inserted;
                            try(Transaction t = Transaction.openOuter()) {
                                inserted =itemStackHandler.insert(ItemVariant.of(handItemStack),handItemStack.getCount(),t);
                                if (inserted > 0){
                                    handItemStack.setCount((int) (handItemStack.getCount() - inserted));
                                    player.setStackInHand(hand,handItemStack);
                                }
                                t.commit();
                            }
                            if (inserted > 0){
                                return ActionResult.SUCCESS;
                            }
                        }
                    }
            }
        }
        return ActionResult.PASS;
    }
}
