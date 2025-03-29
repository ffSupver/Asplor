package com.ffsupver.asplor.planet;

import com.ffsupver.asplor.ModDamages;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.sound.ModSounds;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

import java.util.List;

public class Charged {
    public static final String CHARGED_PROOF = "charged_proof";
    public static final List<EquipmentSlot> PROOF_SLOT = List.of(
            EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET
    );
    public static void tick(LivingEntity entity, ServerWorld world){
        if (!isChargedProof(entity) && entity.age % 20 == 0 && EnvironmentTicks.hasOxygen(entity,world)){
            boolean damaged = entity.damage(ModDamages.charge(world), 2.0f);

            if (damaged && entity instanceof PlayerEntity player && !(player.isCreative() && player.isSpectator())) {
                world.playSoundFromEntity(null,entity, ModSounds.ELECTRICITY_WORK, SoundCategory.AMBIENT,10f,1f);
            }
        }
    }

    public static boolean isChargedProof(LivingEntity entity){
        boolean result = true;
        for (EquipmentSlot equipmentSlot : PROOF_SLOT){
           result = result && isChargedProof(entity.getEquippedStack(equipmentSlot));
        }
        return result;
    }

    public static boolean isChargedProof(ItemStack itemStack){
        NbtCompound nbt = itemStack.getOrCreateNbt();
        return nbt.contains(CHARGED_PROOF, NbtElement.BYTE_TYPE) && nbt.getBoolean(CHARGED_PROOF);
    }

    public static boolean canAddChargedProof(ItemStack input,ItemStack addition){
        if (input.getItem() instanceof Equipment equipment){
            return PROOF_SLOT.contains(equipment.getSlotType()) && addition.isOf(ModItems.CHARGED_ALLOY_INGOT);
        }
        return false;
    }

    public static ItemStack addChargedProof(ItemStack itemStack){
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putBoolean(CHARGED_PROOF,true);
        itemStack.setNbt(nbt);
        return itemStack;
    }
}
