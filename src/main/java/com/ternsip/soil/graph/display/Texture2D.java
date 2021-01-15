package com.ternsip.soil.graph.display;

import lombok.RequiredArgsConstructor;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11C.glDeleteTextures;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

@RequiredArgsConstructor
public class Texture2D {

    public final int width;
    public final int height;
    public final int bindId;
    public final int activationId;

    public void bind() {
        glActiveTexture(GL_TEXTURE0 + activationId);
        glBindTexture(GL_TEXTURE_2D_ARRAY, bindId);
        glActiveTexture(GL_TEXTURE0);
    }

    public void unbind() {
        glActiveTexture(GL_TEXTURE0 + activationId);
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        glActiveTexture(GL_TEXTURE0);
    }

    public void delete() {
        glDeleteTextures(bindId);
    }

}
