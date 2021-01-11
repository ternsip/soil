package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.graph.shader.Shader;
import com.ternsip.soil.graph.shader.TextureType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

// TODO CHECK THIS CLASS ON MATH
public class Quad {

    public static final int UNASSIGNED = -1;
    public static final int QUAD_PINNED_FLAG = 0x1;

    public TextureType textureType;
    public boolean pinned;
    public float x1, y1, x2, y2, x3, y3, x4, y4;
    public int animationStart = 0;
    public float animationPeriod;
    public int metadata1;
    public int metadata2;

    @Getter
    private int layer;
    @Getter
    private int index = UNASSIGNED;
    @Getter
    private int orderIndex = UNASSIGNED;

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
        ArrayList<Quad> quads = Soil.THREADS.client.quadRepository.quads;
        ArrayList<Integer> quadOrder = Soil.THREADS.client.quadRepository.quadOrder;
        TreeMap<Integer, Integer> layerToQuadOrderOffset = Soil.THREADS.client.quadRepository.layerToQuadOrderOffset;
        if (index != UNASSIGNED) {
            throw new IllegalArgumentException("Quad is already registered");
        }
        if (quads.size() >= Shader.MAX_QUADS) {
            throw new IllegalArgumentException("Trying to register too much quads");
        }
        int lastIndex = quads.size();
        quads.add(this);
        quadOrder.add(lastIndex);
        setQuadOrder(lastIndex, lastIndex);
        int orderPointer = lastIndex;
        for (Map.Entry<Integer, Integer> entry : layerToQuadOrderOffset.entrySet()) {
            int entryLayer = entry.getKey();
            int layerStart = entry.getValue();
            if (entryLayer > layer) {
                setQuadOrder(orderPointer, quadOrder.get(layerStart));
                setQuadOrder(layerStart, lastIndex);
                orderPointer = layerStart;
                entry.setValue(layerStart + 1);
            } else {
                break;
            }
        }
        if (!layerToQuadOrderOffset.containsKey(layer)) {
            layerToQuadOrderOffset.put(layer, orderPointer);
        }
        index = lastIndex;
        orderIndex = orderPointer;
        updateBuffers();
    }

    public void unregister() {
        if (index == UNASSIGNED) {
            throw new IllegalArgumentException("Quad is not registered yet to be unregistered");
        }
        ArrayList<Quad> quads = Soil.THREADS.client.quadRepository.quads;
        ArrayList<Integer> quadOrder = Soil.THREADS.client.quadRepository.quadOrder;
        TreeMap<Integer, Integer> layerToQuadOrderOffset = Soil.THREADS.client.quadRepository.layerToQuadOrderOffset;
        int lastIndex = quads.size() - 1;
        int initialQuadOrderIndex = orderIndex;
        Quad lastQuad = quads.get(lastIndex);
        setQuadOrder(lastQuad.orderIndex, index);
        lastQuad.index = index;
        quads.set(index, lastQuad);
        lastQuad.updateBuffers();
        int orderValuePointer = quadOrder.get(lastIndex);
        int orderIndexPointer = lastIndex;
        boolean layerDestroyed = false;
        for (Map.Entry<Integer, Integer> entry : layerToQuadOrderOffset.entrySet()) {
            int entryLayer = entry.getKey();
            int layerStart = entry.getValue();
            if (entryLayer > layer) {
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
            layerToQuadOrderOffset.remove(layer);
        }
        quads.remove(lastIndex);
        quadOrder.remove(lastIndex);
        index = UNASSIGNED;
        orderIndex = UNASSIGNED;
    }

    public void updateBuffers() {
        if (index == UNASSIGNED) {
            throw new IllegalArgumentException("Quad is not registered yet");
        }
        Shader shader = Soil.THREADS.client.shader;
        int quadOffset = index * shader.quadBuffer.structureLength;
        shader.quadBuffer.writeInt(quadOffset, textureType.ordinal());
        shader.quadBuffer.writeInt(quadOffset + 1, animationStart);
        shader.quadBuffer.writeFloat(quadOffset + 2, animationPeriod);
        shader.quadBuffer.writeInt(quadOffset + 3, (pinned ? QUAD_PINNED_FLAG : 0));
        shader.quadBuffer.writeInt(quadOffset + 4, metadata1);
        shader.quadBuffer.writeInt(quadOffset + 5, metadata2);
        shader.quadBuffer.writeFloat(quadOffset + 6, x1);
        shader.quadBuffer.writeFloat(quadOffset + 7, y1);
        shader.quadBuffer.writeFloat(quadOffset + 8, x2);
        shader.quadBuffer.writeFloat(quadOffset + 9, y2);
        shader.quadBuffer.writeFloat(quadOffset + 10, x3);
        shader.quadBuffer.writeFloat(quadOffset + 11, y3);
        shader.quadBuffer.writeFloat(quadOffset + 12, x4);
        shader.quadBuffer.writeFloat(quadOffset + 13, y4);
    }

    private void setQuadOrder(int quadOrderedIndex, int quadRealIndex) {
        Soil.THREADS.client.quadRepository.quadOrder.set(quadOrderedIndex, quadRealIndex);
        Soil.THREADS.client.quadRepository.quads.get(quadRealIndex).orderIndex = quadOrderedIndex;
        Soil.THREADS.client.shader.quadOrderBuffer.writeInt(quadOrderedIndex, quadRealIndex);
    }

}
