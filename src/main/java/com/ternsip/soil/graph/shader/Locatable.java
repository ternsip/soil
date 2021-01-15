package com.ternsip.soil.graph.shader;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30C.glBindBufferBase;
import static org.lwjgl.opengl.GL43C.*;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;

@Setter
@Getter
@Slf4j
public abstract class Locatable {

    public static final int NOT_LOCATED = -1;

    private int location = NOT_LOCATED;

    public abstract void locate(int programID, String name);

    public void locateSSBO(int programID, String name, int ssbo) {
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

}
