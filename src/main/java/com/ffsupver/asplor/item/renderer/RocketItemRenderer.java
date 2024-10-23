package com.ffsupver.asplor.item.renderer;

import earth.terrarium.adastra.client.renderers.entities.vehicles.RocketRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class RocketItemRenderer  implements BuiltinItemRenderer {
    private RocketRenderer.ItemRenderer itemRenderer;
    public RocketItemRenderer(EntityModelLayer layer, Identifier texture) {
        this.itemRenderer = new RocketRenderer.ItemRenderer(layer,texture);
    }

    @Override
    public void render(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        this.itemRenderer.render(stack, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND,matrices,vertexConsumers,light,overlay);
    }
}
