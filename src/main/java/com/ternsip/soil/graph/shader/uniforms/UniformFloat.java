package com.ternsip.soil.graph.shader.uniforms;

import com.ternsip.soil.graph.shader.base.Uniform;

import static org.lwjgl.opengl.GL20.glUniform1f;

public class UniformFloat extends Uniform<Float> {

    private Float value;

    public void load(Float value) {
        if (this.value == null || !this.value.equals(value)) {
            this.value = value;
            glUniform1f(getLocation(), value);
        }
    }

}
