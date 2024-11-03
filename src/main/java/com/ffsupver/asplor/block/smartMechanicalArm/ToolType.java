package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.item.item.ToolItem;
import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ToolType {

    private final Item toolItem;
    private final Item usedToolItem;
    private final Identifier id;
    public ToolType(Item toolItem, Item usedToolItem, Identifier id){
        this.toolItem = toolItem;
        this.usedToolItem = usedToolItem;
        this.id = id;
    }
    public NbtCompound writeToNbt(){
        NbtCompound data = new NbtCompound();
        data.putString("tool",id.toString());
        return data;
    }

    public static ToolType readFromNbt(NbtCompound nbt){
       Identifier id = new Identifier(nbt.getString("tool"));
        return getById(id);
    }

    public static ToolType getById(Identifier id){
        if (ToolTypes.TOOL_TYPES.containsKey(id)){
            return ToolTypes.TOOL_TYPES.get(id);
        }
        return ToolTypes.EMPTY;
    }

    public PartialModel getToolModel(){
        if (toolItem instanceof ToolItem toolItem1){
            return toolItem1.getToolModel();
        }
        return null;
    }

    public PartialModel getToolWorkModel(){
        if (toolItem instanceof ToolItem toolItem1){
            return toolItem1.getToolWorkModel();
        }
        return null;
    }

    public Text getRecipeText(){
        String[] id = this.id.toString().split(":");
        return Text.translatable("tool_type."+id[0]+"."+id[1]);
    }

    public Item getToolItem() {
        return toolItem;
    }

    public Item getUsedToolItem() {
        return usedToolItem;
    }

    @Override
    public String toString() {
        return this.id.toString()+"   "+this.toolItem.toString();
    }

    public Identifier getId() {
        return id;
    }

}
