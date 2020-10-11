package com.ternsip.soil.graph.shader.base;

import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public enum TextureType {

    EMPTY(new File("interface/button.png")),
    BLOCK_WATER(new File("interface/button.png")),
    BLOCK_DIRT(new File("interface/button.png")),
    PLAYER_IDLE(new File("interface/button.png")),
    PLAYER_ATTACK(new File("interface/player-attack.gif")),
    HOMER(new File("interface/homer.gif")),
    TEST(new File("interface/test.gif")),
    BACKGROUND(new File("interface/scrollbar_background.jpg"));


    public final File file;

}
