package com.ternsip.soil.graph.shader.uniforms;

import com.ternsip.soil.graph.shader.base.Uniform;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class UniformInteger extends Uniform<Integer> {

    private Integer value;

    @Override
    public void load(Integer value) {
        if (this.value == null || !this.value.equals(value)) {
            this.value = value;
            glUniform1i(getLocation(), value);
        }
    }

}
