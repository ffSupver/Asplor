package com.ffsupver.asplor.block.alloyDepot;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.item.item.SchematicItem;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.SharedDepotBlockMethods;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.AdventureUtil;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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

public class AlloyDepot extends Block implements IBE<AlloyDepotEntity> {
    public static final Property<Boolean> SCHEMATIC = BooleanProperty.of("schematic");
    public AlloyDepot(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(SCHEMATIC,false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(SCHEMATIC));
    }

    @Override
    public Class<AlloyDepotEntity> getBlockEntityClass() {
        return AlloyDepotEntity.class;
    }

    @Override
    public BlockEntityType<? extends AlloyDepotEntity> getBlockEntityType() {
        return AllBlockEntityTypes.ALLOY_DEPOT_ENTITY.get();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient)
            return ActionResult.SUCCESS;

        ItemStack heldItem = player.getStackInHand(hand);
        boolean wasEmptyHanded = heldItem.isEmpty();
        boolean isSchematicItem = heldItem.isOf(ModItems.SCHEMATIC);

        if (hit.getSide() != Direction.UP){
            if (!world.isClient() && world.getBlockEntity(pos) instanceof AlloyDepotEntity alloyDepotEntity) {
                if (isSchematicItem && alloyDepotEntity.getSchematic() == null) {
                    String schematic = SchematicItem.getSchematicFromItem(heldItem);
                    if (schematic != null) {
                        alloyDepotEntity.setSchematic(schematic);
                        player.setStackInHand(hand,heldItem.copyWithCount(heldItem.getCount() - 1));

                        alloyDepotEntity.notifyUpdate();
                        return ActionResult.SUCCESS;
                    }
                }

                if (wasEmptyHanded) {
                   String schematic = alloyDepotEntity.getSchematic();
                   if (schematic != null){
                       alloyDepotEntity.setSchematic(AlloyDepotEntity.EMPTY_SCHEMATIC);
                       player.setStackInHand(hand,SchematicItem.getSchematicItem(schematic));

                       alloyDepotEntity.notifyUpdate();
                       return ActionResult.SUCCESS;
                   }
                }
            }
        return ActionResult.PASS;
    }

        if (AdventureUtil.isAdventure(player))
            return ActionResult.PASS;


        AlloyDepotBehaviour behaviour = BlockEntityBehaviour.get(world, pos, AlloyDepotBehaviour.TYPE);


        ItemStack mainItemStack = behaviour.getHeldItemStack();



        if (!mainItemStack.isEmpty()) {
            player.getInventory()
                    .offerOrDrop(mainItemStack);
            behaviour.removeHeldItem();
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, .2f,
                    1f + Create.RANDOM.nextFloat());
        }
        ItemStackHandler outputs = behaviour.processingOutputBuffer;
        try (Transaction t = TransferUtil.getTransaction()) {
            for (StorageView<ItemVariant> view : outputs.nonEmptyViews()) {
                ItemVariant var = view.getResource();
                long extracted = view.extract(var, 64, t);
                ItemStack stack = var.toStack(ItemHelper.truncateLong(extracted));
                player.getInventory().offerOrDrop(stack);
            }
            t.commit();
        }

        if (!wasEmptyHanded && !isSchematicItem) {
            TransportedItemStack transported = new TransportedItemStack(heldItem);
            transported.insertedFrom = player.getHorizontalFacing();
            transported.prevBeltPosition = .25f;
            transported.beltPosition = .25f;
            behaviour.setHeldItem(transported);
            player.setStackInHand(hand, ItemStack.EMPTY);
            AllSoundEvents.DEPOT_SLIDE.playOnServer(world, pos);
        }



        behaviour.blockEntity.notifyUpdate();
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AllShapes.CASING_13PX.get(Direction.UP);
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        super.onEntityLand(world, entity);
        SharedDepotBlockMethods.onLanded(world,entity);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())){
            IBE.onRemove(state, world, pos, newState);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState, World worldIn, BlockPos pos) {
        return SharedDepotBlockMethods.getComparatorInputOverride(blockState, worldIn, pos);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView reader, BlockPos pos, NavigationType type) {
        return false;
    }
}
