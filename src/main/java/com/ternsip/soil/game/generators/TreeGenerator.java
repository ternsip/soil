package com.ternsip.soil.game.generators;

import com.ternsip.soil.game.blocks.BlocksRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TreeGenerator implements ChunkGenerator {

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public void populate(BlocksRepository blocksRepository) {

    }

}
