package com.ffsupver.asplor.entity;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.entity.custom.AlloyChestEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<AlloyChestEntity> ALLOY_CHEST = Registry.register(Registries.ENTITY_TYPE,new Identifier(Asplor.MOD_ID,"alloy_chest"),
        FabricEntityTypeBuilder.<AlloyChestEntity>create(SpawnGroup.MISC, AlloyChestEntity::new)
            .dimensions(EntityDimensions.fixed(1.0f, 1.0f)) // 设置实体大小
            .trackRangeBlocks(80).trackedUpdateRate(3) // 设置追踪范围和更新频率
        .build());



    public static void register(){
//        FabricDefaultAttributeRegistry.register(CART,CartEntity.createCartAttribute());
    }
}
