package com.ternsip.soil.graph.shader;

import lombok.RequiredArgsConstructor;

import java.io.File;

import static com.ternsip.soil.graph.shader.TextureStyle.*;

@RequiredArgsConstructor
public enum TextureType {

    EMPTY(new File("interface/button.png"), TEXTURE_STYLE_EMPTY),
    BLOCKS(new File("interface/blocks.jpg"), TEXTURE_STYLE_BLOCKS),
    FONT(new File("fonts/default.png"), TEXTURE_STYLE_FONT256),
    BLOCK_WATER(new File("interface/button.png"), TEXTURE_STYLE_WATER),
    BLOCK_LAVA(new File("interface/lava.png"), TEXTURE_STYLE_LAVA),
    SHADOW(new File("interface/blocks.jpg"), TEXTURE_STYLE_SHADOW),
    BLOCK_DIRT(new File("interface/dirt.png"), TEXTURE_STYLE_NORMAL),
    BLOCK_SAND(new File("interface/sand_4_adjacent8.png"), TEXTURE_STYLE_4_ADJACENT8_VARIATION),
    BLOCK_LAWN(new File("interface/lawn.png"), TEXTURE_STYLE_NORMAL),
    PLAYER_IDLE(new File("interface/button.png"), TEXTURE_STYLE_NORMAL),
    PLAYER_ATTACK(new File("interface/player-attack.gif"), TEXTURE_STYLE_NORMAL),
    HOMER(new File("interface/homer.gif"), TEXTURE_STYLE_NORMAL),
    TEST(new File("interface/test.gif"), TEXTURE_STYLE_NORMAL),
    GRASS(new File("interface/grass.gif"), TEXTURE_STYLE_NORMAL),
    KITTY(new File("interface/kitty.gif"), TEXTURE_STYLE_NORMAL),
    BACKGROUND(new File("interface/scrollbar_background.jpg"), TEXTURE_STYLE_NORMAL);

    public final File file;
    public final TextureStyle textureStyle;

}
