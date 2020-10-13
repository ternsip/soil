package com.ternsip.soil.graph.shader.uniforms;

import com.ternsip.soil.graph.shader.base.Uniform;

import static org.lwjgl.opengl.GL20.glUniform1f;

public class UniformBoolean extends Uniform {

    private Boolean value;

    public void load(boolean value) {
        if (this.value == null || this.value != value) {
            this.value = value;
            glUniform1f(getLocation(), value ? 1f : 0f);
        }
    }

}
