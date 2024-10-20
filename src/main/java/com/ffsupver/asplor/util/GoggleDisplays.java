package com.ffsupver.asplor.util;

import com.ffsupver.asplor.block.SmartEnergyStorage;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class GoggleDisplays {
    public static void register(){
        //注册工程师护目镜显示
        GogglesItem.addIsWearingPredicate(player ->{
                    ItemStack itemStack = player.getEquippedStack(EquipmentSlot.HEAD);
                    return itemStack.hasNbt()&&itemStack.getNbt().getBoolean("goggle");
                }
        );
    }

    public static boolean addEnergyDisplayToGoggles(List<Text> tooltip, SmartEnergyStorage energyStorage){
        Lang.translate("gui.goggles.battery")
                .forGoggles(tooltip);
        Lang.translate("gui.goggles.battery.energy_stored")
                .add(Lang.text(" "+(float)energyStorage.getAmount()/1000+"k").style(Formatting.GOLD))
                .add(Lang.text(" / ").style(Formatting.GRAY))
                .add(Lang.text((float)energyStorage.getCapacity()/1000+"k ").style(Formatting.GOLD))
                .add(Lang.text("E").style(Formatting.AQUA))
                .style(Formatting.GRAY)
                .forGoggles(tooltip, 1);
        return true;
    }
}
