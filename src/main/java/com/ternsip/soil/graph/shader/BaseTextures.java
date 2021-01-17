package com.ternsip.soil.graph.shader;

import com.ternsip.soil.Soil;
import com.ternsip.soil.graph.display.Texture;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public enum BaseTextures {

    EMPTY(new File("soil/interface/button.png")),
    FONT(new File("soil/fonts/default.png")),
    SHADOW(new File("soil/interface/blocks.jpg")),
    PLAYER_IDLE(new File("soil/interface/player_idle.gif")),
    PLAYER_ATTACK(new File("soil/interface/player-attack.gif")),
    HOMER(new File("soil/interface/homer.gif")),
    TEST(new File("soil/interface/test.gif")),
    KITTY(new File("soil/interface/kitty.gif")),
    OVERLAY(new File("soil/interface/checkbox_background.png")),
    BACKGROUND(new File("soil/interface/scrollbar_background.jpg"));

    public final File file;
    public Texture texture;

    public Texture getTexture() {
        if (texture == null) {
            texture = Soil.THREADS.client.textureRepository.getTexture(file);
        }
        return texture;
    }

}
