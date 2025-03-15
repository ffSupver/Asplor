package com.ffsupver.asplor.util;

import com.ffsupver.asplor.block.SmartEnergyStorage;
import com.ffsupver.asplor.item.item.NavigationChipItem;
import com.ffsupver.asplor.networking.packet.worldAdder.PlanetCreatingData;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

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

    public static boolean addPlanetDataDisplayToGoggles(List<Text> tooltip, PlanetCreatingData planet, Identifier id){
        boolean empty = planet != null && !planet.isEmpty();
        if (empty){
            LangBuilder planetDescription = Lang.translate("gui.goggles.planet");
            if (id != null){
                planetDescription.add(NavigationChipItem.toPlanetName(id).copy());
            }
            planetDescription.forGoggles(tooltip);
            if (planet.oxygen != null){
                Lang.translate("gui.goggles.planet.oxygen")
                        .add(Text.translatable(
                                        planet.oxygen ?
                                                "description.asplor.meteorite.has_oxygen" : "description.asplor.meteorite.no_oxygen"
                                )
                        )
                        .style(Formatting.GRAY)
                        .forGoggles(tooltip, 1);
            }

            if (planet.temperature != null) {
                tooltip.add(addIndex(Text.translatable("description.asplor.meteorite.temperature", planet.temperature).formatted(Formatting.GRAY), 1));
            }
            if (planet.gravity != null) {
                tooltip.add(addIndex(Text.translatable("description.asplor.meteorite.gravity", planet.gravity).formatted(Formatting.GRAY), 1));
            }
            if (planet.solarPower != null) {
                tooltip.add(addIndex(Text.translatable("description.asplor.meteorite.solar_power", planet.solarPower).formatted(Formatting.GRAY), 1));
            }

        }
        return empty;
    }

    private static Text addIndex(Text text,int index){
        return Text.literal(" ".repeat(index + 4)).append(text);
    }
}
