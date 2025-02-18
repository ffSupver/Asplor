package com.ffsupver.asplor.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DescriptionEnchantment extends Enchantment {
    private final Text description;
    protected DescriptionEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes, Text description) {
        super(weight, target, slotTypes);
        this.description = description.copy().formatted(Formatting.GRAY);
    }

    public Text getDescription() {
        return description;
    }
}
