package com.ternsip.soil.universe.generators;

import com.ternsip.soil.universe.BlocksRepository;

public class WaterGenerator implements ChunkGenerator {

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void populate(BlocksRepository blocksRepository) {

    }
}
