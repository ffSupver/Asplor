package com.ffsupver.asplor;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class ModDamages {
    public static final RegistryKey<DamageType> MOLTEN_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,new Identifier(Asplor.MOD_ID,"molten"));
    public static final RegistryKey<DamageType> CHARGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,new Identifier(Asplor.MOD_ID,"charge"));
    public static DamageSource molten(ServerWorld world) {
        return get(world,MOLTEN_TYPE);
    }
    public static DamageSource charge(ServerWorld world) {
        return get(world,CHARGE_TYPE);
    }
    public static DamageSource get(ServerWorld world,RegistryKey<DamageType> type){
        // 获取 DamageType 的 RegistryEntry
        RegistryEntry<DamageType> damageTypeEntry = world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(type);

        // 创建并返回自定义 DamageSource
        return new DamageSource(damageTypeEntry);
    }
}
