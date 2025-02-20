package com.ffsupver.asplor.entity;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.ModTags;
import com.ffsupver.asplor.entity.custom.AlloyChestEntity;
import com.ffsupver.asplor.entity.custom.Ranger;
import com.ffsupver.asplor.entity.custom.rocket.AdvanceRocketEntity;
import com.ffsupver.asplor.entity.custom.rocket.CargoRocketEntity;
import com.ffsupver.asplor.item.ModItems;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
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

    public static final EntityType<Rocket> TIER_0_ROCKET = Registry.register(Registries.ENTITY_TYPE,new Identifier(Asplor.MOD_ID,"tier_0_rocket"),
            FabricEntityTypeBuilder.<Rocket>create(SpawnGroup.MISC,(entityType,world)->new Rocket(entityType,world,new Rocket.RocketProperties(0, ModItems.TIER_0_ROCKET,0.8F, ModTags.Fluids.TIER_0_ROCKET_FUEL)))
                    .dimensions(EntityDimensions.fixed(1.0F, 3.875F))
                    .build());

    public static final EntityType<CargoRocketEntity> CARGO_ROCKET = Registry.register(Registries.ENTITY_TYPE,new Identifier(Asplor.MOD_ID,"cargo_rocket"),
            FabricEntityTypeBuilder.<CargoRocketEntity>create(SpawnGroup.MISC, CargoRocketEntity::new)
                    .dimensions(EntityDimensions.fixed(17/16f,4.3f))
                    .build()
    );
    public static final EntityType<Ranger> RANGER = Registry.register(Registries.ENTITY_TYPE,new Identifier(Asplor.MOD_ID,"ranger"),
            FabricEntityTypeBuilder.<Ranger>create(SpawnGroup.MISC, Ranger::new)
                    .dimensions(EntityDimensions.fixed(1f,1f))
                    .build()
    );
    public static final EntityType<AdvanceRocketEntity> ADVANCE_ROCKET = Registry.register(Registries.ENTITY_TYPE,new Identifier(Asplor.MOD_ID,"advance_rocket"),
            FabricEntityTypeBuilder.<AdvanceRocketEntity>create(SpawnGroup.MISC, AdvanceRocketEntity::new)
                    .dimensions(EntityDimensions.fixed(1.3f,6.7f))
                    .build()
    );

    public static void register(){
    }
}
