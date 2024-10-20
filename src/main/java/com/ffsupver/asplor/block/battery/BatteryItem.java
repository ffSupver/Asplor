package com.ffsupver.asplor.block.battery;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.ffsupver.asplor.block.EnergyConnectiveHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BatteryItem extends BlockItem {
    public static boolean IS_PLACING_NBT = false;
    public BatteryItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult place(ItemPlacementContext ctx) {
        IS_PLACING_NBT = checkPlacingNbt(ctx);
        ActionResult initialResult = super.place(ctx);
        IS_PLACING_NBT = false;
        if (!initialResult.isAccepted())
            return initialResult;
        tryMultiPlace(ctx);
        return initialResult;
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

        if (!Battery.isBattery(placedOnState))
            return;

        BatteryEntity batteryAt = EnergyConnectiveHandler.partAt(
                AllBlockEntityTypes.BATTERY_ENTITY.get(), world, placedOnPos
        );
        if (batteryAt == null)
            return;
        BatteryEntity controllerBE = batteryAt.getControllerBE();
        if (controllerBE == null)
            return;

        int width = controllerBE.width;
        if (width == 1)
            return;

        int batteryToPlace = 0;
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
                if (Battery.isBattery(blockState))
                    continue;
                if (!blockState.isReplaceable())
                    return;
                batteryToPlace++;
            }
        }

        if (!player.isCreative() && stack.getCount() < batteryToPlace)
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = startPos.add(xOffset, 0, zOffset);
                BlockState blockState = world.getBlockState(offsetPos);
                if (Battery.isBattery(blockState))
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
