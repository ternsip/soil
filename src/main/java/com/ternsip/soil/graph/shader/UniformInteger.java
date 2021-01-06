package com.ternsip.soil.graph.shader;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class UniformInteger extends Uniform {

    private int value = 0;

    public void load(int value) {
        if (this.value != value) {
            this.value = value;
            glUniform1i(getLocation(), value);
        }
    }

}
