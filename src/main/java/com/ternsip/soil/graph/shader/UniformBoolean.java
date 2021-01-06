package com.ternsip.soil.graph.shader;

import static org.lwjgl.opengl.GL20.glUniform1f;

public class UniformBoolean extends Uniform {

    private boolean value = false;

    public void load(boolean value) {
        if (this.value != value) {
            this.value = value;
            glUniform1f(getLocation(), value ? 1f : 0f);
        }
    }

}
