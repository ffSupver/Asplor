package com.ffsupver.asplor.block.planetLocator;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.ffsupver.asplor.item.item.MeteoriteFragmentItem;
import com.ffsupver.asplor.item.item.NavigationChipItem;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Optional;

public class PlanetLocator extends Block implements IBE<PlanetLocatorEntity> , IWrenchable {
    public PlanetLocator(Settings settings) {
        super(settings);
    }

    @Override
    public Class<PlanetLocatorEntity> getBlockEntityClass() {
        return PlanetLocatorEntity.class;
    }

    @Override
    public BlockEntityType<? extends PlanetLocatorEntity> getBlockEntityType() {
        return AllBlockEntityTypes.PLANET_LOCATOR_ENTITY.get();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PlanetLocatorEntity planetLocatorEntity){
            if (itemStack.getItem() instanceof NavigationChipItem) {
                Optional<RegistryKey<World>> originalWorldKey = NavigationChipItem.getWorldKey(itemStack,false);
                if (originalWorldKey.isPresent()){
                    planetLocatorEntity.setWorldKey(originalWorldKey.get().getValue());
                    planetLocatorEntity.updatePlanetData();
                    return ActionResult.SUCCESS;
                }
                long inserted = planetLocatorEntity.insertNav(itemStack);
                ItemStack result = itemStack.copyWithCount((int) (itemStack.getCount() - inserted));
                player.setStackInHand(hand,result);
                return ActionResult.SUCCESS;

            }
            if (itemStack.getItem() instanceof MeteoriteFragmentItem) {
                long inserted = planetLocatorEntity.insertMeteorite(itemStack);
                ItemStack result = itemStack.copyWithCount((int) (itemStack.getCount() - inserted));
                player.setStackInHand(hand,result);
                return ActionResult.SUCCESS;
            }
            if (itemStack.isEmpty()){
                ItemStack outputStack = planetLocatorEntity.extractOutput();
                if (!outputStack.isEmpty()){
                    player.setStackInHand(hand, outputStack);
                    return ActionResult.SUCCESS;
                }else {
                    if (planetLocatorEntity.hasItem()){
                        player.setStackInHand(hand, planetLocatorEntity.getItemInside());
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0,0,0,1.0f,0.5f,1.0f);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}
