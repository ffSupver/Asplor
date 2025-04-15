package com.ffsupver.asplor.entity.custom;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.sound.ModSounds;
import com.simibubi.create.AllItems;
import net.minecraft.block.SkullBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ZombifiedCosmonaut extends ZombieEntity {

    public ZombifiedCosmonaut(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public ZombifiedCosmonaut(World world) {
        super(world);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ZOMBIFIED_COSMONAUT_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ZOMBIFIED_COSMONAUT_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ZOMBIFIED_COSMONAUT_HURT;
    }

    public static DefaultAttributeContainer.Builder createZombifiedCosmonautAttributes() {
       return createZombieAttributes()
               .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
               .add(EntityAttributes.GENERIC_ARMOR, 4.0);
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        if (random.nextFloat() < (this.getWorld().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
            int i = random.nextInt(10);
            ItemStack handItem;
            if (i > 2) {
                handItem = new ItemStack(AllItems.WRENCH);
                if (random.nextInt(3) == 0){
                    handItem.addEnchantment(Enchantments.LOOTING,3);

                    handItem.addAttributeModifier(
                            EntityAttributes.GENERIC_ATTACK_DAMAGE,
                            new EntityAttributeModifier(
                                    UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"),//物品描述
                                    "Weapon modifier",
                                    6.0,
                                    EntityAttributeModifier.Operation.ADDITION
                            ),
                            EquipmentSlot.MAINHAND
                    );
                    handItem.addAttributeModifier(
                            EntityAttributes.GENERIC_ATTACK_SPEED,
                            new EntityAttributeModifier(
                                    UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"),
                                    "Weapon modifier",
                                    -2.0,
                                    EntityAttributeModifier.Operation.ADDITION
                            ),
                            EquipmentSlot.MAINHAND
                    );
                }
            }else {
                handItem = new ItemStack(ModItems.ALLOY_SWORD);
                int eR = random.nextInt(5);
                if (eR == 0){
                    EnchantmentHelper.enchant(random, handItem, 30, true);
                }else if (eR < 2){
                    EnchantmentHelper.enchant(random, handItem, 15, false);
                }
            }
            this.equipStack(EquipmentSlot.MAINHAND, handItem);
        };
    }

    private void equipChestPlate(Random random){
        ItemStack alloyChest = new ItemStack(ModItems.ALLOY_CHESTPLATE);
        EnchantmentHelper.enchant(random,alloyChest,15,true);
        this.equipStack(EquipmentSlot.CHEST,alloyChest);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
       EntityData result = super.initialize(world,difficulty,spawnReason,entityData,entityNbt);
       if (getVehicle() instanceof ChickenEntity chickenEntity){
           this.stopRiding();
           chickenEntity.discard();
           equipChestPlate(world.getRandom());
       }
       return result;
    }

    @Override
    protected ItemStack getSkull() {
        return new ItemStack(AllBlocks.ZOMBIFIED_COSMONAUT_HEAD.asItem());
    }

    public enum SkullType implements SkullBlock.SkullType{
        ZOMBIFIED_COSMONAUT
    }
}
