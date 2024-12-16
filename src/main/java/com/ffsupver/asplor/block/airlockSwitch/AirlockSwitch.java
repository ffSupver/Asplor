package com.ffsupver.asplor.block.airlockSwitch;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.util.math.Direction.Axis.*;

public class AirlockSwitch extends Block implements BlockEntityProvider {
    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.of("axis", Direction.Axis.class, List.of(X, Z));
    public AirlockSwitch(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(ON,false).with(AXIS, X));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()){
            return ActionResult.SUCCESS;
        }
        if (!hit.getSide().getAxis().equals(state.get(AXIS))){
            return ActionResult.PASS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AirlockSwitchEntity airlockSwitchEntity){
            airlockSwitchEntity.use();
            world.scheduleBlockTick(pos,this,20);
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction.Axis axis = ctx.getPlayerLookDirection().getAxis();
        return getDefaultState().with(AXIS,axis == Z ? Z : X);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AirlockSwitchEntity airlockSwitchEntity){
            airlockSwitchEntity.schedule();
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(ON) && !direction.getAxis().equals(state.get(AXIS)) ? 15 : 0;
    }



    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(ON,AXIS));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AirlockSwitchEntity(pos,state);
    }

}
