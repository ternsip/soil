package com.ternsip.soil.universe;

import com.ternsip.soil.common.logic.Finishable;
import com.ternsip.soil.common.logic.Indexer;
import com.ternsip.soil.common.logic.Utils;
import com.ternsip.soil.graph.shader.base.Shader;
import com.ternsip.soil.universe.generators.ChunkGenerator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BlocksRepository implements Finishable {

    private static final List<ChunkGenerator> CHUNK_GENERATORS = constructChunkGenerators();
    public static final int SIZE_X = 4000;
    public static final int SIZE_Y = 3000;
    public static Shader SHADER;

    public static final Indexer INDEXER = new Indexer(SIZE_X, SIZE_Y);
    public final Block[][] blocks = new Block[SIZE_X][SIZE_Y];

    public void init() {
        for (ChunkGenerator chunkGenerator : CHUNK_GENERATORS) {
            chunkGenerator.populate(this);
        }
    }

    public void updateBlocks(int startX, int startY, Block[][] blocks) {
        int endX = startX + blocks.length - 1;
        int endY = startX + blocks[0].length - 1;
        for (int x = startX; x <= endX; ++x) {
            for (int y = startY; y <= endY; ++y) {
                this.blocks[x][y] = blocks[x][y];
            }
        }
    }

    public void visualUpdate(int startX, int startY, int endX, int endY) {
        for (int x = startX; x <= endX; ++x) {
            for (int y = startY; y <= endY; ++y) {
                SHADER.blocksBuffer.writeInt((int) INDEXER.getIndex(x, y), blocks[x][y].textureType.ordinal());
            }
        }
    }

    public void fullVisualUpdate() {
        for (int x = 0; x < SIZE_X; ++x) {
            for (int y = 0; y < SIZE_Y; ++y) {
                SHADER.blocksBuffer.writeInt((int) INDEXER.getIndex(x, y), blocks[x][y].textureType.ordinal());
            }
        }
    }


    public void update() {

    }

    public void finish() {

    }

    private static List<ChunkGenerator> constructChunkGenerators() {
        return Utils.getAllClasses(ChunkGenerator.class).stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(ChunkGenerator::getPriority))
                .collect(Collectors.toList());
    }

}
