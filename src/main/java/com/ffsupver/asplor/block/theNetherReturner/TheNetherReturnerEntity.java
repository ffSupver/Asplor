package com.ffsupver.asplor.block.theNetherReturner;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.util.RenderUtil;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import java.util.List;

public class TheNetherReturnerEntity extends SmartBlockEntity {
    private boolean active;
    private boolean hasTarget;
    private BlockPos targetPos;
    private int timesRemain;

    //render
    private int rotation;

    public TheNetherReturnerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.rotation = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!hasWorld()){
            return;
        }

        if (!active){
            return;
        }

        if (timesRemain <= 0){
            this.active = false;
            this.hasTarget = false;
            return;
        }

        if (world.isClient()){
            rotation = rotation > 360 ? 0 : rotation + 1;
        }


       List<Entity> entitiesToReturn = world.getOtherEntities(null, new Box(pos.getX(), pos.getY()+1, pos.getZ(), pos.getX() + 1, pos.getY()+2, pos.getZ() + 1), (entity -> true));

        for (Entity entity : entitiesToReturn){

            if (!world.isClient()){
                if (world.getDimensionKey().getValue().equals(new Identifier(Asplor.MOD_ID,"the_nether"))) {
                    ServerWorld overworld = world.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier("overworld")));

                    TeleportTarget teleportTarget = new TeleportTarget(
                           hasTarget ?
                                   targetPos.toCenterPos() :
                                   new Vec3d(entity.getX(), overworld.getHeight() + overworld.getBottomY(), entity.getZ()),
                            entity.getVelocity(), entity.getYaw(), entity.getPitch());


                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 600));
                    }
                    timesRemain -= 1;
                    FabricDimensionInternals.changeDimension(entity, overworld, teleportTarget);
                }
                sendData();
            }
        }

    }

    public int getRotation() {
        return rotation;
    }

    public void setHasTarget(boolean hasTarget) {
        this.hasTarget = hasTarget;
    }

    public void setTargetPos(BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    public void setTimesRemain(int timesRemain) {
        this.timesRemain = timesRemain;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getActive(){
        return active;
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        active = tag.getBoolean("active");
        hasTarget = tag.getBoolean("has_target");
        timesRemain = tag.getInt("times_remain");

        if (hasTarget){
            NbtCompound targetPosNbt = tag.getCompound("target");
            targetPos = new BlockPos(
                targetPosNbt.getInt("x"),
                targetPosNbt.getInt("y"),
                targetPosNbt.getInt("z")
            );
        }
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("active", active);

        tag.putBoolean("has_target",hasTarget);
        tag.putInt("times_remain",timesRemain);

        if (hasTarget){
            NbtCompound targetPosNbt = new NbtCompound();
            targetPosNbt.putInt("x", targetPos.getX());
            targetPosNbt.putInt("y", targetPos.getY());
            targetPosNbt.putInt("z", targetPos.getZ());
            tag.put("target", targetPosNbt);
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public Box getRenderBoundingBox() {
        return RenderUtil.createRenderBoundingBox(pos,3.0);
    }
}
