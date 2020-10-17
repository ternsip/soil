package com.ternsip.soil.universe.generators;

import com.ternsip.soil.universe.Block;
import com.ternsip.soil.universe.BlocksRepository;
import lombok.Getter;

import java.util.Random;

import static com.ternsip.soil.universe.BlocksRepository.SIZE_X;
import static com.ternsip.soil.universe.BlocksRepository.SIZE_Y;

@Getter
public class SurfaceGenerator implements ChunkGenerator {

    private final int height = 9;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void populate(BlocksRepository blocksRepository) {
        Random random = new Random(0);
        for (int x = 0; x < SIZE_X; ++x) {
            for (int y = 0; y < SIZE_Y; ++y) {
                blocksRepository.blocks[x][y] = y > height ? Block.AIR : Block.DIRT;
                if (y == height) {
                    blocksRepository.blocks[x][y] = Block.LAWN;
                }
                if (y == height + 1) {
                    blocksRepository.blocks[x][y] = Block.GRASS;
                }
                if (y < 4 && random.nextDouble() > 0.9) {
                    blocksRepository.blocks[x][y] = Block.LAVA;
                }
            }
        }
    }


}
