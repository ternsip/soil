package com.ternsip.soil.graph.shader;

import com.ternsip.soil.graph.shader.Uniform;

import static org.lwjgl.opengl.GL20.glUniform1f;

public class UniformFloat extends Uniform {

    private float value;

    public void load(float value) {
        if (this.value != value) {
            this.value = value;
            glUniform1f(getLocation(), value);
        }
    }

}
