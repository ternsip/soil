package com.ternsip.soil.graph.shader.uniforms;

import com.ternsip.soil.graph.shader.base.Uniform;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import static org.lwjgl.opengl.GL20.glUniform4f;

public class UniformVec4 extends Uniform<Vector4fc> {

    private Vector4fc value;

    @Override
    public void load(Vector4fc value) {
        if (this.value == null || !this.value.equals(value)) {
            this.value = new Vector4f(value);
            glUniform4f(getLocation(), value.x(), value.y(), value.z(), value.w());
        }
    }

}
