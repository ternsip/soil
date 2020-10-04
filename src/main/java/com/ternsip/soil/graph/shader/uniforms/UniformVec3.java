package com.ternsip.soil.graph.shader.uniforms;

import com.ternsip.soil.graph.shader.base.Uniform;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import static org.lwjgl.opengl.GL20.glUniform3f;

public class UniformVec3 extends Uniform<Vector3fc> {

    private Vector3fc value;

    public void load(Vector3fc value) {
        if (this.value == null || !this.value.equals(value)) {
            this.value = new Vector3f(value);
            glUniform3f(getLocation(), value.x(), value.y(), value.z());
        }
    }

}
