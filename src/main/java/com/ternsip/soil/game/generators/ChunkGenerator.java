package com.ternsip.soil.game.generators;

import com.ternsip.soil.game.blocks.BlocksRepository;

public interface ChunkGenerator {

    int getPriority();

    void populate(BlocksRepository blocksRepository);

}
