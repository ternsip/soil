package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.graph.shader.Shader;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2i;

import java.util.*;

import static com.ternsip.soil.graph.display.Quad.QUAD_PINNED_FLAG;
import static com.ternsip.soil.graph.display.Quad.UNASSIGNED;

@Slf4j
// TODO CHECK THIS CLASS ON MATH
public class QuadRepository {

    private final ArrayList<Quad> quads = new ArrayList<>();
    private final ArrayList<Integer> quadOrder = new ArrayList<>();
    private final TreeMap<Integer, Integer> layerToQuadOrderOffset = new TreeMap<>(Collections.reverseOrder());

    public void register(Quad quad) {
        if (quad.index != UNASSIGNED) {
            throw new IllegalArgumentException("Quad is already registered");
        }
        if (quads.size() >= Shader.MAX_QUADS) {
            throw new IllegalArgumentException("Trying to register too much quads");
        }
        int lastIndex = quads.size();
        quads.add(quad);
        quadOrder.add(lastIndex);
        setQuadOrder(lastIndex, lastIndex);
        int orderPointer = lastIndex;
        for (Map.Entry<Integer, Integer> entry : layerToQuadOrderOffset.entrySet()) {
            int layer = entry.getKey();
            int layerStart = entry.getValue();
            if (layer > quad.getLayer()) {
                setQuadOrder(orderPointer, quadOrder.get(layerStart));
                setQuadOrder(layerStart, lastIndex);
                orderPointer = layerStart;
                entry.setValue(layerStart + 1);
            } else {
                break;
            }
        }
        if (!layerToQuadOrderOffset.containsKey(quad.getLayer())) {
            layerToQuadOrderOffset.put(quad.getLayer(), orderPointer);
        }
        quad.index = lastIndex;
        quad.orderIndex = orderPointer;
        writeToQuadBufferLayout(quad);
    }

    public void unregister(Quad quad) {
        if (quad.index == UNASSIGNED) {
            throw new IllegalArgumentException("Quad is not registered yet to be unregistered");
        }
        int lastIndex = quads.size() - 1;
        int initialQuadOrderIndex = quad.orderIndex;
        Quad lastQuad = quads.get(lastIndex);
        setQuadOrder(lastQuad.orderIndex, quad.index);
        lastQuad.index = quad.index;
        quads.set(quad.index, lastQuad);
        writeToQuadBufferLayout(lastQuad);
        int orderValuePointer = quadOrder.get(lastIndex);
        int orderIndexPointer = lastIndex;
        boolean layerDestroyed = false;
        for (Map.Entry<Integer, Integer> entry : layerToQuadOrderOffset.entrySet()) {
            int layer = entry.getKey();
            int layerStart = entry.getValue();
            if (layer > quad.getLayer()) {
                int beforeLayer = layerStart - 1;
                int oldOrderValue = quadOrder.get(beforeLayer);
                setQuadOrder(beforeLayer, orderValuePointer);
                entry.setValue(beforeLayer);
                orderValuePointer = oldOrderValue;
                orderIndexPointer = beforeLayer;
            } else {
                if (orderIndexPointer == layerStart) {
                    layerDestroyed = true;
                } else if (initialQuadOrderIndex != orderIndexPointer) {
                    setQuadOrder(initialQuadOrderIndex, orderValuePointer);
                }
                break;
            }
        }
        if (layerDestroyed) {
            layerToQuadOrderOffset.remove(quad.getLayer());
        }
        quads.remove(lastIndex);
        quadOrder.remove(lastIndex);
        quad.index = UNASSIGNED;
        quad.orderIndex = UNASSIGNED;
    }

    public void setQuadOrder(int quadOrderedIndex, int quadRealIndex) {
        quadOrder.set(quadOrderedIndex, quadRealIndex);
        quads.get(quadRealIndex).orderIndex = quadOrderedIndex;
        Soil.THREADS.client.shader.quadOrderBuffer.writeInt(quadOrderedIndex,quadRealIndex);
    }

    public void writeToQuadBufferLayout(Quad quad) {
        if (quad.index == UNASSIGNED) {
            throw new IllegalArgumentException("Quad is not registered yet");
        }
        Shader shader = Soil.THREADS.client.shader;
        int quadOffset = quad.index * shader.quadBuffer.structureLength;
        shader.quadBuffer.writeInt(quadOffset, quad.textureType.ordinal());
        shader.quadBuffer.writeInt(quadOffset + 1, quad.animationStart);
        shader.quadBuffer.writeFloat(quadOffset + 2, quad.animationPeriod);
        shader.quadBuffer.writeInt(quadOffset + 3, (quad.pinned ? QUAD_PINNED_FLAG : 0));
        shader.quadBuffer.writeInt(quadOffset + 4, quad.metadata1);
        shader.quadBuffer.writeInt(quadOffset + 5, quad.metadata2);
        shader.quadBuffer.writeFloat(quadOffset + 6, quad.x1);
        shader.quadBuffer.writeFloat(quadOffset + 7, quad.y1);
        shader.quadBuffer.writeFloat(quadOffset + 8, quad.x2);
        shader.quadBuffer.writeFloat(quadOffset + 9, quad.y2);
        shader.quadBuffer.writeFloat(quadOffset + 10, quad.x3);
        shader.quadBuffer.writeFloat(quadOffset + 11, quad.y3);
        shader.quadBuffer.writeFloat(quadOffset + 12, quad.x4);
        shader.quadBuffer.writeFloat(quadOffset + 13, quad.y4);
    }

    public int getNumberOfQuads() {
        return quads.size();
    }

}
