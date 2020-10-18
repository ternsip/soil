package com.ternsip.soil.game.generators;

import com.ternsip.soil.game.blocks.BlocksRepository;

public class WaterGenerator implements ChunkGenerator {

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void populate(BlocksRepository blocksRepository) {

    }
}
