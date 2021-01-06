package com.ternsip.soil.game.blocks;

import com.ternsip.soil.graph.shader.TextureType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Material {

    AIR(TextureType.EMPTY),
    DIRT(TextureType.BLOCK_DIRT),
    STONE(TextureType.BLOCK_DIRT),
    LAWN(TextureType.BLOCK_LAWN),
    GRASS(TextureType.GRASS),
    WOOD(TextureType.BLOCK_DIRT),
    WATER(TextureType.BLOCK_DIRT),
    LAVA(TextureType.BLOCK_LAVA),
    SAND(TextureType.BLOCK_SAND);

    public final TextureType textureType;


}
