package com.ternsip.soil.graph.shader;

import lombok.RequiredArgsConstructor;

import java.io.File;

import static com.ternsip.soil.graph.shader.TextureStyle.*;

@RequiredArgsConstructor
public enum TextureType {

    EMPTY(new File("soil/interface/button.png"), TEXTURE_STYLE_EMPTY),
    FONT(new File("soil/fonts/default.png"), TEXTURE_STYLE_FONT256),
    SHADOW(new File("soil/interface/blocks.jpg"), TEXTURE_STYLE_SHADOW),
    PLAYER_IDLE(new File("soil/interface/player_idle.gif"), TEXTURE_STYLE_NORMAL),
    PLAYER_ATTACK(new File("soil/interface/player-attack.gif"), TEXTURE_STYLE_NORMAL),
    HOMER(new File("soil/interface/homer.gif"), TEXTURE_STYLE_NORMAL),
    TEST(new File("soil/interface/test.gif"), TEXTURE_STYLE_NORMAL),
    KITTY(new File("soil/interface/kitty.gif"), TEXTURE_STYLE_NORMAL),
    OVERLAY(new File("soil/interface/checkbox_background.png"), TEXTURE_STYLE_NORMAL),
    SOIL(new File("soil/interface/soil.png"), TEXTURE_STYLE_NORMAL),
    BACKGROUND(new File("soil/interface/scrollbar_background.jpg"), TEXTURE_STYLE_NORMAL);

    public final File file;
    public final TextureStyle textureStyle;

}
