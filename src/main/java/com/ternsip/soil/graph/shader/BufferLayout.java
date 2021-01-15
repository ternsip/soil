package com.ternsip.soil.graph.shader;

import com.ternsip.soil.common.Finishable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL43C.*;
import static org.lwjgl.opengl.GL44C.*;


/*
 * NOTE: You might need to write vec4 here, because SSBOs have specific
 * alignment requirements for struct members (vec3 is always treated
 * as vec4 in memory!) Or you might need special 4x alignment in size (not sure)
 * "https://www.safaribooksonline.com/library/view/opengl-programming-guide/9780132748445/app09lev1sec3.html"
 * TODO OR it might be vec3 depending on shader version
 * USEFUL LINKS:
 * https://www.bfilipek.com/2015/01/persistent-mapped-buffers-in-opengl.html
 * http://voidptr.io/blog/2016/04/28/ldEngine-Part-1.html
 */
public class BufferLayout extends Locatable implements Finishable {

    private static final int FOUR_BYTES = 4;
    private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private static final int BUFFER_FLAGS = GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT;
    public final int size;
    public final int maxStructures;
    public final int structureLength;
    private final int ssbo;
    private final ByteBuffer data;

    public BufferLayout(int maxStructures, int structureLength) {
        this.maxStructures = maxStructures;
        this.structureLength = structureLength;
        this.size = maxStructures * structureLength;
        int padSize = size + (4 - size % 4) % 4;
        int bytesSize = padSize * FOUR_BYTES;
        this.ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        ByteBuffer allocatedBuffer = ByteBuffer.allocateDirect(bytesSize).order(BYTE_ORDER);
        glBufferStorage(GL_SHADER_STORAGE_BUFFER, allocatedBuffer, BUFFER_FLAGS);
        this.data = glMapBuffer(GL_SHADER_STORAGE_BUFFER, GL_WRITE_ONLY).order(BYTE_ORDER);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    @Override
    public void locate(int programID, String name) {
        locateSSBO(programID, name, ssbo);
    }

    public int readInt(int index) {
        return data.getInt(index * FOUR_BYTES);
    }

    public float readFloat(int index) {
        return data.getFloat(index * FOUR_BYTES);
    }

    public void writeInt(int index, int value) {
        data.putInt(index * FOUR_BYTES, value);
    }

    public void writeFloat(int index, float value) {
        data.putFloat(index * FOUR_BYTES, value);
    }

    @Override
    public void finish() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        glDeleteBuffers(ssbo);
    }

}
