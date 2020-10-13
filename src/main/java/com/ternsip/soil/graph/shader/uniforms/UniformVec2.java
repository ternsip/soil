package com.ternsip.soil.graph.shader.uniforms;

import com.ternsip.soil.graph.shader.base.Uniform;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import static org.lwjgl.opengl.GL20.glUniform2f;


public class UniformVec2 extends Uniform {

    private float x;
    private float y;

    public void load(float x, float y) {
        if (this.x != x || this.y != y) {
            this.x = x;
            this.y = y;
            glUniform2f(getLocation(), this.x, this.y);
        }
    }

}
