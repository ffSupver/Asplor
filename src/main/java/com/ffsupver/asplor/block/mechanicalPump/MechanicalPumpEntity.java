package com.ffsupver.asplor.block.mechanicalPump;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

import static net.minecraft.util.Formatting.GRAY;

public class MechanicalPumpEntity extends KineticBlockEntity implements IHaveGoggleInformation , IWrenchable {
    private final Long WATER_PER_TICK =  81L;
    private double upperSpeed;
    public MechanicalPumpEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        upperSpeed=0;
    }

    @Override
    public void tick() {
        if (this.getCachedState().get(MechanicalPump.HALF).equals(DoubleBlockHalf.LOWER)){
            double preUpperSpeed = upperSpeed;
            updateUpperSpeed();
            if (!world.isClient()) {
                if (preUpperSpeed!=upperSpeed){
                    sendData();
                }
                tryAddWaterToBelow();
            }
        }
        super.tick();

    }

    private void updateUpperSpeed(){
        BlockEntity blockEntity = world.getBlockEntity(pos.up());
        if (blockEntity instanceof MechanicalPumpEntity){
            MechanicalPumpEntity upperEntity = (MechanicalPumpEntity) blockEntity;
            this.upperSpeed = upperEntity.getSpeed();
        }
    }

    private void tryAddWaterToBelow(){
        BlockPos below = pos.down();
        BlockEntity blockEntity =  world.getBlockEntity(below);
        if (blockEntity instanceof FluidTankBlockEntity){
            FluidTankBlockEntity fluidTankBlockEntity = (FluidTankBlockEntity) blockEntity;
            try (Transaction t = Transaction.openOuter()){
                fluidTankBlockEntity.getFluidStorage(Direction.UP).insert(FluidVariant.of(Fluids.WATER),getWaterPerTick(this.upperSpeed) ,t);
                t.commit();
            }
        }
    }

    private long getWaterPerTick(double speed){
        return (long) Math.abs(WATER_PER_TICK *speed);
    }

    @Override
    public ActionResult onSneakWrenched(BlockState state, ItemUsageContext context) {
        return IWrenchable.super.onSneakWrenched(state, context);
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        if (this.getCachedState().get(MechanicalPump.HALF).equals(DoubleBlockHalf.UPPER)){
            float stressAtBase = calculateStressApplied();

            Lang.translate("gui.goggles.kinetic_stats")
                    .forGoggles(tooltip);

            addStressImpactStats(tooltip, stressAtBase);

            addWaterStatToTooltip(tooltip);
            return true;
        }else {
            return false;
        }
    }

    private void addWaterStatToTooltip(List<Text> tooltip){
        Lang.translate("gui.goggles.pump_stats")

                .forGoggles(tooltip);
        Lang.translate("tooltip.water_per_tick")
                .style(GRAY)
                .forGoggles(tooltip);

        Lang.number(getWaterPerTick(speed))
                .translate("generic.unit.mb_per_tick")
                .style(Formatting.AQUA)
                .space()
                .add(Lang.translate("gui.goggles.at_current_speed")
                        .style(Formatting.DARK_GRAY))
                .forGoggles(tooltip, 1);
    }
}
