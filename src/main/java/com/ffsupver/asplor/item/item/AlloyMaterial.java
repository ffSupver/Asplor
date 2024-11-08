package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.item.ModItems;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class AlloyMaterial {
    public static class Armor implements ArmorMaterial{
        //头胸腿鞋
        public static final Armor MATERIAL = new Armor();
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
    public static class Tool implements ToolMaterial{
        public static final Tool MATERIAL = new Tool();
        public static Item.Settings toolSettings(Item.Settings settings){
            return settings.maxCount(0);
        }

        @Override
        public int getDurability() {
            return 1796;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return 8.5F;
        }

        @Override
        public float getAttackDamage() {
            return 4.0F;
        }

        @Override
        public int getMiningLevel() {
            return 4;
        }

        @Override
        public int getEnchantability() {
            return 13;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(ModItems.ALLOY_INGOT);
        }
    }
}
