package com.ffsupver.asplor.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
    @Accessor
    public abstract Executor getWorkerExecutor();

    @Accessor
    public abstract LevelStorage.Session getSession();

    @Accessor
    public abstract WorldGenerationProgressListenerFactory getWorldGenerationProgressListenerFactory();


    @Accessor
    public abstract Map<RegistryKey<World>, ServerWorld> getWorlds();


    @Accessor
    public abstract SaveProperties getSaveProperties();


}
