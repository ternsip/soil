package com.ternsip.soil.graph.shader;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

@Getter
@Setter
@Slf4j
public abstract class Uniform extends Locatable {

    public void locate(int programID, String name) {
        int location = glGetUniformLocation(programID, name);
        if (location == NOT_LOCATED) {
            log.error("Uniform variable not found - {}, It might be unused or optimised", name);
        } else {
            setLocation(location);
        }
    }

}
