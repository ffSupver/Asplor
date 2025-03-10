package com.ffsupver.asplor;

import com.jozufozu.flywheel.core.PartialModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
@Environment(EnvType.CLIENT)
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
    public static final PartialModel SMART_MECHANICAL_ARM_HEAD = create("block/smart_mechanical_arm/head");
    public static final PartialModel SMART_MECHANICAL_ARM_BASE = create("block/smart_mechanical_arm/base");
    public static final PartialModel SMART_MECHANICAL_ARM_FIRST_ARM = create("block/smart_mechanical_arm/first_arm");
    public static final PartialModel SMART_MECHANICAL_ARM_SECOND_ARM = create("block/smart_mechanical_arm/second_arm");
    public static final PartialModel DRILL_TOOL = create("block/smart_mechanical_arm/tools/drill");
    public static final PartialModel LASER_TOOL = create("block/smart_mechanical_arm/tools/laser");
    public static final PartialModel LASER_TOOL_WORK = create("block/smart_mechanical_arm/tools/laser_work");
    public static final PartialModel EMPTY_DROPPER = create("block/smart_mechanical_arm/tools/empty_dropper");
    public static final PartialModel MOLTEN_GOLD_DROPPER = create("block/smart_mechanical_arm/tools/molten_gold_dropper");
    public static final PartialModel MOLTEN_GOLD_DROPPER_WORK = create("block/smart_mechanical_arm/tools/molten_gold_dropper_work");
    public static final PartialModel GLUE_DROPPER = create("block/smart_mechanical_arm/tools/glue_dropper");
    public static final PartialModel GLUE_DROPPER_WORK = create("block/smart_mechanical_arm/tools/glue_dropper_work");
    public static final PartialModel MOLTEN_ALLOY_DROPPER = create("block/smart_mechanical_arm/tools/molten_alloy_dropper");
    public static final PartialModel MOLTEN_ALLOY_DROPPER_WORK = create("block/smart_mechanical_arm/tools/molten_alloy_dropper_work");
    public static final PartialModel MOLTEN_BRASS_DROPPER = create("block/smart_mechanical_arm/tools/molten_brass_dropper");
    public static final PartialModel MOLTEN_BRASS_DROPPER_WORK = create("block/smart_mechanical_arm/tools/molten_brass_dropper_work");
    public static final PartialModel ROCKET_FUEL_LOADER_PIPE_BASE = create("block/rocket_fuel_loader/pipe_base");
    public static final PartialModel ROCKET_FUEL_LOADER_PIPE_EXTEND = create("block/rocket_fuel_loader/pipe_extend");
    public static final PartialModel MOLTEN_DESH_DROPPER = create("block/smart_mechanical_arm/tools/molten_desh_dropper");
    public static final PartialModel MOLTEN_DESH_DROPPER_WORK = create("block/smart_mechanical_arm/tools/molten_desh_dropper_work");
    public static final PartialModel MOLTEN_OSTRUM_DROPPER = create("block/smart_mechanical_arm/tools/molten_ostrum_dropper");
    public static final PartialModel MOLTEN_OSTRUM_DROPPER_WORK = create("block/smart_mechanical_arm/tools/molten_ostrum_dropper_work");



    private static PartialModel create(String path){
    return new PartialModel(new Identifier(Asplor.MOD_ID,path));
}
    public static void init() {
        // init static fields
    }
}
