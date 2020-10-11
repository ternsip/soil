package com.ternsip.soil.universe.generators;

import com.ternsip.soil.universe.Block;
import com.ternsip.soil.universe.BlocksRepository;
import lombok.Getter;

@Getter
public class AirGenerator implements ChunkGenerator {

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void populate(BlocksRepository blocksRepository) {
        for (int x = 0; x < blocksRepository.sizeX; ++x) {
            for (int y = 0; y < blocksRepository.sizeY; ++y) {
                blocksRepository.blocks[x][y] = Block.AIR;
            }
        }
    }

}
