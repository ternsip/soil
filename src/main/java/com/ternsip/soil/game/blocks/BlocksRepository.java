package com.ternsip.soil.game.blocks;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Finishable;
import com.ternsip.soil.common.Indexer;
import com.ternsip.soil.common.Maths;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.game.generators.ChunkGenerator;
import com.ternsip.soil.graph.shader.Shader;

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

    public void init() {
        for (ChunkGenerator chunkGenerator : CHUNK_GENERATORS) {
            chunkGenerator.populate(this);
        }
    }

    public float trace(float x1, float y1, float x2, float y2) {
        float tDeltaX = x2 - x1;
        float tDeltaY = y2 - y1;
        float tMaxX = Maths.INF_F;
        float tMaxY = Maths.INF_F;
        int signX = 0;
        int signY = 0;
        if (tDeltaX > Maths.EPS_F) {
            tDeltaX = 1 / tDeltaX;
            signX = 1;
            tMaxX = tDeltaX * (1 - Maths.fract(x1));
        } else if (tDeltaX < -Maths.EPS_F) {
            tDeltaX = -1 / tDeltaX;
            signX = -1;
            tMaxX = tDeltaX * Maths.fract(x1);
        } else {
            tDeltaX = Maths.INF_F;
        }
        if (tDeltaY > Maths.EPS_F) {
            tDeltaY = 1 / tDeltaY;
            signY = 1;
            tMaxY = tDeltaY * (1 - Maths.fract(y1));
        } else if (tDeltaY < -Maths.EPS_F) {
            signY = -1;
            tDeltaY = -1 / tDeltaY;
            tMaxY = tDeltaY * Maths.fract(y1);
        } else {
            tDeltaY = Maths.INF_F;
        }
        int cx = (int) Math.floor(x1);
        int cy = (int) Math.floor(y1);
        if (isObstacle(cx, cy)) {
            return Math.min(1, Math.min(tMaxX, tMaxY));
        }
        while (tMaxX <= 1 || tMaxY <= 1) {
            if (tMaxX < tMaxY) {
                cx += signX;
                if (isObstacle(cx, cy)) {
                    return Math.min(1, Math.min(tMaxX, tMaxY));
                }
                tMaxX = tMaxX + tDeltaX;
            } else {
                cy += signY;
                if (isObstacle(cx, cy)) {
                    return Math.min(1, Math.min(tMaxX, tMaxY));
                }
                tMaxY = tMaxY + tDeltaY;
            }
        }
        return 1;
    }

    public boolean setBlockSafe(int x, int y, Block block) {
        if (INDEXER.isInside(x, y)) {
            blocks[x][y] = block;
            visualUpdate(x, y, 1, 1);
            return true;
        }
        return false;
    }

    public boolean isObstacle(int x, int y) {
        return INDEXER.isInside(x, y) && blocks[x][y].obstacle;
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
        visualUpdate(startX, startY, sizeX, sizeY);
    }

    public void visualUpdate(int startX, int startY, int sizeX, int sizeY) {
        Shader shader = Soil.THREADS.client.shader;
        int endX = startX + sizeX - 1;
        int endY = startY + sizeY - 1;
        for (int x = startX; x <= endX; ++x) {
            int height = SIZE_Y - 1;
            while (
                    height > 0 && !blocks[x][height].obstacle &&
                    x > 0 && !blocks[x - 1][height].obstacle &&
                    x < (SIZE_X - 1) && !blocks[x + 1][height].obstacle
            ) {
                height--;
            }
            shader.heightsBuffer.writeInt(x, height + 1);
            for (int y = startY; y <= endY; ++y) {
                int index = (int) INDEXER.getIndex(x, y);
                int offset = index * shader.blocksBuffer.structureLength;
                shader.blocksBuffer.writeInt(offset, blocks[x][y].textureType.ordinal());
                shader.blocksBuffer.writeInt(offset + 3, Math.max(0, blocks[x][y].opacity));
                shader.blocksBuffer.writeInt(offset + 4, Math.max(0, -blocks[x][y].opacity));
            }
        }
    }

    public void fullVisualUpdate() {
        visualUpdate(0, 0, SIZE_X, SIZE_Y);
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
