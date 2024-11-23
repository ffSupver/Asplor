package com.ffsupver.asplor.mixin;

import com.ffsupver.asplor.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)

public abstract class CrossbowItemMixin {

    @Inject(method = "getSpeed",at = @At(value = "HEAD"), cancellable = true)
    private static void getSpeed(ItemStack stack, CallbackInfoReturnable<Float> cir){
        int level = EnchantmentHelper.getLevel(ModEnchantments.STRENGTH,stack);
        if (level != 0){
            float originSpeed = CrossbowItem.hasProjectile(stack, Items.FIREWORK_ROCKET)? 1.6F : 3.15F;
            cir.setReturnValue(originSpeed * (level/2.0f+1));
        }
    }
}
