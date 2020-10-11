package com.ternsip.soil.universe;

import com.ternsip.soil.common.logic.Finishable;
import com.ternsip.soil.common.logic.Indexer;
import com.ternsip.soil.common.logic.Utils;
import com.ternsip.soil.graph.shader.base.BufferUpdate;
import com.ternsip.soil.graph.shader.base.Shader;
import com.ternsip.soil.universe.generators.ChunkGenerator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BlocksRepository implements Finishable {

    private static final List<ChunkGenerator> CHUNK_GENERATORS = constructChunkGenerators();
    public static final int MAX_SIZE_X = 4000;
    public static final int MAX_SIZE_Y = 3000;
    public static Shader SHADER;

    public final int sizeX = 4000;
    public final int sizeY = 3000;
    public final Indexer indexer = new Indexer(sizeX, sizeY);
    public final Block[][] blocks = new Block[sizeX][sizeY];

    public void init() {
        for (ChunkGenerator chunkGenerator : CHUNK_GENERATORS) {
            chunkGenerator.populate(this);
        }
    }

    public void updateBlocks(int startX, int startY, Block[][] blocks) {
        int endX = startX + blocks.length - 1;
        int endY = startX + blocks[0].length - 1;
        for (int y = startY; y <= endY; ++y) {
            for (int x = startX; x <= endX; ++x) {
                this.blocks[x][y] = blocks[x][y];
            }
        }
    }

    public void visualUpdate(int startX, int startY, int sizeX, int sizeY) {
        int endX = startX + sizeX - 1;
        int endY = startX + sizeY - 1;
        for (int y = startY; y <= endY; ++y) {
            for (int x = startX; x <= endX; ++x) {
                int index = (int) indexer.getIndex(startX, y);
                SHADER.blocksBuffer.writeInt(index, blocks[x][y].textureType.ordinal());
            }
            int startIndex = (int) indexer.getIndex(startX, y);
            int endIndex = (int) indexer.getIndex(endX, y);
            SHADER.blocksUpdates.add(new BufferUpdate(startIndex, endIndex));
        }
    }

    public void fullVisualUpdate() {
        for (int y = 0; y < sizeY; ++y) {
            for (int x = 0; x < sizeX; ++x) {
                int index = (int) indexer.getIndex(x, y);
                SHADER.blocksBuffer.writeInt(index, blocks[x][y].textureType.ordinal());
            }
        }
        SHADER.blocksUpdates.add(new BufferUpdate(0, (int) indexer.getVolume() - 1));
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
