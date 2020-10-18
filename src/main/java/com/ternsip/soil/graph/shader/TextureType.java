package com.ternsip.soil.graph.shader;

import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public enum TextureType {

    EMPTY(new File("interface/button.png")),
    BLOCKS(new File("interface/blocks.jpg")),
    FONT(new File("fonts/default.png")),
    BLOCK_WATER(new File("interface/button.png")),
    BLOCK_LAVA(new File("interface/lava.png")),
    SHADOW(new File("interface/blocks.jpg")),
    BLOCK_DIRT(new File("interface/dirt.png")),
    BLOCK_LAWN(new File("interface/lawn.png")),
    PLAYER_IDLE(new File("interface/button.png")),
    PLAYER_ATTACK(new File("interface/player-attack.gif")),
    HOMER(new File("interface/homer.gif")),
    TEST(new File("interface/test.gif")),
    GRASS(new File("interface/grass.gif")),
    KITTY(new File("interface/kitty.gif")),
    BACKGROUND(new File("interface/scrollbar_background.jpg"));


    public final File file;

}
