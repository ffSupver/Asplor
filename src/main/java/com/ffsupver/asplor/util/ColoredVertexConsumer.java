package com.ffsupver.asplor.util;

import net.minecraft.client.render.VertexConsumer;

public class ColoredVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final float red, green, blue, alpha;

    public ColoredVertexConsumer(VertexConsumer delegate, float red, float green, float blue, float alpha) {
        this.delegate = delegate;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return delegate.color((int)(this.red * 255), (int)(this.green * 255), (int)(this.blue * 255), (int)(this.alpha * 255));
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        return delegate.vertex(x, y, z);
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        return delegate.texture(u, v);
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        return delegate.overlay(u, v);
    }

    @Override
    public VertexConsumer light(int u, int v) {
        return delegate.light(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return delegate.normal(x, y, z);
    }

    @Override
    public void next() {
        delegate.next();
    }

    @Override
    public void fixedColor(int red, int green, int blue, int alpha) {
        delegate.fixedColor(red,green,blue,alpha);
    }

    @Override
    public void unfixColor() {
        delegate.unfixColor();
    }
}