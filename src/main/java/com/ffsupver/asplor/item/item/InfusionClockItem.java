package com.ffsupver.asplor.item.item;

import appeng.core.definitions.AEBlocks;
import appeng.decorative.solid.BuddingCertusQuartzBlock;
import com.ffsupver.asplor.block.blocks.UnstableRock;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static appeng.decorative.solid.BuddingCertusQuartzBlock.canClusterGrowAtState;
import static net.minecraft.block.BuddingAmethystBlock.canGrowIn;


public class InfusionClockItem extends Item {
    public InfusionClockItem(Settings settings) {
        super(settings.maxCount(1).maxDamage(16));
    }

    @Override
    public boolean isDamageable() {
        return true;
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState =context.getWorld().getBlockState(blockPos);
        World world = context.getWorld();
        if (blockState.getBlock() instanceof Fertilizable fertilizable){
                if (fertilizable.isFertilizable(world, blockPos, blockState, world.isClient)) {
                    if (world instanceof ServerWorld) {
                        if (fertilizable.canGrow(world, world.random, blockPos, blockState)) {
                            fertilizable.grow((ServerWorld)world, world.random, blockPos, blockState);
                        }
                        decreaseItem(context);
                    }
                }
            return ActionResult.SUCCESS;
        }
        if (blockState.getBlock() instanceof SugarCaneBlock && growSugarCane(world,blockPos,blockState)){
                decreaseItem(context);
                return ActionResult.SUCCESS;
        }
        if (blockState.getBlock() instanceof CactusBlock && growCactus(world,blockPos,blockState)){
                decreaseItem(context);
                return ActionResult.SUCCESS;
        }
        if (blockState.getBlock() instanceof NetherWartBlock && growNetherWart(world,blockState,blockPos)){
            decreaseItem(context);
            return ActionResult.SUCCESS;
        }
        if (blockState.getBlock() instanceof BuddingAmethystBlock buddingAmethystBlock && growAmethyst(blockPos,world,buddingAmethystBlock)){
            decreaseItem(context);
            return ActionResult.SUCCESS;
        }
        if (blockState.getBlock() instanceof BuddingCertusQuartzBlock && growCertusQuartz(blockPos,world)){
            decreaseItem(context);
            return ActionResult.SUCCESS;
        }
        if (blockState.getBlock() instanceof UnstableRock unstableRock){
            if (unstableRock.turn(world,blockPos,blockState)){
                decreaseItem(context);
                return ActionResult.SUCCESS;
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof AnimalEntity animalEntity){
            if (animalEntity.isBaby()){
                animalEntity.growUp(-PassiveEntity.BABY_AGE);
                decreaseItem(user,hand,stack);
                return ActionResult.SUCCESS;
            }
        }
        return super.useOnEntity(stack, user, entity, hand);
    }


    private void decreaseItem(PlayerEntity player,Hand hand,ItemStack itemStack){
        if (!player.isCreative()){
            if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                damageItem(itemStack, serverPlayerEntity, serverPlayerEntity.getWorld().getRandom());
            }
            player.setStackInHand(hand, itemStack);
        }
    }
    private void decreaseItem(ItemUsageContext context){
        if (!context.getPlayer().isCreative()){
            ItemStack itemStack = context.getPlayer().getStackInHand(context.getHand());
            if (context.getPlayer() instanceof ServerPlayerEntity serverPlayerEntity) {
                itemStack = damageItem(itemStack, serverPlayerEntity, context.getWorld().getRandom());
            }
            context.getPlayer().setStackInHand(context.getHand(), itemStack);
        }
    }

    private ItemStack damageItem(ItemStack itemStack, ServerPlayerEntity serverPlayerEntity, Random random){
        if (itemStack.getDamage() < itemStack.getMaxDamage()){
            itemStack.damage(1, random, serverPlayerEntity);
        }else {
            itemStack = ItemStack.EMPTY;
        }
        return itemStack;
    }

