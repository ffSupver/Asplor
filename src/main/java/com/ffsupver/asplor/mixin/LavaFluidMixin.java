package com.ffsupver.asplor.mixin;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public abstract class LavaFluidMixin  extends FlowableFluid {
    @Shadow protected abstract void playExtinguishEvent(WorldAccess world, BlockPos pos);

    @Inject(method = "flow",at= @At(value = "INVOKE",target = "Lnet/minecraft/world/WorldAccess;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"), cancellable = true)
    protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo ci){
        FluidState fluidState1 = world.getFluidState(pos);
        if (this.isIn(FluidTags.LAVA) && fluidState1.isIn(ModTags.Fluids.GLUE)) {
            if (state.getBlock() instanceof FluidBlock) {
                world.setBlockState(pos, Blocks.CLAY.getDefaultState(), 3);
            }

            this.playExtinguishEvent(world, pos);
            ci.cancel();
        }
    }
}
