package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.item.ModItems;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ToolTypes {
    public static Map<Identifier,ToolType> TOOL_TYPES = new HashMap<>();
    public static ToolType EMPTY = registerToolType(new ToolType(Items.AIR,Items.AIR,new Identifier(Asplor.MOD_ID,"empty")));
    public static ToolType DRILL;
    public static ToolType LASER;
    public static ToolType MOLTEN_GOLD_DROPPER;
    public static ToolType GLUE_DROPPER;
    public static ToolType MOLTEN_ALLOY_DROPPER;
    public static ToolType registerToolType(ToolType toolType){
        TOOL_TYPES.put(toolType.getId(),toolType);
        return toolType;
    }
    public static void register(){
        DRILL = registerToolType(new ToolType(ModItems.DRILL_TOOL,ModItems.USED_DRILL_TOOL,new Identifier(Asplor.MOD_ID,"drill")));
        LASER = registerToolType(new ToolType(ModItems.LASER_TOOL,ModItems.USED_LASER_TOOL,new Identifier(Asplor.MOD_ID,"laser")));
        MOLTEN_GOLD_DROPPER = registerToolType(new ToolType(ModItems.MOLTEN_GOLD_DROPPER,ModItems.EMPTY_DROPPER,new Identifier(Asplor.MOD_ID,"molten_gold_dropper")));
        GLUE_DROPPER = registerToolType(new ToolType(ModItems.GLUE_DROPPER,ModItems.EMPTY_DROPPER,new Identifier(Asplor.MOD_ID,"glue_dropper")));
        MOLTEN_ALLOY_DROPPER = registerToolType(new ToolType(ModItems.MOLTEN_ALLOY_DROPPER,ModItems.EMPTY_DROPPER,new Identifier(Asplor.MOD_ID,"molten_alloy_dropper")));

    }
}
