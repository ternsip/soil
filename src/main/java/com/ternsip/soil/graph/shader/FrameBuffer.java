package com.ternsip.soil.graph.shader;

import com.ternsip.soil.Soil;
import com.ternsip.soil.graph.display.Texture2D;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

public class FrameBuffer {

    public final int fbo;
    public final Texture2D mainTexture;

    public FrameBuffer(int width, int height) {
        this.fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        this.mainTexture = Soil.THREADS.client.textureRepository.registerTexture2D(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mainTexture.bindId, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
