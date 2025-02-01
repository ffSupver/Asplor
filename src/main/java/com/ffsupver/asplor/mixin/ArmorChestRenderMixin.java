package com.ffsupver.asplor.mixin;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.screen.backpack.BackpackBaseHandler;
import earth.terrarium.adastra.common.items.armor.SpaceSuitItem;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ffsupver.asplor.screen.backpack.BackpackBaseHandler.backpackDataKey;
import static com.ffsupver.asplor.screen.backpack.BackpackBaseHandler.backpackTypeKey;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorChestRenderMixin {
    @Inject(method = "renderArmor", at = @At("HEAD"))
    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity, EquipmentSlot armorSlot, int light, BipedEntityModel<LivingEntity> model, CallbackInfo ci) {
        ItemStack stack = entity.getEquippedStack(armorSlot);
        if (BackpackBaseHandler.isBackpackItem(stack)) {
            matrices.push();
            matrices.scale(0.7f,0.7f,0.2f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
            matrices.translate(-0.5f,-0.8f,stack.getItem() instanceof SpaceSuitItem ? 1.9f :0.9f);
                BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
                blockRenderManager.renderBlockAsEntity(
                        stack.getOrCreateSubNbt(backpackDataKey).getString(backpackTypeKey).equals("small") ?
                                Blocks.CHEST.getDefaultState() : AllBlocks.ALLOY_CHEST.getDefaultState(),
                        matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV
                );
            matrices.pop();
        }
    }
}
