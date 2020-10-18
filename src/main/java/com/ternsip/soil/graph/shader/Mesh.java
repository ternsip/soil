package com.ternsip.soil.graph.shader;

import com.ternsip.soil.common.Finishable;
import com.ternsip.soil.common.Utils;

import java.nio.IntBuffer;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public final class Mesh implements Finishable {

    public static final int QUAD_VERTICES = 4;
    public static final int MAX_VERTICES = 1 << 16;
    public static final int MAX_QUADS = MAX_VERTICES / QUAD_VERTICES;
    public static final int INDICES_ATTRIBUTE_POINTER = 0;

    private final int vao;
    private final int indicesVBO;

    public Mesh() {
        this.vao = glGenVertexArrays();
        glBindVertexArray(vao);
        this.indicesVBO = glGenBuffers();
        IntBuffer indices = Utils.arrayToBuffer(IntStream.range(0, MAX_VERTICES).toArray());
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glBindVertexArray(0);
    }

    public void render(int quads) {
        if (quads <= 0) {
            return;
        }
        glBindVertexArray(vao);
        glEnableVertexAttribArray(INDICES_ATTRIBUTE_POINTER);
        glDrawElements(GL_QUADS, Math.min(quads, MAX_QUADS) * QUAD_VERTICES, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(INDICES_ATTRIBUTE_POINTER);
        glBindVertexArray(0);
    }

    @Override
    public void finish() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(indicesVBO);
    }

}
