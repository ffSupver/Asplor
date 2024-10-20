package com.ffsupver.asplor.mixin.disablePortal;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortal.class)
public class NetherPortalMixin {

    @Shadow @Final private WorldAccess world;

    @Shadow private int foundPortalBlocks;

    @Shadow @Nullable private BlockPos lowerCorner;


    @Shadow @Final private Direction negativeDir;

    @Shadow private int height;

    @Shadow @Final private int width;

    @Inject(method = "createPortal",at = @At(value = "HEAD"), cancellable = true)
    public void createPortal(CallbackInfo ci) {
        World worldOfPortal = (World) world;
        if (!worldOfPortal.isClient()){
           ServerWorld serverWorld = (ServerWorld)worldOfPortal;
            Vec3d vec3d = lowerCorner.toCenterPos().add(0, (double) height /2 - 0.5,0).offset(negativeDir, (double) width /2 - 0.5);
            float power = (float) (width * height) /(23*23) * 15 + 5;
            serverWorld.createExplosion(null, serverWorld.getDamageSources().badRespawnPoint(vec3d), null, vec3d, power, false, World.ExplosionSourceType.BLOCK);
        }
        ci.cancel();
    }
}
