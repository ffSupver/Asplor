package com.ffsupver.asplor.block.meltingFurnace;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MeltingFurnace extends Block implements IBE<MeltingFurnaceEntity> {
    private final VoxelShape shape = VoxelShapes.union(VoxelShapes.cuboid(2/16f,0/16f,2/16f,14/16f,1/16f,14/16f),
            VoxelShapes.cuboid(0/16f,1/16f,0/16f,16/16f,15/16f,16/16f),
            VoxelShapes.cuboid(1/16f,15/16f,1/16f,15/16f,16/16f,15/16f));
    public MeltingFurnace(Settings settings) {
        super(settings);
    }

    @Override
    public Class<MeltingFurnaceEntity> getBlockEntityClass() {
        return MeltingFurnaceEntity.class;
    }

    @Override
    public BlockEntityType<? extends MeltingFurnaceEntity> getBlockEntityType() {
        return AllBlockEntityTypes.MELTING_FURNACE_ENTITY.get();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return shape;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return shape;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MeltingFurnaceEntity){
            MeltingFurnaceEntity meltingFurnaceEntity = (MeltingFurnaceEntity) blockEntity;
            meltingFurnaceEntity.destroy();
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }


}
