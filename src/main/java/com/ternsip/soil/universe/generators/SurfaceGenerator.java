package com.ternsip.soil.universe.generators;

import com.ternsip.soil.universe.Block;
import com.ternsip.soil.universe.BlocksRepository;
import lombok.Getter;

@Getter
public class SurfaceGenerator implements ChunkGenerator {

    private final int height = 30;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void populate(BlocksRepository blocksRepository) {
        for (int x = 0; x < blocksRepository.sizeX; ++x) {
            for (int y = 0; y < blocksRepository.sizeY; ++y) {
                blocksRepository.blocks[x][y] = y > height ? Block.AIR : Block.DIRT;
                if (y == height) {
                    blocksRepository.blocks[x][y] = Block.LAWN;
                }
                if (y == height + 1) {
                    blocksRepository.blocks[x][y] = Block.GRASS;
                }
            }
        }
    }


}
