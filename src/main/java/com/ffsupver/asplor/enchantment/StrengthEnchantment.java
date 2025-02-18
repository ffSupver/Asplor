package com.ffsupver.asplor.enchantment;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.text.Text;

public class StrengthEnchantment extends DescriptionEnchantment {

    protected StrengthEnchantment(Rarity weight, EquipmentSlot[] slotTypes) {
        super(weight, EnchantmentTarget.CROSSBOW, slotTypes, Text.translatable("description.asplor.enchantment.strength"));
    }

    public int getMinPower(int level) {
        return 1 + (level - 1) * 10;
    }

    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    public int getMaxLevel() {
        return 3;
    }
}
