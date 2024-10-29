package com.ffsupver.asplor.block.alloyChest;

import com.ffsupver.asplor.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AlloyChest extends BlockWithEntity implements BlockEntityProvider {
    public static final DirectionProperty FACING;
    public static final BooleanProperty OPEN;
    public AlloyChest(Settings settings) {

        super(settings);
        this.setDefaultState(((BlockState) this.stateManager.getDefaultState()).with(FACING, Direction.NORTH).with(OPEN,false));
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AlloyChestEntity(pos,state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AlloyChestEntity) {
                ItemScatterer.spawn(world, pos, (AlloyChestEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(itemStack.hasCustomName()){
            BlockEntity blockEntity= world.getBlockEntity(pos);
            if(blockEntity instanceof AlloyChestEntity){
                 ((AlloyChestEntity) blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING,ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            AlloyChestEntity alloyChestEntity = (AlloyChestEntity)blockEntity;
            ItemStack handItemStack = player.getStackInHand(hand);

            if (handItemStack.isOf(ModItems.PACKER.get())){
                if (alloyChestEntity instanceof AlloyChestEntity){
                    if (!player.isCreative()){
                        handItemStack.setCount(handItemStack.getCount() - 1);
                        player.setStackInHand(hand, handItemStack);
                    }
                    return alloyChestEntity.turnIntoEntity();
                }
                return ActionResult.PASS;
            }else {
                NamedScreenHandlerFactory screenHandlerFactory= alloyChestEntity;

                if (screenHandlerFactory!=null){
                    player.openHandledScreen(screenHandlerFactory);
                    player.incrementStat(Stats.OPEN_ENDERCHEST);
                    PiglinBrain.onGuardedBlockInteracted(player,true);
                }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState) state.with(FACING,rotation.rotate((Direction) state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING,OPEN});
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AlloyChestEntity){
           ((AlloyChestEntity) blockEntity).tick();
        }
    }

    static {
        FACING= Properties.FACING;
        OPEN=Properties.OPEN;
    }
}
