package com.ternsip.soil.graph.shader.uniforms;

import com.ternsip.soil.graph.shader.base.Uniform;

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
