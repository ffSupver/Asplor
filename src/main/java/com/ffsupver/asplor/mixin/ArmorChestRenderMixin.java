package com.ffsupver.asplor.mixin;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.screen.backpack.BackpackBaseHandler;
import com.ffsupver.asplor.util.GoggleDisplays;
import com.ffsupver.asplor.util.RenderUtil;
import com.simibubi.create.AllPartialModels;
import earth.terrarium.adastra.common.items.armor.SpaceSuitItem;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ffsupver.asplor.screen.backpack.BackpackBaseHandler.backpackDataKey;
import static com.ffsupver.asplor.screen.backpack.BackpackBaseHandler.backpackTypeKey;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorChestRenderMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {


    public ArmorChestRenderMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "renderArmor", at = @At("HEAD"))
    private void renderArmorMixin(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity, EquipmentSlot armorSlot, int light, BipedEntityModel<T> model, CallbackInfo ci) {
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
        if (GoggleDisplays.isGoggle(stack)){
            BipedEntityModel<T> nM = this.getContextModel();
            this.getContextModel().copyBipedStateTo(model);

            boolean isSneaking = entity instanceof PlayerEntity player && player.isSneaking();

            matrices.push();
            if (isSneaking){
                matrices.translate(0,1/4f,0);
            }
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) Math.toDegrees(nM.getHead().yaw)));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch()));
            matrices.translate(.5f,3/32f,-.5f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

            RenderUtil.renderModel(
                    matrices,
                    vertexConsumers,
                    AllPartialModels.GOGGLES,
                    light
            );
            matrices.pop();


        }
    }

}
