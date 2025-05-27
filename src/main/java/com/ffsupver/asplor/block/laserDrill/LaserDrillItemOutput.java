package com.ffsupver.asplor.block.laserDrill;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LaserDrillItemOutput extends Block implements IBE<LaserDrillItemOutputEntity>, IWrenchable {
    public LaserDrillItemOutput(Settings settings) {
        super(settings);
    }

    @Override
    public Class<LaserDrillItemOutputEntity> getBlockEntityClass() {
        return LaserDrillItemOutputEntity.class;
    }

    @Override
    public BlockEntityType<? extends LaserDrillItemOutputEntity> getBlockEntityType() {
        return AllBlockEntityTypes.LASER_DRILL_ITEM_OUTPUT_ENTITY.get();
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.getBlock().equals(newState.getBlock())){
            withBlockEntityDo(world,pos, LaserDrillItemOutputEntity::destroy);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
