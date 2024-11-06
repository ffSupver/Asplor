package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.item.ModItems;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class AlloyMaterial implements ArmorMaterial {
    //头胸腿鞋
    public static final ArmorMaterial MATERIAL = new AlloyMaterial();
    private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
    private static final int[] PROTECTION_VALUES = new int[]{2, 5, 6, 2};
    @Override
    public int getDurability(ArmorItem.Type type) {
        return BASE_DURABILITY[type.ordinal()]*35;
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return PROTECTION_VALUES[type.ordinal()];
    }

    @Override
    public int getEnchantability() {
        return 13;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ModItems.ALLOY_INGOT);
    }

    @Override
    public String getName() {
        return "alloy";
    }

    @Override
    public float getToughness() {
        return 2.5f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.05f;
    }
}
