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
    public static ToolType registerToolType(ToolType toolType){
        TOOL_TYPES.put(toolType.getId(),toolType);
        return toolType;
    }
    public static void register(){
        DRILL = registerToolType(new ToolType(ModItems.DRILL_TOOL,ModItems.USED_DRILL_TOOL,new Identifier(Asplor.MOD_ID,"drill")));
        LASER = registerToolType(new ToolType(ModItems.LASER_TOOL,ModItems.USED_LASER_TOOL,new Identifier(Asplor.MOD_ID,"laser")));
    }
}
