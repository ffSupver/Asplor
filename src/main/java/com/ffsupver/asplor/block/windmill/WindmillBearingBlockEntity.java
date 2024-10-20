package com.ffsupver.asplor.block.windmill;

import com.ffsupver.asplor.ModTags;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class WindmillBearingBlockEntity extends MechanicalBearingBlockEntity {

    protected ScrollOptionBehaviour<RotationDirection> movementDirection;
    protected float lastGeneratedSpeed;

    protected boolean queuedReassembly;

    public WindmillBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void updateGeneratedRotation() {
        super.updateGeneratedRotation();
        lastGeneratedSpeed = getGeneratedSpeed();
        queuedReassembly = false;
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        boolean cancelAssembly = assembleNextTick;
        super.onSpeedChanged(prevSpeed);
        assembleNextTick = cancelAssembly;
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isClient())
            return;
        if (!queuedReassembly)
            return;
        queuedReassembly = false;
        if (!running)
            assembleNextTick = true;
    }

    public void disassembleForMovement() {
        if (!running)
            return;
        disassemble();
        queuedReassembly = true;
    }

    @Override
    public float getGeneratedSpeed() {
        if (!running)
            return 0;
        if (world.getBiome(pos).isIn(ModTags.Biomes.NO_WIND)){
            return 0;
        }
        if (movedContraption == null)
            return lastGeneratedSpeed;
        int sails = ((BearingContraption) movedContraption.getContraption()).getSailBlocks()
                / AllConfigs.server().kinetics.windmillSailsPerRPM.get();
        return MathHelper.clamp(sails, 1, 16) * getAngleSpeedDirection();
    }

    @Override
    protected boolean isWindmill() {
        return true;
    }

    protected float getAngleSpeedDirection() {
        com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity.RotationDirection rotationDirection = com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity.RotationDirection.values()[movementDirection.getValue()];
        return (rotationDirection == com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity.RotationDirection.CLOCKWISE ? 1 : -1);
    }

    @Override
    public void write(NbtCompound compound, boolean clientPacket) {
        compound.putFloat("LastGenerated", lastGeneratedSpeed);
        compound.putBoolean("QueueAssembly", queuedReassembly);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(NbtCompound compound, boolean clientPacket) {
        if (!wasMoved)
            lastGeneratedSpeed = compound.getFloat("LastGenerated");
        queuedReassembly = compound.getBoolean("QueueAssembly");
        super.read(compound, clientPacket);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.remove(movementMode);
        movementDirection = new ScrollOptionBehaviour<>(RotationDirection.class,
                Lang.translateDirect("contraptions.windmill.rotation_direction"), this, getMovementModeSlot());
        movementDirection.withCallback($ -> onDirectionChanged());
        behaviours.add(movementDirection);
        registerAwardables(behaviours, AllAdvancements.WINDMILL, AllAdvancements.WINDMILL_MAXED);
    }

    private void onDirectionChanged() {
        if (!running)
            return;
        if (!world.isClient)
            updateGeneratedRotation();
    }

    @Override
    public boolean isWoodenTop() {
        return true;
    }

    public static enum RotationDirection implements INamedIconOptions {

        CLOCKWISE(AllIcons.I_REFRESH), COUNTER_CLOCKWISE(AllIcons.I_ROTATE_CCW),

        ;

        private String translationKey;
        private AllIcons icon;

        private RotationDirection(AllIcons icon) {
            this.icon = icon;
            translationKey = "generic." + Lang.asId(name());
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return translationKey;
        }

    }

}