package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.ffsupver.asplor.item.item.SchematicItem;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BeltSmartProcessor extends Block implements IBE<BeltSmartProcessorEntity> {
    public static final Property<Boolean> SCHEMATIC = BooleanProperty.of("schematic");

    public BeltSmartProcessor(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(SCHEMATIC,false));
    }

    @Override
    public Class<BeltSmartProcessorEntity> getBlockEntityClass() {
        return BeltSmartProcessorEntity.class;
    }

    @Override
    public BlockEntityType<? extends BeltSmartProcessorEntity> getBlockEntityType() {
        return AllBlockEntityTypes.BELT_SMART_PROCESSOR_ENTITY.get();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(SCHEMATIC));
    }



    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack handItem = player.getStackInHand(hand);
        if (handItem.getItem() instanceof SchematicItem){
            withBlockEntityDo(world,pos,beltSmartProcessorEntity -> {
               boolean set = beltSmartProcessorEntity.setSchematic(handItem);
               if (set){
                   world.setBlockState(pos,state.with(SCHEMATIC,true));
               }
            });
            player.setStackInHand(hand,handItem.copyWithCount(handItem.getCount() - 1));
            return ActionResult.SUCCESS;
        }else if (handItem.isEmpty()){
            withBlockEntityDo(world,pos,beltSmartProcessorEntity -> {
                if (hit.getSide().equals(Direction.DOWN)){
                    ItemStack toolStack = beltSmartProcessorEntity.extractToolItem();
                    if (!toolStack.isEmpty()){
                        player.setStackInHand(hand,toolStack);

                    }else {
                        ItemStack schematic = beltSmartProcessorEntity.removeSchematic();
                        player.setStackInHand(hand, schematic);
                        world.setBlockState(pos, state.with(BeltSmartProcessor.SCHEMATIC, false));
                    }
                }else {
                    ItemStack schematic = beltSmartProcessorEntity.removeSchematic();
                    player.setStackInHand(hand, schematic);
                    world.setBlockState(pos, state.with(BeltSmartProcessor.SCHEMATIC, false));
                }
            });
            return ActionResult.SUCCESS;
        }
        if (world.getBlockEntity(pos) instanceof BeltSmartProcessorEntity beltSmartProcessorEntity){
           long i = beltSmartProcessorEntity.insertTool(handItem);
           if (i > 0){
               player.setStackInHand(hand,handItem.copyWithCount((int) (handItem.getCount() - i)));
               return ActionResult.SUCCESS;
           }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
