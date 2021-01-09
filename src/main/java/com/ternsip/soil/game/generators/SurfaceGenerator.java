package com.ternsip.soil.game.generators;

import com.ternsip.soil.common.Maths;
import com.ternsip.soil.game.blocks.BlocksRepository;
import com.ternsip.soil.game.blocks.Material;
import lombok.Getter;

import java.util.Random;

import static com.ternsip.soil.game.blocks.BlocksRepository.SIZE_X;
import static com.ternsip.soil.game.blocks.BlocksRepository.SIZE_Y;

@Getter
public class SurfaceGenerator implements ChunkGenerator {

    private final int height = 35;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void populate(BlocksRepository blocksRepository) {
        Random random = new Random(0);
        for (int x = 0; x < SIZE_X; ++x) {
            for (int y = 0; y < SIZE_Y; ++y) {
                blocksRepository.materials[x][y] = Material.DIRT;
                blocksRepository.rgbas[x][y] = Maths.packRGBA(Math.abs(random.nextInt()) % 256, Math.abs(random.nextInt()) % 256, Math.abs(random.nextInt()) % 256, Math.abs(random.nextInt()) % 256);
            }
        }
    }


}
