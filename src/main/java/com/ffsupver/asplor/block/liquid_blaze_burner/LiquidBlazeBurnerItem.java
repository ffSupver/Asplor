package com.ffsupver.asplor.block.liquid_blaze_burner;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

public class LiquidBlazeBurnerItem extends BlockItem {
    @ApiStatus.Internal
    public static boolean IS_PLACING_NBT = false;
    public LiquidBlazeBurnerItem(Block block, Settings settings) {
        super(block, settings);
    }
    @Override
    public ActionResult place(ItemPlacementContext ctx) {
        IS_PLACING_NBT = checkPlacingNbt(ctx);
        ActionResult initialResult = super.place(ctx);
        IS_PLACING_NBT = false;
//        if (!initialResult.isAccepted())
//            return initialResult;
//        tryMultiPlace(ctx);
        return initialResult;
    }
    @Override
    protected boolean postPlacement(BlockPos blockPos, World world, PlayerEntity player,
                                    ItemStack itemStack, BlockState blockState) {
        MinecraftServer minecraftserver = world.getServer();
        if (minecraftserver == null)
            return false;
        NbtCompound nbt = itemStack.getSubNbt("BlockEntityTag");
        if (nbt != null) {
            nbt.remove("Size");
            nbt.remove("Height");
            nbt.remove("Controller");
            nbt.remove("LastKnownPos");
            if (nbt.contains("TankContent")) {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound("TankContent"));
                if (!fluid.isEmpty()) {
                    fluid.setAmount(Math.min(LiquidBlazeBurnerEntity.getCapacityMultiplier(), fluid.getAmount()));
                    nbt.put("TankContent", fluid.writeToNBT(new NbtCompound()));
                }
            }
        }
        return super.postPlacement(blockPos, world, player, itemStack, blockState);
    }

    private void tryMultiPlace(ItemPlacementContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        if (player == null)
            return;
        if (player.isSneaking())
            return;
        Direction face = ctx.getSide();
        if (!face.getAxis()
                .isVertical())
            return;
        ItemStack stack = ctx.getStack();
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        BlockPos placedOnPos = pos.offset(face.getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);

        if (!LiquidBlazeBurner.isTank(placedOnState))
            return;

        LiquidBlazeBurnerEntity tankAt = ConnectivityHandler.partAt(
                AllBlockEntityTypes.LIQUID_BLAZE_BURNER_ENTITY.get(), world, placedOnPos
        );
        if (tankAt == null)
            return;
        LiquidBlazeBurnerEntity controllerBE = tankAt.getControllerBE();
        if (controllerBE == null)
            return;

        int width = controllerBE.width;
        if (width == 1)
            return;

        int tanksToPlace = 0;
        BlockPos startPos = face == Direction.DOWN ? controllerBE.getPos()
                .down()
                : controllerBE.getPos()
                .up(controllerBE.height);

        if (startPos.getY() != pos.getY())
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.add(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (LiquidBlazeBurner.isTank(blockState))
                    continue;
                if (!blockState.isReplaceable())
                    return;
                tanksToPlace++;
            }
        }

        if (!player.isCreative() && stack.getCount() < tanksToPlace)
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.add(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (LiquidBlazeBurner.isTank(blockState))
                    continue;
                ItemPlacementContext context = ItemPlacementContext.offset(ctx, offsetPos, face);
                player.getCustomData()
                        .putBoolean("SilenceTankSound", true);
                IS_PLACING_NBT = checkPlacingNbt(context);
                super.place(context);
                IS_PLACING_NBT = false;
                player.getCustomData()
                        .getBoolean("SilenceTankSound");
            }
        }
    }
    public static boolean checkPlacingNbt(ItemPlacementContext ctx) {
        ItemStack item = ctx.getStack();
        return BlockItem.getBlockEntityNbt(item) != null;
    }
}
