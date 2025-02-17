package com.ffsupver.asplor.mixin.lightningAbsorber;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.ModPointOfInterestTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements WorldView {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Shadow public abstract PointOfInterestStorage getPointOfInterestStorage();

//    @Shadow protected abstract BlockPos getLightningPos(BlockPos pos);

    @Shadow protected abstract BlockPos getLightningPos(BlockPos pos);

    @Inject(method = "getLightningRodPos",at = @At(value = "HEAD"), cancellable = true)
    private void getLightningRodPos(BlockPos pos, CallbackInfoReturnable<Optional<BlockPos>> cir) {
        Optional<BlockPos> optional = this.getPointOfInterestStorage()
                .getNearestPosition(
                        poiType -> poiType.matchesKey(ModPointOfInterestTypes.LIGHTNING_ABSORBER_KEY),
                        innerPos -> innerPos.getY() == this.getTopY(Heightmap.Type.WORLD_SURFACE, innerPos.getX(), innerPos.getZ()) - 1,
                        pos,
                        128,
                        PointOfInterestStorage.OccupationStatus.ANY
                );
        if (optional.isPresent()) {
            cir.setReturnValue(optional);
        }
    }
    @Inject(method = "tickChunk",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/world/ServerWorld;getLightningPos(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;"))
    private void tickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        BlockPos blockPos = this.getLightningPos(this.getRandomPosInChunk(i, 0, j, 15));
        if (this.hasRain(blockPos.up())) {
            if (getBlockState(blockPos).isOf(AllBlocks.LIGHTNING_ABSORBER.get())) {
                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(this);
                if (lightningEntity != null) {
                    lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos.up()));
                    this.spawnEntity(lightningEntity);
                }
            }
        }
    }
}
