package com.ffsupver.asplor.block.liquid_blaze_burner;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankItem;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.AdventureUtil;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.annotation.MethodsReturnNonnullByDefault;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.function.Consumer;

import static com.ffsupver.asplor.AllBlockEntityTypes.LIQUID_BLAZE_BURNER_ENTITY;


@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LiquidBlazeBurner extends HorizontalFacingBlock implements IBE<LiquidBlazeBurnerEntity>, IWrenchable {

    public static final EnumProperty<BlazeBurnerBlock.HeatLevel> HEAT_LEVEL = EnumProperty.of("blaze", BlazeBurnerBlock.HeatLevel.class);

    public LiquidBlazeBurner(Settings properties) {
        super(properties);
        setDefaultState(getDefaultState().with(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.NONE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(HEAT_LEVEL, FACING);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        if (world.isClient)
            return;
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;

        Consumer<LiquidBlazeBurnerEntity> consumer = LiquidBlazeBurnerItem.IS_PLACING_NBT
                ? LiquidBlazeBurnerEntity::queueConnectivityUpdate
                : LiquidBlazeBurnerEntity::updateConnectivity;
        withBlockEntityDo(world, pos, consumer);

        BlockEntity blockEntity = world.getBlockEntity(pos.up());
        if (!(blockEntity instanceof BasinBlockEntity))
            return;
        BasinBlockEntity basin = (BasinBlockEntity) blockEntity;
        basin.notifyChangeOfContents();
    }

    @Override
    public Class<LiquidBlazeBurnerEntity> getBlockEntityClass() {
        return LiquidBlazeBurnerEntity.class;
    }

    @Override
    public BlockEntityType<? extends LiquidBlazeBurnerEntity> getBlockEntityType() {
        return LIQUID_BLAZE_BURNER_ENTITY.get();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return IBE.super.createBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                              BlockHitResult blockRayTraceResult) {
        ItemStack heldItem = player.getStackInHand(hand);
        BlazeBurnerBlock.HeatLevel heat = state.get(BlazeBurnerBlock.HEAT_LEVEL);

        if (AllItems.GOGGLES.isIn(heldItem) && heat != BlazeBurnerBlock.HeatLevel.NONE)
            return onBlockEntityUse(world, pos, bbte -> {
                if (bbte.goggles)
                    return ActionResult.PASS;
                bbte.goggles = true;
                bbte.notifyUpdate();
                return ActionResult.SUCCESS;
            });

        if (AdventureUtil.isAdventure(player))
            return ActionResult.PASS;

        if (heldItem.isEmpty() && heat != BlazeBurnerBlock.HeatLevel.NONE)
            return onBlockEntityUse(world, pos, bbte -> {
                if (!bbte.goggles)
                    return ActionResult.PASS;
                bbte.goggles = false;
                bbte.notifyUpdate();
                return ActionResult.SUCCESS;
            });


        boolean doNotConsume = player.isCreative();
        boolean forceOverflow = !(player instanceof FakePlayer);
        try (Transaction t = TransferUtil.getTransaction()) {
            TypedActionResult<ItemStack> res =
                    tryInsert(state, world, pos, heldItem, doNotConsume, forceOverflow, t);
            t.commit();
            ItemStack leftover = res.getValue();
            if (!world.isClient && !doNotConsume && !leftover.isEmpty()) {
                if (heldItem.isEmpty()) {
                    player.setStackInHand(hand, leftover);
                } else if (!player.getInventory()
                        .insertStack(leftover)) {
                    player.dropItem(leftover, false);
                }
            }

            return res.getResult() == ActionResult.SUCCESS ? ActionResult.SUCCESS : ActionResult.PASS;
        }
    }

    public static TypedActionResult<ItemStack> tryInsert(BlockState state, World world, BlockPos pos,
                                                         ItemStack stack, boolean doNotConsume, boolean forceOverflow, TransactionContext ctx) {
        if (!state.hasBlockEntity())
            return TypedActionResult.fail(ItemStack.EMPTY);

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof LiquidBlazeBurnerEntity))
            return TypedActionResult.fail(ItemStack.EMPTY);
        LiquidBlazeBurnerEntity burnerBE = (LiquidBlazeBurnerEntity) be;

        if (burnerBE.isCreativeFuel(stack)) {
            TransactionCallback.onSuccess(ctx, burnerBE::applyCreativeFuel);
            return TypedActionResult.success(ItemStack.EMPTY);
        }
        return TypedActionResult.fail(ItemStack.EMPTY);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        ItemStack stack = context.getStack();
        Item item = stack.getItem();
        BlockState defaultState = getDefaultState();
        if (!(item instanceof LiquidBlazeBurnerItem))
            return defaultState;
        BlazeBurnerBlock.HeatLevel initialHeat =
               BlazeBurnerBlock.HeatLevel.SMOULDERING;
        return defaultState.with(BlazeBurnerBlock.HEAT_LEVEL, initialHeat)
                .with(FACING, context.getHorizontalPlayerFacing()
                        .getOpposite());
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView reader, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(1, 0, 1, 15, 16, 15);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockView p_220071_2_, BlockPos p_220071_3_,
                                        ShapeContext p_220071_4_) {
        return getOutlineShape(p_220071_1_, p_220071_2_, p_220071_3_, p_220071_4_);
    }

    @Override
    public boolean hasComparatorOutput(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World p_180641_2_, BlockPos p_180641_3_) {
        return Math.max(0, state.get(BlazeBurnerBlock.HEAT_LEVEL)
                .ordinal() - 1);
    }
    public static boolean isTank(BlockState state) {
        return state.getBlock() instanceof LiquidBlazeBurner;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView reader, BlockPos pos, NavigationType type) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(10) != 0)
            return;
        if (!state.get(BlazeBurnerBlock.HEAT_LEVEL)
                .isAtLeast(BlazeBurnerBlock.HeatLevel.SMOULDERING))
            return;
        world.playSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F),
                (double) ((float) pos.getZ() + 0.5F), SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS,
                0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
    }

    public static BlazeBurnerBlock.HeatLevel getHeatLevelOf(BlockState blockState) {
        return blockState.contains(BlazeBurnerBlock.HEAT_LEVEL) ? blockState.get(BlazeBurnerBlock.HEAT_LEVEL)
                : BlazeBurnerBlock.HeatLevel.NONE;
    }

    public static int getLight(BlockState state) {
        BlazeBurnerBlock.HeatLevel level = state.get(BlazeBurnerBlock.HEAT_LEVEL);
        return switch (level) {
            case NONE -> 0;
            case SMOULDERING -> 8;
            default -> 15;
        };
    }


    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof LiquidBlazeBurnerEntity))
                return;
            LiquidBlazeBurnerEntity tankBE = (LiquidBlazeBurnerEntity) be;
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(tankBE);
        }
    }
}
