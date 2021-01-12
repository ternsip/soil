package com.ternsip.soil.graph.shader;

import com.ternsip.soil.Soil;

import static com.ternsip.soil.graph.shader.Shader.UNASSIGNED_INDEX;

public class Quad {

    public TextureType textureType;
    public boolean pinned;
    public float x1, y1, x2, y2, x3, y3, x4, y4;
    public int animationStart = 0;
    public float animationPeriod;
    public int metadata1;
    public int metadata2;
    int layer;
    int index = UNASSIGNED_INDEX;
    int orderIndex = UNASSIGNED_INDEX;

    public Quad(int layer, TextureType textureType, boolean pinned) {
        this(layer, textureType, pinned, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public Quad(
            int layer,
            TextureType textureType,
            boolean pinned,
            float animationPeriod,
            float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4,
            int metadata1,
            int metadata2
    ) {
        this.layer = layer;
        this.textureType = textureType;
        this.pinned = pinned;
        this.animationPeriod = animationPeriod;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.x4 = x4;
        this.y4 = y4;
        this.metadata1 = metadata1;
        this.metadata2 = metadata2;
    }

    public boolean isRegistered() {
        return index != UNASSIGNED_INDEX;
    }

    public void setLayer(int newLayer) {
        if (isRegistered()) {
            unregister();
            layer = newLayer;
            register();
        }
    }

    public void register() {
        Soil.THREADS.client.shader.register(this);
    }

    public void unregister() {
        Soil.THREADS.client.shader.unregister(this);
    }

    public void updateBuffers() {
        Soil.THREADS.client.shader.updateBuffers(this);
    }

}
