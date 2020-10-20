package com.ternsip.soil.game.blocks;

import com.ternsip.soil.graph.shader.TextureType;
import lombok.RequiredArgsConstructor;

import static com.ternsip.soil.game.blocks.BlocksRepository.MAX_LIGHT;

@RequiredArgsConstructor
public enum Block {

    AIR(false, 1, TextureType.EMPTY),
    DIRT(true, 3, TextureType.BLOCK_DIRT),
    STONE(true, MAX_LIGHT, TextureType.BLOCK_DIRT),
    LAWN(true, 3, TextureType.BLOCK_LAWN),
    GRASS(false, MAX_LIGHT, TextureType.GRASS),
    WOOD(true, MAX_LIGHT, TextureType.BLOCK_DIRT),
    WATER(false, 2, TextureType.BLOCK_DIRT),
    LAVA(false, -10, TextureType.BLOCK_LAVA),
    SAND(true, MAX_LIGHT, TextureType.BLOCK_DIRT);

    public final boolean obstacle;
    public final int opacity;
    public final TextureType textureType;


}
