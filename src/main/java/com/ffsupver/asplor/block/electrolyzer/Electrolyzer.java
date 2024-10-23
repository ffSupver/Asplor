package com.ffsupver.asplor.block.electrolyzer;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.ffsupver.asplor.AllBlocks;
import com.simibubi.create.content.contraptions.BlockMovementChecks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Electrolyzer extends Block implements IBE<ElectrolyzerEntity> , IWrenchable {
    public static EnumProperty<ElectrolyzerPart> PART = EnumProperty.of("part",ElectrolyzerPart.class);
    public static final IntProperty LIGHT_LEVEL = IntProperty.of("light_level", 0, 15);

    private final VoxelShape outlineShape = VoxelShapes.union(VoxelShapes.cuboid(0,0,0,16/16f,14/16f,16/16f),
            VoxelShapes.cuboid(11/16f,14/16f,11/16f,15/16f,17/16f,15/16f),
            VoxelShapes.cuboid(6/16f,14/16f,6/16f,10/16f,17/16f,10/16f),
            VoxelShapes.cuboid(0,17/16f,0,16/16f,31/16f,16/16f),
            VoxelShapes.cuboid(6/16f,31/16f,6/16f,10/16f,34/16f,10/16f),
            VoxelShapes.cuboid(0,34/16f,0,16/16f,48/16f,16/16f));

    public Electrolyzer(Settings settings) {
        super(setLightFunction(settings));
       this.setDefaultState(this.getDefaultState()
               .with(PART,ElectrolyzerPart.LOWER)
               .with(LIGHT_LEVEL, 0));
        BlockMovementChecks.registerMovementAllowedCheck((state, world, pos) ->
                state.isOf(AllBlocks.MECHANICAL_PUMP.get())?
                        BlockMovementChecks.CheckResult.FAIL: BlockMovementChecks.CheckResult.PASS
        );
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(PART)){
            case LOWER -> outlineShape;
            case MIDDLE -> outlineShape.offset(0,-1,0);
            case UPPER -> outlineShape.offset(0,-2,0);
        };
    }

    private static Settings setLightFunction(Settings properties) {
        return properties.luminance(state -> state.get(LIGHT_LEVEL));
    }

    @Override
    public Class<ElectrolyzerEntity> getBlockEntityClass() {
        return ElectrolyzerEntity.class;
    }

    @Override
    public BlockEntityType<? extends ElectrolyzerEntity> getBlockEntityType() {
        return AllBlockEntityTypes.ELECTROLYZER_ENTITY.get();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIGHT_LEVEL,PART);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        boolean canPlace = blockPos.getY() < world.getTopY() - 2 && world.getBlockState(blockPos.up()).canReplace(ctx) && world.getBlockState(blockPos.up().up()).canReplace(ctx);
        if (canPlace) {
            return this.getDefaultState().with(PART, ElectrolyzerPart.LOWER).with(LIGHT_LEVEL, 0);
        } else {
            return null;
        }
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(PART, ElectrolyzerPart.MIDDLE).with(LIGHT_LEVEL, 0), 3);
        world.setBlockState(pos.up().up(), state.with(PART, ElectrolyzerPart.UPPER).with(LIGHT_LEVEL, 0), 3);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onStateReplaced(state, world, pos, newState, isMoving);
        if (!isMoving && !state.isOf(newState.getBlock())){
            for(Pair<BlockPos,ElectrolyzerPart> posCheck : getBlockPosToCheck(pos,state.get(PART))) {
                checkAndRemove(world, posCheck.getLeft(),posCheck.getRight());
            }
        }
    }

    private void checkAndRemove(World world,BlockPos pos,ElectrolyzerPart part){
        BlockState state = world.getBlockState(pos);
        if (state.isOf(state.getBlock()) && state.contains(PART) && state.get(PART) == part) {
            BlockState blockState2 = state.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
            world.setBlockState(pos, blockState2, 35);
        }
    }

    private List<Pair<BlockPos,ElectrolyzerPart>> getBlockPosToCheck(BlockPos pos, ElectrolyzerPart part){
        return switch (part){
            case LOWER -> List.of(new Pair<>(pos.up(), ElectrolyzerPart.MIDDLE), new Pair<>(pos.up().up(), ElectrolyzerPart.UPPER));
            case MIDDLE -> List.of(new Pair<>(pos.up(),ElectrolyzerPart.UPPER),new Pair<>(pos.down(),ElectrolyzerPart.LOWER));
            case UPPER -> List.of(new Pair<>(pos.down(),ElectrolyzerPart.MIDDLE),new Pair<>(pos.down().down(),ElectrolyzerPart.LOWER));
        };
    }

    public enum ElectrolyzerPart implements StringIdentifiable {
        UPPER,
        MIDDLE,
        LOWER;

        private ElectrolyzerPart() {
        }

        public String toString() {
            return this.asString();
        }

        public String asString() {
            return this != MIDDLE ? this == UPPER ? "upper" : "lower" : "middle";
        }
    }
}
