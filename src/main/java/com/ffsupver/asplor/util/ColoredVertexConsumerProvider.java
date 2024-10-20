package com.ffsupver.asplor.util;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

public class ColoredVertexConsumerProvider implements VertexConsumerProvider {
    private final VertexConsumerProvider delegate;
    private final float red, green, blue, alpha;

    public ColoredVertexConsumerProvider(VertexConsumerProvider delegate, float red, float green, float blue, float alpha) {
        this.delegate = delegate;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    @Override
    public VertexConsumer getBuffer(RenderLayer renderLayer) {
        return new ColoredVertexConsumer(delegate.getBuffer(renderLayer), red, green, blue, alpha);
    }
}