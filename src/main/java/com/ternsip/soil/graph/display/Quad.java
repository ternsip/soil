package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.graph.shader.TextureType;
import lombok.Getter;

public class Quad {

    public static final int UNASSIGNED = -1;
    public static final int QUAD_PINNED_FLAG = 0x1;

    @Getter
    private int layer;
    public int index = UNASSIGNED;
    public int orderIndex = UNASSIGNED;
    public TextureType textureType;
    public boolean pinned;
    public float x1, y1, x2, y2, x3, y3, x4, y4;
    public int animationStart = 0;
    public float animationPeriod;
    public int metadata1;
    public int metadata2;

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
        return index != UNASSIGNED;
    }

    public void setLayer(int newLayer) {
        if (isRegistered()) {
            unregister();
            layer = newLayer;
            register();
        }
    }

    public void register() {
        Soil.THREADS.client.quadRepository.register(this);
    }

    public void unregister() {
        Soil.THREADS.client.quadRepository.unregister(this);
    }

    public void updateBuffers() {
        Soil.THREADS.client.quadRepository.writeToQuadBufferLayout(this);
    }

}
