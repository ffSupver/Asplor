package com.ffsupver.asplor.mixin;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureBlockBlockEntity.class)
public class StructureBlockEntity {
    @Shadow private Vec3i size;

    @Inject(method = "readNbt",at = @At(value = "TAIL"))
    public void largeSize(NbtCompound nbt, CallbackInfo ci){
        int l = MathHelper.clamp(nbt.getInt("sizeX"), 0, 256);
        int m = MathHelper.clamp(nbt.getInt("sizeY"), 0, 256);
        int n = MathHelper.clamp(nbt.getInt("sizeZ"), 0, 256);
        size = new Vec3i(l,m,n);
    }
}
