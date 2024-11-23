package com.ffsupver.asplor.enchantment;

import com.ffsupver.asplor.Asplor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static final Enchantment STRENGTH = registerEnchantment("strength",new  StrengthEnchantment(Enchantment.Rarity.RARE,new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
    private static Enchantment registerEnchantment(String name,Enchantment enchantment){
        return Registry.register(Registries.ENCHANTMENT,new Identifier(Asplor.MOD_ID,name),enchantment);
    }

    public static void register(){}
}
