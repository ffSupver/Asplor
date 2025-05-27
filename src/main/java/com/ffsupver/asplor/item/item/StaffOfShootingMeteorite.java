package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.entity.ModEntities;
import com.ffsupver.asplor.entity.custom.Meteorite;
import com.ffsupver.asplor.item.ModItems;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class StaffOfShootingMeteorite extends Item implements Vanishable {
    public StaffOfShootingMeteorite( Settings settings) {
        super( settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        HitResult hitResult = user.raycast(384f, 1, false);


        if (!hitResult.getType().equals(HitResult.Type.MISS)){
            user.getItemCooldownManager().set(ModItems.STAFF_OF_SHOOTING_METEORITE,10);
           return meteoriteHitAt(itemStack,world,user,hitResult);
        }

        return super.use(world,user,hand);
    }



    public static TypedActionResult<ItemStack> meteoriteHitAt(ItemStack itemStack,World world,PlayerEntity user,HitResult hitResult){
        Vec3d targetPosition = hitResult.getPos();

        meteoriteHitAt(targetPosition,world,user,true);

//        Vec3d meteoriteVec = new Vec3d(0, 0, 1).rotateY((float) Math.toRadians(180 - user.getYaw())).add(0, 1, 0).multiply(-1);
//        Meteorite meteorite = new Meteorite(ModEntities.METEORITE, world);
//        double distance = Math.max(user.getPos().add(targetPosition.multiply(-1)).length(), 10);
//        Vec3d meteoritePosition = targetPosition.add(
//                meteoriteVec.multiply(
//                        -distance
//                )
//        );
//        world.spawnEntity(meteorite);
//        meteorite.teleport(meteoritePosition.getX(), meteoritePosition.getY(), meteoritePosition.getZ());
//        meteorite.setNoGravity(true);
//        meteorite.setVelocity(meteoriteVec);
//        meteorite.setOwnerUuid(user.getUuid());

//        AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(world,targetPosition.getX(),targetPosition.getY(),targetPosition.getZ());
//        world.spawnEntity(areaEffectCloudEntity);
//        areaEffectCloudEntity.setColor(0xFF0000);
//        areaEffectCloudEntity.setDuration((int) (distance / 2));

        itemStack.damage(1,user,player -> {});

        return TypedActionResult.success(itemStack);
    }

    public static void meteoriteHitAt(Vec3d targetPosition, World world, Entity user,boolean destroyBlock){
        Vec3d meteoriteVec = new Vec3d(0, 0, 1).rotateY((float) Math.toRadians(180 - user.getYaw())).add(0, 1, 0).multiply(-1);
        Meteorite meteorite = new Meteorite(ModEntities.METEORITE, world,destroyBlock);
        double distance = Math.max(user.getPos().add(targetPosition.multiply(-1)).length(), 10);
        Vec3d meteoritePosition = targetPosition.add(
                meteoriteVec.multiply(
                        -distance
                )
        );
        world.spawnEntity(meteorite);
        meteorite.teleport(meteoritePosition.getX(), meteoritePosition.getY(), meteoritePosition.getZ());
        meteorite.setNoGravity(true);
        meteorite.setVelocity(meteoriteVec);
        meteorite.setOwnerUuid(user.getUuid());

        AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(world,targetPosition.getX(),targetPosition.getY(),targetPosition.getZ());
        world.spawnEntity(areaEffectCloudEntity);
        areaEffectCloudEntity.setColor(0xFF0000);
        areaEffectCloudEntity.setDuration((int) (distance / 2));
    }

    @Override
    public boolean isDamageable() {
        return true;
    }


}
