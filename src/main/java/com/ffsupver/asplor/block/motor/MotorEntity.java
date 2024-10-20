package com.ffsupver.asplor.block.motor;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.block.SmartEnergyStorage;
import com.ffsupver.asplor.util.GoggleDisplays;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class MotorEntity extends GeneratingKineticBlockEntity {
    protected SmartEnergyStorage energyStorage;
    private static final float ENERGY_RATE = 0.5f;
    private static final long CAPACITY= (long) (6553600*ENERGY_RATE);
    private static final long MAX_TRANSFER =5*CAPACITY;
    protected ScrollValueBehaviour generatedSpeed;
    public static final int DEFAULT_SPEED = 16;
    public static final int MAX_SPEED = 256;
    private float setSpeed ;
    private boolean hasEnergy;


    public MotorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        energyStorage=createInventory();
        hasEnergy=false;
        setSpeed=0;
    }

    protected SmartEnergyStorage createInventory() {
        return new SmartEnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER,this::onEnergyLevelChanged);
    }

    protected void onEnergyLevelChanged(long newEnergyLevel) {
        // 执行你需要的更新操作，比如标记区块需要保存，更新客户端等
        markDirty();
        if (!world.isClient()) {
            // 发送能量更新包到客户端
            sendData();
        }
    }

    public SmartEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
            updateGeneratedRotation();
    }

    @Override
    public void tick() {
        if (hasEnergy!=energyStorage.getAmount()>0){
            reActivateSource=true;
        }
        hasEnergy=energyStorage.getAmount()>0;
        if (!hasEnergy){

        }else {
            energyStorage.extractEnergy((long) (ENERGY_RATE*getTheoreticalSpeed()*getTheoreticalSpeed()));
        }
        super.tick();
    }

    @Override
    protected void read(NbtCompound compound, boolean clientPacket) {
        energyStorage.readFromNBT(compound.getCompound("Energy"));
        setSpeed = compound.getFloat("set_speed");
        super.read(compound, clientPacket);
    }

    @Override
    protected void write(NbtCompound compound, boolean clientPacket) {
        compound.put("Energy", energyStorage.writeToNBT(new NbtCompound()));
        compound.putFloat("set_speed",setSpeed);
        super.write(compound, clientPacket);
    }


    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        int max = MAX_SPEED;
        generatedSpeed = new KineticScrollValueBehaviour(Lang.translateDirect("kinetics.motor.rotation_speed"),
                this, new MotorValueBox());
        generatedSpeed.between(-max, max);
        generatedSpeed.value = DEFAULT_SPEED;
        generatedSpeed.withCallback(i->{
            setSetSpeed(i);

            this.updateGeneratedRotation();

        });
        behaviours.add(generatedSpeed);
    }

    public void setSetSpeed(float setSpeed) {
        this.setSpeed = setSpeed;
    }

    @Override
    public float getGeneratedSpeed() {
        if (!AllBlocks.MOTOR.has(getCachedState())||!hasEnergy)
            return 0;
        return -convertToDirection(generatedSpeed.getValue(), getCachedState().get(Motor.FACING));
    }



    class MotorValueBox extends ValueBoxTransform.Sided {

        @Override
        protected Vec3d getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 15.5);
        }

        @Override
        public Vec3d getLocalOffset(BlockState state) {
            Direction facing = state.get(Motor.FACING);
            return super.getLocalOffset(state).add(Vec3d.of(facing.getVector())
                    .multiply(-0 / 16f));
        }

        @Override
        public void rotate(BlockState state, MatrixStack ms) {
            super.rotate(state, ms);
            Direction facing = state.get(Motor.FACING);
            if (facing.getAxis() == Direction.Axis.Y)
                return;
            if (getSide() != Direction.UP)
                return;
            TransformStack.cast(ms)
                    .rotateZ(-AngleHelper.horizontalAngle(facing) + 180);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            Direction facing = state.get(Motor.FACING);
            if (facing.getAxis() != Direction.Axis.Y && direction == Direction.DOWN)
                return false;
            return direction.getAxis() != facing.getAxis();
        }

    }
}
