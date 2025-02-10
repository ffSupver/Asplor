package com.ffsupver.asplor.block.lightningAbsorber;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

public class LightningAbsorber extends Block implements IWrenchable {
    private static final long ENERGY_PER_STUCK = 1000L;
    public LightningAbsorber(Settings settings) {
        super(settings);
    }


    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if ((world.isRaining() || world.isThundering()) && projectile instanceof TridentEntity && ((TridentEntity)projectile).hasChanneling()) {
            BlockPos blockPos = hit.getBlockPos();
            if (world.isSkyVisible(blockPos)) {
                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                if (lightningEntity != null) {
                    lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos.up()));
                    Entity entity = projectile.getOwner();
                    lightningEntity.setChanneler(entity instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity : null);
                    world.spawnEntity(lightningEntity);
                }

                world.playSound(null, blockPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.WEATHER, 5.0F, 1.0F);
            }
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(VoxelShapes.cuboid(6/16f,12/16f,6/16f,10/16f,16/16f,10/16f),
                VoxelShapes.cuboid(7/16f,4/16f,7/16f,9/16f,12/16f,9/16f),
                VoxelShapes.cuboid(4/16f,2/16f,4/16f,12/16f,4/16f,12/16f),
                VoxelShapes.cuboid(3/16f,0/16f,3/16f,13/16f,2/16f,13/16f));
    }

    public void onLightningStuck(World world,BlockPos pos){
        EnergyStorage belowEnergy = EnergyStorage.SIDED.find(world, pos.down(), Direction.UP);
        if (belowEnergy != null){
            try(Transaction t = Transaction.openOuter()) {
                belowEnergy.insert(ENERGY_PER_STUCK, t);
                t.commit();
            }
        }
    }
}
