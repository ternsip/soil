package com.ternsip.soil.graph.shader.base;

import com.ternsip.soil.graph.display.Texture;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public enum TextureType {

    EMPTY(new File("interface/button.png")),
    BLOCK_WATER(new File("interface/button.png")),
    BLOCK_DIRT(new File("interface/button.png")),
    PLAYER_IDLE(new File("interface/button.png"));

    public final File file;

}
