package com.ffsupver.asplor.mixin;

import com.ffsupver.asplor.enchantment.MechanicallyProficientEnchantment;
import com.ffsupver.asplor.enchantment.ModEnchantments;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract boolean isDamageable();

    @Shadow public abstract ItemStack copy();

    @Inject(method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z",
    at = @At(value = "HEAD"), cancellable = true)
    public void damage(int amount, Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir){
        if (isDamageable() && player instanceof DeployerFakePlayer){
            ItemStack stack = copy();
            int i = EnchantmentHelper.getLevel(ModEnchantments.MECHANICALLY_PROFICIENT, stack);
            int j = 0;
            for (int k = 0; i > 0 && k < amount; k++) {
                if (MechanicallyProficientEnchantment.shouldPreventDamage(i, random)) {
                    j++;
                }
            }
            amount -= j;
            if (amount <= 0) {
                cir.setReturnValue(false);
            }
        }
    }
}
