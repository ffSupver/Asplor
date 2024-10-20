package com.ffsupver.asplor.item.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PlaceableBucketItem extends BucketItem {
    private boolean canPlace;
    public PlaceableBucketItem(Fluid fluid, Settings settings,boolean canPlace) {
        super(fluid, settings);
        this.canPlace = canPlace;
    }

    @Override
    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult) {
        return canPlace && super.placeFluid(player, world, pos, hitResult);
    }
}
