package com.ternsip.soil.universe;

import com.ternsip.soil.graph.shader.base.BufferLayout;
import com.ternsip.soil.graph.shader.base.Mesh;
import com.ternsip.soil.graph.shader.base.Shader;
import com.ternsip.soil.graph.shader.base.TextureType;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class EntityQuad {

    public static final LayerQuads LAYER_QUADS = new LayerQuads();
    public static final int UNASSIGNED = -1;
    public static final int QUAD_PINNED_FLAG = 0x1;
    public static Shader SHADER;

    public final int layer;
    public int index = UNASSIGNED;
    public TextureType textureType;
    public boolean pinned;
    public float x1, y1, x2, y2, x3, y3, x4, y4;
    public int animationStart = 0;
    public float animationPeriod;
    public int metadata1;
    public int metadata2;

    public EntityQuad(int layer, TextureType textureType, boolean pinned) {
        this(layer, textureType, pinned, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public EntityQuad(
            int layer,
            TextureType textureType,
            boolean pinned,
            float animationPeriod,
            float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4,
            int metadata1,
            int metadata2
    ) {
        if (layer < 0 || layer > Shader.MAX_LAYERS) {
            throw new IllegalArgumentException("Layer number is out of bound!");
        }
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

    public void writeToBufferLayout() {
        if (index == EntityQuad.UNASSIGNED) {
            throw new IllegalArgumentException("Quad is not registered yet");
        }
        BufferLayout quadBuffer = SHADER.quadBuffer;
        int quadOffset = layer * SHADER.quadBuffer.size / Shader.MAX_LAYERS + index *  SHADER.quadBuffer.structureLength;
        quadBuffer.writeInt(quadOffset, textureType.ordinal());
        quadBuffer.writeInt(quadOffset + 1, animationStart);
        quadBuffer.writeFloat(quadOffset + 2, animationPeriod);
        quadBuffer.writeInt(quadOffset + 3, (pinned ? QUAD_PINNED_FLAG : 0));
        quadBuffer.writeInt(quadOffset + 4, metadata1);
        quadBuffer.writeInt(quadOffset + 5, metadata2);
        quadBuffer.writeFloat(quadOffset + 6, x1);
        quadBuffer.writeFloat(quadOffset + 7, y1);
        quadBuffer.writeFloat(quadOffset + 8, x2);
        quadBuffer.writeFloat(quadOffset + 9, y2);
        quadBuffer.writeFloat(quadOffset + 10, x3);
        quadBuffer.writeFloat(quadOffset + 11, y3);
        quadBuffer.writeFloat(quadOffset + 12, x4);
        quadBuffer.writeFloat(quadOffset + 13, y4);
    }

    public boolean register() {
        Quads quads = LAYER_QUADS.quads[layer];
        ArrayList<EntityQuad> entityQuads = quads.entityQuads;
        if (entityQuads.size() >= Mesh.MAX_QUADS) {
            return false;
        }
        entityQuads.add(this);
        this.index = entityQuads.size() - 1;
        this.writeToBufferLayout();
        quads.count.set(entityQuads.size());
        return true;
    }

    public boolean isRegistered() {
        return index != EntityQuad.UNASSIGNED;
    }

    public void unregister() {
        if (index == EntityQuad.UNASSIGNED) {
            throw new IllegalArgumentException("Quad is not registered yet to be unregistered!");
        }
        Quads quads = LAYER_QUADS.quads[layer];
        ArrayList<EntityQuad> entityQuads = quads.entityQuads;
        int lastIndex = entityQuads.size() - 1;
        if (index < lastIndex) {
            EntityQuad lastQuad = entityQuads.get(lastIndex);
            entityQuads.set(index, lastQuad);
            lastQuad.index = index;
            lastQuad.writeToBufferLayout();
        }
        entityQuads.remove(lastIndex);
        quads.count.set(entityQuads.size());
        index = UNASSIGNED;
    }

    public static int getCountThreadSafe(int layer) {
        return LAYER_QUADS.quads[layer].count.get();
    }

    private static final class LayerQuads {

        private final Quads[] quads = new Quads[Shader.MAX_LAYERS];

        LayerQuads() {
            for (int layer = 0; layer < quads.length; ++layer) {
                quads[layer] = new Quads();
            }
        }

    }

    private static final class Quads {

        private final ArrayList<EntityQuad> entityQuads = new ArrayList<>();
        private final AtomicInteger count = new AtomicInteger(0);

    }

}
