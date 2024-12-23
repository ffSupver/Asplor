package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.entity.ModEntities;
import com.ffsupver.asplor.entity.custom.cargoRocket.CargoRocketEntity;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import earth.terrarium.adastra.common.blocks.properties.LaunchPadPartProperty;
import earth.terrarium.adastra.common.items.rendered.RenderedItem;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.tags.ModBlockTags;
import earth.terrarium.adastra.common.tags.ModFluidTags;
import earth.terrarium.adastra.common.utils.FluidUtils;
import earth.terrarium.adastra.common.utils.TooltipUtils;
import earth.terrarium.botarium.common.fluid.FluidApi;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.BotariumFluidItem;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.ItemFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.WrappedItemFluidContainer;
import earth.terrarium.botarium.common.fluid.utils.ClientFluidHooks;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CargoRocketItem extends RenderedItem implements BotariumFluidItem<WrappedItemFluidContainer> {

    public CargoRocketItem(Settings properties) {
        super(properties);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        World level = context.getWorld();
        if (level.isClient()) {
            return ActionResult.PASS;
        } else {
            BlockPos pos = context.getBlockPos();
            ItemStack stack = context.getStack();
            BlockState state = level.getBlockState(pos);
            if (!state.isIn(ModBlockTags.LAUNCH_PADS)) {
                return ActionResult.PASS;
            } else if (state.contains(LaunchPadBlock.PART) && state.get(LaunchPadBlock.PART) != LaunchPadPartProperty.CENTER) {
                return ActionResult.PASS;
            } else {
                level.playSound(null, pos, SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                Entity vehicle = ModEntities.CARGO_ROCKET.create(level);
                if (vehicle == null) {
                    return ActionResult.PASS;
                } else {
                    vehicle.setPosition((double)pos.getX() + 0.5, (float)pos.getY() + 0.125F, (double)pos.getZ() + 0.5);
                    vehicle.setYaw(context.getHorizontalPlayerFacing().getOpposite().asRotation());
                    level.spawnEntity(vehicle);
                    if (vehicle instanceof CargoRocketEntity rocket) {
                        ItemStackHolder holder = new ItemStackHolder(stack);
                        FluidContainer container = this.getFluidContainer(stack).container();
                        ItemFluidContainer fromContainer = FluidContainer.of(holder);
                        if (fromContainer == null) {
                            return ActionResult.PASS;
                        }

                        FluidApi.moveFluid(fromContainer, rocket.fluidContainer(), container.getFirstFluid(), false);
                    }

                    stack.decrement(1);
                    return ActionResult.SUCCESS;
                }
            }
        }
    }

    @Override
    public WrappedItemFluidContainer getFluidContainer(ItemStack holder) {
        return new WrappedItemFluidContainer(holder, new SimpleFluidContainer(FluidConstants.fromMillibuckets(6000L), 1, (t, f) -> f.is(ModFluidTags.FUEL)));
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ClientFluidHooks.getFluidColor(FluidUtils.getTank(stack));
    }

    @Override
    public int getItemBarStep(@NotNull ItemStack stack) {
        WrappedItemFluidContainer fluidContainer = this.getFluidContainer(stack);
        return (int)((double)fluidContainer.getFirstFluid().getFluidAmount() / (double)fluidContainer.getTankCapacity(0) * 13.0);
    }

    @Override
    public boolean isItemBarVisible(@NotNull ItemStack stack) {
        return FluidUtils.hasFluid(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltipComponents, TooltipContext context) {
        tooltipComponents.add(TooltipUtils.getFluidComponent(FluidUtils.getTank(stack), FluidUtils.getTankCapacity(stack), ModFluids.FUEL.get()));
    }
}
