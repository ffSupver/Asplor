package com.ffsupver.asplor.enchantment;

import com.ffsupver.asplor.Asplor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static final Enchantment STRENGTH = registerEnchantment("strength",new  StrengthEnchantment(Enchantment.Rarity.RARE,new EquipmentSlot[]{EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND}));
    public static final Enchantment MECHANICALLY_PROFICIENT = registerEnchantment("mechanically_proficient",new MechanicallyProficientEnchantment(Enchantment.Rarity.RARE,new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
    private static Enchantment registerEnchantment(String name,Enchantment enchantment){
        return Registry.register(Registries.ENCHANTMENT,new Identifier(Asplor.MOD_ID,name),enchantment);
    }

    public static void register(){}
    @Environment(EnvType.CLIENT)
    public static void registerDescription(){
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (Screen.hasShiftDown()){
                NbtList es = stack.getEnchantments();
                for (NbtElement e : es) {
                    NbtCompound eNbt = (NbtCompound) e;
                    Registries.ENCHANTMENT
                            .getOrEmpty(EnchantmentHelper.getIdFromNbt(eNbt))
                            .ifPresent(enchantment -> {
                                if (enchantment instanceof DescriptionEnchantment d) {
                                    Text text = enchantment.getName(EnchantmentHelper.getLevelFromNbt(eNbt));
                                    for (int i = 0; i < lines.size(); i++) {
                                        if (text.toString().equals(lines.get(i).toString())) {
                                            lines.add(i + 1, d.getDescription());
                                            i++;
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
}
