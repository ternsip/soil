package com.ternsip.soil.graph.shader.uniforms;

import com.ternsip.soil.graph.shader.base.Uniform;

import static org.lwjgl.opengl.GL20.glUniform1f;

public class UniformFloat extends Uniform {

    private Float value;

    public void load(float value) {
        if (this.value == null || this.value != value) {
            this.value = value;
            glUniform1f(getLocation(), value);
        }
    }

}
