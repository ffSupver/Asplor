package com.ffsupver.asplor.block.divider;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import static com.ffsupver.asplor.AllBlockEntityTypes.DIVIDER_ENTITY;

public class Divider extends KineticBlock implements IBE<DividerEntity> , IWrenchable {
    public static final BooleanProperty CASING ;
    public Divider(Settings properties) {
        super(properties);
        this.setDefaultState(this.stateManager.getDefaultState().with(CASING,true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{CASING});
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(1,2,1,15,16,15);
    }

    @Override
    public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.UP;
    }

    @Override
    public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
        World world = context.getWorld();
        BlockState newState = state.with(CASING,!state.get(CASING));
        world.setBlockState(context.getBlockPos(),newState);

        if (world.getBlockState(context.getBlockPos()) != state)
            playRotateSound(world, context.getBlockPos());

        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
                              BlockHitResult hit) {
        if (!player.getStackInHand(handIn)
                .isEmpty())
            return ActionResult.PASS;

        if (worldIn.isClient)
            return ActionResult.SUCCESS;

        withBlockEntityDo(worldIn, pos, millstone -> {
            boolean emptyOutput = true;
            ItemStackHandler inv = millstone.outputInv;
            for (int slot = 0; slot < inv.getSlotCount(); slot++) {
                ItemStack stackInSlot = inv.getStackInSlot(slot);
                if (!stackInSlot.isEmpty())
                    emptyOutput = false;
                player.getInventory()
                        .offerOrDrop(stackInSlot);
                inv.setStackInSlot(slot, ItemStack.EMPTY);
            }

            if (emptyOutput) {
                inv = millstone.inputInv;
                for (int slot = 0; slot < inv.getSlotCount(); slot++) {
                    player.getInventory()
                            .offerOrDrop(inv.getStackInSlot(slot));
                    inv.setStackInSlot(slot, ItemStack.EMPTY);
                }
            }

            millstone.markDirty();
            millstone.sendData();
        });

        return ActionResult.SUCCESS;
    }



    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<DividerEntity> getBlockEntityClass() {
        return DividerEntity.class;
    }

    @Override
    public BlockEntityType<? extends DividerEntity> getBlockEntityType() {
        return DIVIDER_ENTITY.get();
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView reader, BlockPos pos, NavigationType type) {
        return false;
    }
    static {
        CASING= BooleanProperty.of("casing");
    }
}
