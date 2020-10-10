package com.ternsip.soil.graph.shader.base;

import com.ternsip.soil.common.logic.Finishable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL43C.*;


/*
 * NOTE: You might need to write vec4 here, because SSBOs have specific
 * alignment requirements for struct members (vec3 is always treated
 * as vec4 in memory!) Or you might need special 4x alignment in size (not sure)
 * "https://www.safaribooksonline.com/library/view/opengl-programming-guide/9780132748445/app09lev1sec3.html"
 */
@Slf4j
public class BufferLayout extends Locatable implements Finishable {

    // TODO think about heap buffer instead of direct
    // TODO http://voidptr.io/blog/2016/04/28/ldEngine-Part-1.html
    private static final int FOUR_BYTES = 4;
    private static final int VEC4_BYTES = FOUR_BYTES * 4;
    private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    private final int ssbo;
    private final ByteBuffer data;

    public BufferLayout(int size) {
        int bytesSize = size * FOUR_BYTES;
        if (bytesSize % VEC4_BYTES != 0) {
            throw new IllegalArgumentException("SSBO should be always multiple of 16 (vec4 in memory)"); // TODO mb vec3 in memory? depends
        }
        this.ssbo = glGenBuffers();
        this.data = ByteBuffer.allocateDirect(bytesSize);
        this.data.order(BYTE_ORDER);
        allocateBuffer();
    }

    @Override
    public void locate(int programID, String name) {
        int boxesResourceIndex = glGetProgramResourceIndex(programID, GL_SHADER_STORAGE_BLOCK, name);
        if (boxesResourceIndex == NOT_LOCATED) {
            log.error("Buffer layout not found - {}", name);
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

    void writeToGpu(int offset, int size) {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, FOUR_BYTES * offset, sliceData(offset, size));
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    void readFromGpu(int offset, int size) {
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

    @Override
    public void finish() {
        glDeleteBuffers(ssbo);
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
