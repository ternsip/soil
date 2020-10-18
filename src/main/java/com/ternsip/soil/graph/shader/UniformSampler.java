package com.ternsip.soil.graph.shader;

import com.ternsip.soil.graph.shader.Uniform;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class UniformSampler extends Uniform {

    private int value;

    public void load(int value) {
        if (this.value != value) {
            this.value = value;
            glUniform1i(getLocation(), value);
        }
    }

}
