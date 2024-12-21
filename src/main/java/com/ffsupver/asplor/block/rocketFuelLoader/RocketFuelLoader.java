package com.ffsupver.asplor.block.rocketFuelLoader;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RocketFuelLoader extends Block implements IBE<RocketFuelLoaderEntity> {
    public RocketFuelLoader(Settings settings) {
        super(settings);
    }

    @Override
    public Class<RocketFuelLoaderEntity> getBlockEntityClass() {
        return RocketFuelLoaderEntity.class;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        for (Direction direction : DIRECTIONS){
            BlockPos checkPos = pos.offset(direction);
            BlockState checkState = world.getBlockState(checkPos);
            if (checkState.getBlock() instanceof LaunchPadBlock launchPadBlock){
                BlockPos rocketPos = launchPadBlock.getController(checkState,checkPos);
                withBlockEntityDo(world,pos,rocketFuelLoaderEntity -> rocketFuelLoaderEntity.setRocketPos(rocketPos));
            }
        }
    }

    @Override
    public BlockEntityType<? extends RocketFuelLoaderEntity> getBlockEntityType() {
        return AllBlockEntityTypes.ROCKET_FUEL_LOADER_ENTITY.get();
    }

}
