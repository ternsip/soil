package com.ternsip.soil.graph.shader.base;

import com.ternsip.soil.common.logic.Utils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public final class Mesh {

    public static final int QUAD_VERTICES = 4;
    public static final int MAX_VERTICES = 1 << 16;
    public static final int MAX_QUADS = MAX_VERTICES / QUAD_VERTICES;
    public static final AttributeData INDICES = new AttributeData(0, "index", 1);
    public static final AttributeData VERTICES = new AttributeData(1, "position", 2);

    public final FloatBuffer vertices;
    private final int vao;
    private final int indicesVBO;
    private final int verticesVBO;

    private int activeQuads;

    public Mesh() {
        this.vertices = Utils.arrayToBuffer(new float[MAX_VERTICES * VERTICES.getNumberPerVertex()]);
        this.activeQuads = 0;
        this.vao = glGenVertexArrays();
        this.indicesVBO = glGenBuffers();
        this.verticesVBO = glGenBuffers();
        updateIndexBuffer(Utils.arrayToBuffer(IntStream.range(0, MAX_VERTICES * INDICES.getNumberPerVertex()).toArray()));
        updateVertexBuffer();
    }

    public void updateVertexBuffer() {
        glBindVertexArray(vao);
        vertices.rewind();
        glBindBuffer(GL_ARRAY_BUFFER, verticesVBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STREAM_DRAW);
        glVertexAttribPointer(VERTICES.getIndex(), VERTICES.getNumberPerVertex(), GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render() {
        if (activeQuads == 0) {
            return;
        }
        glBindVertexArray(vao);
        glEnableVertexAttribArray(INDICES.getIndex());
        glEnableVertexAttribArray(VERTICES.getIndex());
        glDrawArrays(GL_QUADS, 0, activeQuads * QUAD_VERTICES);
        glDisableVertexAttribArray(INDICES.getIndex());
        glDisableVertexAttribArray(VERTICES.getIndex());
        glBindVertexArray(0);
    }

    public void finish() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(indicesVBO);
        glDeleteBuffers(verticesVBO);
    }

    private void updateIndexBuffer(IntBuffer indices) {
        glBindVertexArray(vao);
        indices.rewind();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glBindVertexArray(0);
    }

}
