package com.ffsupver.asplor.util;

import com.ffsupver.asplor.screen.worldAdder.WorldAdderScreen;
import earth.terrarium.adastra.common.blocks.SlidingDoorBlock;
import earth.terrarium.adastra.common.blocks.properties.SlidingDoorPartProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public final class ModUtil {
    @Environment(EnvType.CLIENT)
    public static void openWorldAdderScreen(PlayerEntity user){
            MinecraftClient.getInstance().setScreen(new WorldAdderScreen(Text.translatable("asplor.screen.world_adder")));
    }

    public static BlockPos getSlideDoorControllerPos(BlockState state, BlockPos pos) {
        SlidingDoorPartProperty part = (SlidingDoorPartProperty)state.get(SlidingDoorBlock.PART);
        Direction direction = ((Direction)state.get(SlidingDoorBlock.FACING)).rotateYClockwise();
        return pos.offset(direction.getOpposite(), -part.xOffset()).down(part.yOffset());
    }
    public static BlockState getSlideDoorControllerState(World world, BlockState state, BlockPos pos) {
        return world.getBlockState(getSlideDoorControllerPos(state,pos));
    }

    public static boolean isSlideDoorOpen(World world, BlockState state, BlockPos pos) {
        return state.get(SlidingDoorBlock.OPEN) || ModUtil.getSlideDoorControllerState(world,state,pos).get(SlidingDoorBlock.POWERED);
    }
}
