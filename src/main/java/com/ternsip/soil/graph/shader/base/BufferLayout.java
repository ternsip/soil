package com.ternsip.soil.graph.shader.base;

import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL15C.glGetBufferSubData;
import static org.lwjgl.opengl.GL43.glBufferData;
import static org.lwjgl.opengl.GL43.glDeleteBuffers;
import static org.lwjgl.opengl.GL43.*;

/*
 * NOTE: You might need to write vec4 here, because SSBOs have specific
 * alignment requirements for struct members (vec3 is always treated
 * as vec4 in memory!) Or you might need special 4x alignment in size (not sure)
 * "https://www.safaribooksonline.com/library/view/opengl-programming-guide/9780132748445/app09lev1sec3.html"
 */
@Slf4j
public class BufferLayout extends Locatable {

    private static final int FOUR_BYTES = 4;
    private static final int VEC4_BYTES = FOUR_BYTES * 4;
    private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    private final int ssbo;
    private ByteBuffer data;

    public BufferLayout(int size) {
        if (size % VEC4_BYTES != 0) {
            throw new IllegalArgumentException("SSBO should be always multiple of 16 (vec4 in memory)");
        }
        this.ssbo = glGenBuffers();
        this.data = ByteBuffer.allocateDirect(size * FOUR_BYTES);
        this.data.order(BYTE_ORDER);
        allocateBuffer();
    }

    public void locate(int programID, String name) {
        int boxesResourceIndex = glGetProgramResourceIndex(programID, GL_SHADER_STORAGE_BLOCK, name);
        if (boxesResourceIndex == NOT_LOCATED) {
            log.debug("Buffer layout not found - {}", name);
        } else {
            IntBuffer props = BufferUtils.createIntBuffer(1);
            IntBuffer params = BufferUtils.createIntBuffer(1);
            props.put(0, GL_BUFFER_BINDING);
            glGetProgramResourceiv(programID, GL_SHADER_STORAGE_BLOCK, boxesResourceIndex, props, null, params);
            int location = params.get(0);
            setLocation(location);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, getLocation(), ssbo);
        }
    }

    public void updateSubBuffer(int offset, int size) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, FOUR_BYTES * offset, sliceData(offset, size));
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void read(int offset, int size) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glGetBufferSubData(GL_SHADER_STORAGE_BUFFER, FOUR_BYTES * offset, sliceData(offset, size));
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
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

    public void finish() {
        glDeleteBuffers(ssbo);
    }

    public void fill(int offset, int size, int value) {
        int end = offset + size;
        for (int i = offset; i < end; ++i) {
            writeInt(i, value); // TODO use array fill
        }
    }

    public void fill(int offset, int size, float value) {
        int end = offset + size;
        for (int i = offset; i < end; ++i) {
            writeFloat(i, value); // TODO use array fill
        }
    }

    private void allocateBuffer() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    private ByteBuffer sliceData(int offset, int size) {
        ByteBuffer byteBuffer = data.slice();
        byteBuffer.order(BYTE_ORDER);
        byteBuffer.position(offset * FOUR_BYTES);
        byteBuffer.limit(size * FOUR_BYTES);
        return byteBuffer;
    }

}
