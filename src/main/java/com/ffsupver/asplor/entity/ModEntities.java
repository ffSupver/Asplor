package com.ffsupver.asplor.entity;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.ModTags;
import com.ffsupver.asplor.entity.custom.AlloyChestEntity;
import com.ffsupver.asplor.entity.custom.AstraMob;
import com.ffsupver.asplor.entity.custom.Meteorite;
import com.ffsupver.asplor.entity.custom.Ranger;
import com.ffsupver.asplor.entity.custom.rocket.AdvanceRocketEntity;
import com.ffsupver.asplor.entity.custom.rocket.CargoRocketEntity;
import com.ffsupver.asplor.item.ModItems;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class ModEntities {

    public static final EntityType<AlloyChestEntity> ALLOY_CHEST = registerEntity("alloy_chest",
        FabricEntityTypeBuilder.<AlloyChestEntity>create(SpawnGroup.MISC, AlloyChestEntity::new)
            .dimensions(EntityDimensions.fixed(1.0f, 1.0f)) // 设置实体大小
            .trackRangeBlocks(80).trackedUpdateRate(3) // 设置追踪范围和更新频率
        .build());

    public static final EntityType<Rocket> TIER_0_ROCKET = registerEntity("tier_0_rocket",
            FabricEntityTypeBuilder.<Rocket>create(SpawnGroup.MISC,(entityType,world)->new Rocket(entityType,world,new Rocket.RocketProperties(0, ModItems.TIER_0_ROCKET,0.8F, ModTags.Fluids.TIER_0_ROCKET_FUEL)))
                    .dimensions(EntityDimensions.fixed(1.0F, 3.875F))
                    .build());

    public static final EntityType<CargoRocketEntity> CARGO_ROCKET = registerEntity("cargo_rocket",
            FabricEntityTypeBuilder.<CargoRocketEntity>create(SpawnGroup.MISC, CargoRocketEntity::new)
                    .dimensions(EntityDimensions.fixed(17/16f,4.3f))
                    .build()
    );
    public static final EntityType<Ranger> RANGER = registerEntity("ranger",
            FabricEntityTypeBuilder.<Ranger>create(SpawnGroup.MISC, Ranger::new)
                    .dimensions(EntityDimensions.fixed(1f,1f))
                    .build()
    );
    public static final EntityType<AdvanceRocketEntity> ADVANCE_ROCKET = registerEntity("advance_rocket",
            FabricEntityTypeBuilder.<AdvanceRocketEntity>create(SpawnGroup.MISC, AdvanceRocketEntity::new)
                    .dimensions(EntityDimensions.fixed(1.3f,6.7f))
                    .build()
    );
    public static final EntityType<Meteorite> METEORITE = registerEntity("meteorite",
            FabricEntityTypeBuilder.<Meteorite>create(SpawnGroup.MISC, Meteorite::new)
                    .dimensions(EntityDimensions.fixed(2.0f, 2.0f)) // 设置实体大小
                    .trackRangeBlocks(384).trackedUpdateRate(3) // 设置追踪范围和更新频率
                    .build()
    );

    public static final EntityType<AstraMob> ASTRA_MOB = registerEntity("astra_mod",
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER,AstraMob::new)
                    .dimensions(EntityDimensions.fixed(3,3))
                    .build()
    );
    public static <T extends Entity> EntityType<T> registerEntity(String id, EntityType<T> entityType){
        return Registry.register(Registries.ENTITY_TYPE,new Identifier(Asplor.MOD_ID,id),
                entityType);
    }

    private static void registerSpawnRestriction(){
        SpawnRestriction.register(ASTRA_MOB, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AstraMob::canSpawnInDark);
    }

    private  static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(ASTRA_MOB,AstraMob.createMobAttributes());
    }

    public static void register(){
        registerSpawnRestriction();

        registerAttributes();
    }
}
