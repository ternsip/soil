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
    public static Shader SHADER;

    public final int layer;
    public int index = UNASSIGNED;
    public TextureType textureType;
    public float x1, y1, x2, y2, x3, y3, x4, y4;
    public float period;
    public int metadataI;
    public float metadataF;

    public EntityQuad(
            int layer,
            TextureType textureType,
            float period,
            float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4,
            int metadataI,
            float metadataF
    ) {
        if (layer < 0 || layer > Shader.MAX_LAYERS) {
            throw new IllegalArgumentException("Layer number is out of bound!");
        }
        this.layer = layer;
        this.textureType = textureType;
        this.period = period;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.x4 = x4;
        this.y4 = y4;
        this.metadataI = metadataI;
        this.metadataF = metadataF;
    }

    public void writeToBufferLayout() {
        if (index == EntityQuad.UNASSIGNED) {
            throw new IllegalArgumentException("Quad is not registered yet");
        }
        BufferLayout quadBuffer = SHADER.quadBuffer;
        BufferLayout vertexBuffer = SHADER.vertexBuffer;
        int quadOffset = layer * Shader.QUAD_BUFFER_SIZE + index * Shader.QUAD_BUFFER_CELL_SIZE;
        int vertexOffset = layer * Shader.VERTEX_BUFFER_SIZE + index * Mesh.QUAD_VERTICES * Shader.VERTEX_BUFFER_CELL_SIZE;
        quadBuffer.writeInt(quadOffset, textureType.ordinal());
        quadBuffer.writeFloat(quadOffset + 1, period);
        quadBuffer.writeInt(quadOffset + 2, metadataI);
        quadBuffer.writeFloat(quadOffset + 3, metadataF);
        vertexBuffer.writeFloat(vertexOffset, x1);
        vertexBuffer.writeFloat(vertexOffset + 1, y1);
        vertexBuffer.writeFloat(vertexOffset + 2, x2);
        vertexBuffer.writeFloat(vertexOffset + 3, y2);
        vertexBuffer.writeFloat(vertexOffset + 4, x3);
        vertexBuffer.writeFloat(vertexOffset + 5, y3);
        vertexBuffer.writeFloat(vertexOffset + 6, x4);
        vertexBuffer.writeFloat(vertexOffset + 7, y4);
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
