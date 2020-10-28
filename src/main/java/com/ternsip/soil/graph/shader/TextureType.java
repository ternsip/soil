package com.ternsip.soil.graph.shader;

import lombok.RequiredArgsConstructor;

import java.io.File;

import static com.ternsip.soil.graph.shader.TextureStyle.*;

@RequiredArgsConstructor
public enum TextureType {

    EMPTY(new File("soil/interface/button.png"), TEXTURE_STYLE_EMPTY),
    BLOCKS(new File("soil/interface/blocks.jpg"), TEXTURE_STYLE_BLOCKS),
    FONT(new File("soil/fonts/default.png"), TEXTURE_STYLE_FONT256),
    BLOCK_WATER(new File("soil/interface/button.png"), TEXTURE_STYLE_WATER),
    BLOCK_LAVA(new File("soil/interface/lava.png"), TEXTURE_STYLE_LAVA),
    SHADOW(new File("soil/interface/blocks.jpg"), TEXTURE_STYLE_SHADOW),
    BLOCK_DIRT(new File("soil/interface/dirt.png"), TEXTURE_STYLE_NORMAL),
    BLOCK_SAND(new File("soil/interface/sand_4_adjacent8.png"), TEXTURE_STYLE_4_ADJACENT8_VARIATION),
    BLOCK_LAWN(new File("soil/interface/lawn.png"), TEXTURE_STYLE_NORMAL),
    PLAYER_IDLE(new File("soil/interface/player_idle.gif"), TEXTURE_STYLE_NORMAL),
    PLAYER_ATTACK(new File("soil/interface/player-attack.gif"), TEXTURE_STYLE_NORMAL),
    HOMER(new File("soil/interface/homer.gif"), TEXTURE_STYLE_NORMAL),
    TEST(new File("soil/interface/test.gif"), TEXTURE_STYLE_NORMAL),
    GRASS(new File("soil/interface/grass.gif"), TEXTURE_STYLE_NORMAL),
    KITTY(new File("soil/interface/kitty.gif"), TEXTURE_STYLE_NORMAL),
    BACKGROUND(new File("soil/interface/scrollbar_background.jpg"), TEXTURE_STYLE_NORMAL);

    public final File file;
    public final TextureStyle textureStyle;

}