    private boolean growSugarCane(World world,BlockPos pos,BlockState state){
            int i;
            for(i = 1; world.getBlockState(pos.down(i)).isOf(Blocks.SUGAR_CANE); ++i) {
            }
            for (int j = 0;j<3;j++){
                if(world.getBlockState(pos.up()).isOf(Blocks.SUGAR_CANE)){
                    i++;
                    pos = pos.up();
                }else {
                    break;
                }
            }
        if (world.isAir(pos.up())) {
            if (i < 3) {
                world.setBlockState(pos.up(), state.getBlock().getDefaultState());
                world.setBlockState(pos, state.with(SugarCaneBlock.AGE, 0), 4);
                return true;
            }
        }
        return false;
    }
    private boolean growCactus(World world,BlockPos pos,BlockState state){

        int i;
        for(i = 1; world.getBlockState(pos.down(i)).isOf(Blocks.CACTUS); ++i) {
        }
        for (int j = 0;j<3;j++){
            if(world.getBlockState(pos.up()).isOf(Blocks.CACTUS)){
                i++;
                pos = pos.up();
            }else {
                break;
            }
        }
        if (world.isAir(pos.up())) {
            if (i < 3) {
                world.setBlockState(pos.up(), state.getBlock().getDefaultState());
                BlockState blockState = state.with(CactusBlock.AGE, 0);
                world.setBlockState(pos, blockState, 4);
                world.updateNeighbor(blockState, pos, state.getBlock(), pos, false);
                return true;
            }
        }
        return false;
    }
    private boolean growNetherWart(World world,BlockState state,BlockPos pos) {
            int i = state.get(NetherWartBlock.AGE);
            if (i < 3) {
                state = state.with(NetherWartBlock.AGE, i + 1);
                world.setBlockState(pos, state, 2);
                return true;
            }
        return false;
    }
    private boolean growAmethyst(BlockPos pos,World world,BuddingAmethystBlock buddingAmethystBlock){
        boolean grown = false;

        for (Direction direction : Direction.values()){
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            Block block = null;
            if (canGrowIn(blockState)) {
                block = Blocks.SMALL_AMETHYST_BUD;
            } else if (blockState.isOf(Blocks.SMALL_AMETHYST_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                block = Blocks.MEDIUM_AMETHYST_BUD;
            } else if (blockState.isOf(Blocks.MEDIUM_AMETHYST_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                block = Blocks.LARGE_AMETHYST_BUD;
            } else if (blockState.isOf(Blocks.LARGE_AMETHYST_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                block = Blocks.AMETHYST_CLUSTER;
            }


            if (block != null) {
                BlockState blockState2 = block.getDefaultState().with(AmethystClusterBlock.FACING, direction).with(AmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);
                world.setBlockState(blockPos, blockState2);
                grown = true;
            }
        }
        return grown;
    }
    private boolean growCertusQuartz(BlockPos pos,World level){
        boolean grow = false;

        Block block = level.getBlockState(pos).getBlock();
        for (Direction direction : Direction.values()){
            BlockPos targetPos = pos.offset(direction);
            BlockState targetState = level.getBlockState(targetPos);
            Block newCluster = null;
            if (canClusterGrowAtState(targetState)) {
                newCluster = AEBlocks.SMALL_QUARTZ_BUD.block();
            } else if (targetState.isOf(AEBlocks.SMALL_QUARTZ_BUD.block()) && targetState.get(AmethystClusterBlock.FACING) == direction) {
                newCluster = AEBlocks.MEDIUM_QUARTZ_BUD.block();
            } else if (targetState.isOf(AEBlocks.MEDIUM_QUARTZ_BUD.block()) && targetState.get(AmethystClusterBlock.FACING) == direction) {
                newCluster = AEBlocks.LARGE_QUARTZ_BUD.block();
            } else if (targetState.isOf(AEBlocks.LARGE_QUARTZ_BUD.block()) && targetState.get(AmethystClusterBlock.FACING) == direction) {
                newCluster = AEBlocks.QUARTZ_CLUSTER.block();
            }

            if (newCluster != null) {
                BlockState newClusterState = newCluster.getDefaultState().with(AmethystClusterBlock.FACING, direction).with(AmethystClusterBlock.WATERLOGGED, targetState.getFluidState().getFluid() == Fluids.WATER);
                level.setBlockState(targetPos, newClusterState);


                grow = true;

                if (block != AEBlocks.FLAWLESS_BUDDING_QUARTZ.block() && level.getRandom().nextInt(12) == 0) {
                    Block newBlock;
                    if (block == AEBlocks.FLAWED_BUDDING_QUARTZ.block()) {
                        newBlock = AEBlocks.CHIPPED_BUDDING_QUARTZ.block();
                    } else if (block == AEBlocks.CHIPPED_BUDDING_QUARTZ.block()) {
                        newBlock = AEBlocks.DAMAGED_BUDDING_QUARTZ.block();
                    } else {
                        if (block != AEBlocks.DAMAGED_BUDDING_QUARTZ.block()) {
                            throw new IllegalStateException("Unexpected block: " + block);
                        }

                        newBlock = AEBlocks.QUARTZ_BLOCK.block();
                    }

                    level.setBlockState(pos, newBlock.getDefaultState());
                }
            }
        }
        return grow;
    }
}
