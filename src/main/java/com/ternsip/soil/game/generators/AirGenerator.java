package com.ternsip.soil.game.generators;

import com.ternsip.soil.game.blocks.Block;
import com.ternsip.soil.game.blocks.BlocksRepository;
import lombok.Getter;

import static com.ternsip.soil.game.blocks.BlocksRepository.SIZE_X;
import static com.ternsip.soil.game.blocks.BlocksRepository.SIZE_Y;

@Getter
public class AirGenerator implements ChunkGenerator {

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void populate(BlocksRepository blocksRepository) {
        for (int x = 0; x < SIZE_X; ++x) {
            for (int y = 0; y < SIZE_Y; ++y) {
                blocksRepository.blocks[x][y] = Block.AIR;
            }
        }
    }

}
