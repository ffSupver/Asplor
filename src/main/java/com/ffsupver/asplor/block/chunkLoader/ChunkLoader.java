package com.ffsupver.asplor.block.chunkLoader;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChunkLoader extends Block implements IBE<ChunkLoaderEntity> {
    public ChunkLoader(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ChunkLoaderEntity chunkLoaderEntity){
           return chunkLoaderEntity.onUse(player,hand,hit);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(!state.isOf(newState.getBlock())){
            withBlockEntityDo(world,pos, SmartBlockEntity::destroy);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public Class<ChunkLoaderEntity> getBlockEntityClass() {
        return ChunkLoaderEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChunkLoaderEntity> getBlockEntityType() {
        return AllBlockEntityTypes.CHUNK_LOADER_ENTITY.get();
    }
}
