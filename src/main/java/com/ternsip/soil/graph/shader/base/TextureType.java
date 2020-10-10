package com.ternsip.soil.graph.shader.base;

import com.ternsip.soil.graph.display.Texture;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public enum TextureType {

    EMPTY(new File("interface/browse_overlay.png")),
    BLOCK_WATER(new File("interface/browse_overlay.png")),
    BLOCK_DIRT(new File("interface/browse_overlay.png")),
    PLAYER_IDLE(new File("interface/browse_overlay.png"));

    public final File file;

}
