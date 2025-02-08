package com.ffsupver.asplor.block.theNetherReturner;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.ffsupver.asplor.item.ModItems;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static com.ffsupver.asplor.item.item.LocatorItem.LOCATION_DATA_KEY;

public class TheNetherReturner extends Block implements IBE<TheNetherReturnerEntity> {


    public TheNetherReturner(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

        return VoxelShapes.union(
                VoxelShapes.cuboid(new Box(3/16f,0/16f,3/16f,13/16f,2/16f,13/16f)),
                VoxelShapes.cuboid(new Box(4/16f,2/16f,4/16f,12/16f,3/16f,12/16f)),
                VoxelShapes.cuboid(new Box(5/16f,3/16f,5/16f,11/16f,7/16f,11/16f)),
                VoxelShapes.cuboid(new Box(3/16f,7/16f,3/16f,13/16f,8/16f,13/16f)),
                VoxelShapes.cuboid(new Box(1/16f,8/16f,1/16f,15/16f,10/16f,15/16f))
        );
    }

    @Override
    public Class<TheNetherReturnerEntity> getBlockEntityClass() {
        return TheNetherReturnerEntity.class;
    }

    @Override
    public BlockEntityType<? extends TheNetherReturnerEntity> getBlockEntityType() {
        return AllBlockEntityTypes.THE_NETHER_RETURNER_ENTITY.get();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TheNetherReturnerEntity theNetherReturnerEntity){
                if (!theNetherReturnerEntity.getActive()){
                    ItemStack handItemStack = player.getStackInHand(hand);
                    if (handItemStack.isOf(ModItems.LOCATOR)){
                        NbtCompound handItemStackNbt = handItemStack.getOrCreateNbt();
                        if (handItemStackNbt.contains(LOCATION_DATA_KEY,10)){
                            BlockPos location = new BlockPos(
                                    handItemStackNbt.getCompound(LOCATION_DATA_KEY).getInt("x"),
                                    handItemStackNbt.getCompound(LOCATION_DATA_KEY).getInt("y"),
                                    handItemStackNbt.getCompound(LOCATION_DATA_KEY).getInt("z")
                            );
                            theNetherReturnerEntity.setHasTarget(true);
                            theNetherReturnerEntity.setTargetPos(location);
                        }
                        if(!player.isCreative()){
                            handItemStack.setCount(handItemStack.getCount() - 1);
                        }
                        player.setStackInHand(hand,handItemStack);
                        theNetherReturnerEntity.setTimesRemain(3);
                        theNetherReturnerEntity.setActive(true);
                        return ActionResult.SUCCESS;
                    }
                }

        }
        return super.onUse(state, world, pos, player, hand, hit);
    }



}


