package com.ternsip.soil.game.blocks;

import com.ternsip.soil.graph.shader.TextureType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Block {

    AIR(false, 0, 1, TextureType.EMPTY),
    DIRT(true, 0, 3, TextureType.BLOCK_DIRT),
    STONE(true, 0, 3, TextureType.BLOCK_DIRT),
    LAWN(true, 0, 3, TextureType.BLOCK_LAWN),
    GRASS(false, 0, 2, TextureType.GRASS),
    WOOD(true, 0, 3, TextureType.BLOCK_DIRT),
    WATER(false, 0, 2, TextureType.BLOCK_DIRT),
    LAVA(false, 10, 2, TextureType.BLOCK_LAVA),
    SAND(true, 0, 3, TextureType.BLOCK_DIRT);

    public final boolean obstacle;
    public final int emitLight;
    public final int lightOpacity;
    public final TextureType textureType;


}
