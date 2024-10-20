package com.ffsupver.asplor;

import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AllBoilerHeaters {
    public static BoilerHeaters.Heater LIQUID_BLAZE_BURNER = new BoilerHeaters.Heater() {
        @Override
        public float getActiveHeat(World level, BlockPos pos, BlockState state) {
            if (state.contains(BlazeBurnerBlock.HEAT_LEVEL)){
                return switch (state.get(BlazeBurnerBlock.HEAT_LEVEL)) {
                    case FADING -> 1;
                    case SEETHING -> 2;
                    case KINDLED -> 1;
                    default -> -1;
                };
            }
            return 0;
        }
    };
    public static void register(){
        BoilerHeaters.registerHeater(AllBlocks.LIQUID_BLAZE_BURNER.get(), LIQUID_BLAZE_BURNER);
    }
}
