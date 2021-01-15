package com.ternsip.soil.graph.shader;

import com.ternsip.soil.common.Finishable;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL43C.*;
import static org.lwjgl.opengl.GL44C.*;


public class BufferLayoutServer extends Locatable implements Finishable {

    // TODO this buffer can be byte-sized
    private static final int FOUR_BYTES = 4;
    private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private static final int BUFFER_FLAGS = 0;
    private final int ssbo;

    public BufferLayoutServer(int size) {
        int padSize = size + (4 - size % 4) % 4;
        int bytesSize = padSize * FOUR_BYTES;
        this.ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        ByteBuffer allocatedBuffer = ByteBuffer.allocateDirect(bytesSize).order(BYTE_ORDER);
        glBufferStorage(GL_SHADER_STORAGE_BUFFER, allocatedBuffer, BUFFER_FLAGS);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    @Override
    public void locate(int programID, String name) {
        locateSSBO(programID, name, ssbo);
    }

    @Override
    public void finish() {
        glDeleteBuffers(ssbo);
    }

}
