package com.ternsip.soil.graph.shader.uniforms;

import com.ternsip.soil.graph.shader.base.Uniform;
import org.joml.Matrix4fc;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class UniformMatrix4 extends Uniform<Matrix4fc> {

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    @Override
    public void load(Matrix4fc value) {
        value.get(matrixBuffer);
        glUniformMatrix4fv(getLocation(), false, matrixBuffer);
    }

}
