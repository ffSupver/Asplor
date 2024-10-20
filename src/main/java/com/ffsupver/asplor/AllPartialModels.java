package com.ffsupver.asplor;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.util.Identifier;

public class AllPartialModels {
    public static final PartialModel Divider_ROTATING_MODEL = create("block/divider/divider_rotating_model");
    public static final PartialModel ALLOY_MECHANICAL_PRESS_HEAD = create("block/alloy_mechanical_press/head");
    public static final PartialModel BLAZE_SUPER_ACTIVE = create("block/liquid_blaze_burner/blaze/super_active");
    public static final PartialModel BLAZE_SUPER=create("block/liquid_blaze_burner/blaze/super");
    public static final PartialModel BLAZE_ACTIVE=create("block/liquid_blaze_burner/blaze/active");
    public static final PartialModel BLAZE_IDLE=create("block/liquid_blaze_burner/blaze/idle");
    public static final PartialModel BLAZE_INERT=create("block/liquid_blaze_burner/blaze/inert");
    public static final PartialModel GENERATOR_ROTATING_MODEL = create("block/generator/generator_rotating_model");
    public static final PartialModel MOTOR_ROTATING_MODEL = create("block/motor/motor_rotating_model");
    public static final PartialModel TIME_INJECTOR_INNER_RING = create("block/time_injector/inner_ring");
    public static final PartialModel SPACE_TELEPORTER_INNER = create("block/space_teleporter/inner");



private static PartialModel create(String path){
    return new PartialModel(new Identifier(Asplor.MOD_ID,path));
}
    public static void init() {
        // init static fields
    }
}
