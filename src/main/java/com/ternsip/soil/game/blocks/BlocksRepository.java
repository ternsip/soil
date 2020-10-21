package com.ternsip.soil.game.blocks;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Finishable;
import com.ternsip.soil.common.Indexer;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.graph.shader.Shader;
import com.ternsip.soil.game.generators.ChunkGenerator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BlocksRepository implements Finishable {

    private static final List<ChunkGenerator> CHUNK_GENERATORS = constructChunkGenerators();
    public static final int SIZE_X = 4000;
    public static final int SIZE_Y = 3000;
    public static final int MAX_LIGHT = 16;

    public static final Indexer INDEXER = new Indexer(SIZE_X, SIZE_Y);
    public final Block[][] blocks = new Block[SIZE_X][SIZE_Y];
    public final LightMassKernel lightMassKernel = new LightMassKernel();

    public void init() {
        for (ChunkGenerator chunkGenerator : CHUNK_GENERATORS) {
            chunkGenerator.populate(this);
        }
    }

    public void updateBlocks(int startX, int startY, Block[][] blocks) {
        int sizeX = blocks.length;
        int sizeY = blocks[0].length;
        int endX = startX + sizeX - 1;
        int endY = startX + sizeY - 1;
        for (int x = startX; x <= endX; ++x) {
            for (int y = startY; y <= endY; ++y) {
                this.blocks[x][y] = blocks[x][y];
            }
        }
        updateLight(startX, startY, sizeX, sizeY);
        visualUpdate(startX, startY, sizeX, sizeY);
    }

    public void updateLight(int startX, int startY, int sizeX, int sizeY) {
        int endX = startX + sizeX - 1;
        int endY = startX + sizeY - 1;
        for (int x = startX; x <= endX; ++x) {
            int height = SIZE_Y - 1;
            while (height > 0 && !blocks[x][height].obstacle) {
                height--;
            }
            lightMassKernel.height[x] = height + 1;
            for (int y = startY; y <= endY; ++y) {
                int index = (int) INDEXER.getIndex(x, y);
                lightMassKernel.setLightMeta(index, Math.max(blocks[x][y].opacity, 0), Math.max(-blocks[x][y].opacity, 0));
            }
        }
        lightMassKernel.update(startX, startY, sizeX, sizeY);
    }

    public void visualUpdate(int startX, int startY, int sizeX, int sizeY) {
        Shader shader = Soil.THREADS.client.shader;
        int endX = startX + sizeX - 1;
        int endY = startX + sizeY - 1;
        for (int x = startX; x <= endX; ++x) {
            for (int y = startY; y <= endY; ++y) {
                int index = (int) INDEXER.getIndex(x, y);
                int offset = index * shader.blocksBuffer.structureLength;
                shader.blocksBuffer.writeInt(offset, blocks[x][y].textureType.ordinal());
                shader.blocksBuffer.writeFloat(offset + 1, lightMassKernel.getLightSky(index) / (float)MAX_LIGHT);
                shader.blocksBuffer.writeFloat(offset + 2, lightMassKernel.getLightEmit(index)  / (float)MAX_LIGHT);
            }
        }
    }

    public void fullVisualUpdate() {
        updateLight(0, 0, SIZE_X, SIZE_Y);
        visualUpdate(0, 0, SIZE_X, SIZE_Y);
    }


    public void update() {

    }

    public void finish() {
        lightMassKernel.dispose();
    }

    private static List<ChunkGenerator> constructChunkGenerators() {
        return Utils.getAllClasses(ChunkGenerator.class).stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(ChunkGenerator::getPriority))
                .collect(Collectors.toList());
    }

}