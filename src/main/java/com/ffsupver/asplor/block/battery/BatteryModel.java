package com.ffsupver.asplor.block.battery;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankCTBehaviour;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.Arrays;
import java.util.function.Supplier;

public class BatteryModel extends CTModel {

    public static BatteryModel standard(BakedModel originalModel) {
        return new BatteryModel(originalModel, AllSpriteShifts.FLUID_TANK, AllSpriteShifts.FLUID_TANK_TOP,
                AllSpriteShifts.FLUID_TANK_INNER);
    }

    public static BatteryModel creative(BakedModel originalModel) {
        return new BatteryModel(originalModel, AllSpriteShifts.CREATIVE_FLUID_TANK, AllSpriteShifts.CREATIVE_CASING,
                AllSpriteShifts.CREATIVE_CASING);
    }

    private BatteryModel(BakedModel originalModel, CTSpriteShiftEntry side, CTSpriteShiftEntry top,
                           CTSpriteShiftEntry inner) {
        super(originalModel, new FluidTankCTBehaviour(side, top, inner));
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        BatteryModel.CullData cullData = new BatteryModel.CullData();
        for (Direction d : Iterate.horizontalDirections)
            cullData.setCulled(d, ConnectivityHandler.isConnected(blockView, pos, pos.offset(d)));

        context.pushTransform(quad -> {
            Direction cullFace = quad.cullFace();
            if (cullFace != null && cullData.isCulled(cullFace)) {
                return false;
            }
            quad.cullFace(null);
            return true;
        });
        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        context.popTransform();
    }

    private static class CullData {
        boolean[] culledFaces;

        public CullData() {
            culledFaces = new boolean[4];
            Arrays.fill(culledFaces, false);
        }

        void setCulled(Direction face, boolean cull) {
            if (face.getAxis()
                    .isVertical())
                return;
            culledFaces[face.getHorizontal()] = cull;
        }

        boolean isCulled(Direction face) {
            if (face.getAxis()
                    .isVertical())
                return false;
            return culledFaces[face.getHorizontal()];
        }
    }

}
