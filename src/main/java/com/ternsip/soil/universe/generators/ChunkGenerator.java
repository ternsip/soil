package com.ternsip.soil.universe.generators;

import com.ternsip.soil.universe.BlocksRepository;

public interface ChunkGenerator {

    int getPriority();

    void populate(BlocksRepository blocksRepository);

}
