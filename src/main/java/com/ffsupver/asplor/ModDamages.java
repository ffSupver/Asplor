package com.ffsupver.asplor;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class ModDamages {
    public static final RegistryKey<DamageType> MOLTEN_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,new Identifier(Asplor.MOD_ID,"molten")
    );
    public static DamageSource molten(ServerWorld world) {
        // 获取 DamageType 的 RegistryEntry
        RegistryEntry<DamageType> damageTypeEntry = world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(MOLTEN_TYPE);

        // 创建并返回自定义 DamageSource
        return new DamageSource(damageTypeEntry);
    }
}
