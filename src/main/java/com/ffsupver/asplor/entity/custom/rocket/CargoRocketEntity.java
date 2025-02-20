package com.ffsupver.asplor.entity.custom.rocket;

import com.ffsupver.asplor.entity.ModEntities;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.screen.rocket.CargoRocketScreenHandler;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.api.planets.PlanetApi;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CargoRocketEntity extends RoundTripRocketEntity {
    public CargoRocketEntity(World world, Vec3d pos, Vec3d velocity) {
        super(ModEntities.CARGO_ROCKET,world, pos, velocity);
    }

    public CargoRocketEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected int getInventorySize() {
        return 46;
    }

    @Override
    protected Optional<RegistryKey<World>> getTargetWorldKeyOptional(Planet planet) {
        return PlanetApi.API.isSpace(getWorld()) ? planet.getOrbitPlanet() : planet.orbit();
    }

    @Override
    protected @NotNull ItemStack asItemStack() {
        return ModItems.CARGO_ROCKET.getDefaultStack();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CargoRocketScreenHandler(syncId,playerInventory,this);
    }
}
