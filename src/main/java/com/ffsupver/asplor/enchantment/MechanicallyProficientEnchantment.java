package com.ffsupver.asplor.enchantment;

import com.ffsupver.asplor.item.ModItems;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class MechanicallyProficientEnchantment extends DescriptionEnchantment {
    private static final int MAX_LEVEL = 3;
    protected MechanicallyProficientEnchantment(Rarity weight, EquipmentSlot[] slotTypes) {
        super(weight, EnchantmentTarget.BREAKABLE, slotTypes,
                Text.translatable("description.asplor.enchantment.mechanically_proficient").append(
                        Text.translatable("enchantment.level."+MAX_LEVEL)
                )
        );
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        Item item = stack.getItem();
        return stack.isDamageable() ? !stack.isOf(ModItems.INFUSION_CLOCK) && !(item instanceof ArmorItem || item instanceof ShieldItem)
                : super.isAcceptableItem(stack);
    }

    public static boolean shouldPreventDamage(int level, Random random) {
        return level >= MAX_LEVEL || random.nextInt(level + 1) > 0;
    }
}
