package com.ternsip.soil.graph.shader;


import com.ternsip.soil.Soil;

import static com.ternsip.soil.graph.shader.Shader.UNASSIGNED_INDEX;

public class Light {

    public float x;
    public float y;
    public float radius;
    int index = UNASSIGNED_INDEX;

    public Light(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public boolean isRegistered() {
        return index != UNASSIGNED_INDEX;
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
