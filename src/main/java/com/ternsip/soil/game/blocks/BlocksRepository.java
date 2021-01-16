package com.ternsip.soil.game.blocks;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Finishable;
import com.ternsip.soil.common.Maths;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.game.generators.ChunkGenerator;
import com.ternsip.soil.graph.display.Image;
import com.ternsip.soil.graph.shader.Light;
import com.ternsip.soil.graph.shader.TextureType;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.stream.Collectors;

public class BlocksRepository implements Finishable {

    public static final int SIZE_X = 8192;
    public static final int SIZE_Y = 4096;
    public final int[][] rgbas = new int[SIZE_X][SIZE_Y];
    public final Material[][] materials = new Material[SIZE_X][SIZE_Y];
    public final ByteBuffer buffer = BufferUtils.createByteBuffer(SIZE_X * SIZE_Y * 4);
    public final Map<Vector2ic, Light> posToLight = new HashMap<>();

    public void init() {
        List<ChunkGenerator> chunkGenerators = Utils.getAllClasses(ChunkGenerator.class).stream()
                .map(Utils::createInstanceSilently)
                .sorted(Comparator.comparing(ChunkGenerator::getPriority))
                .collect(Collectors.toList());
        for (ChunkGenerator chunkGenerator : chunkGenerators) {
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

    public void setMaterialSafe(int x, int y, Material material, int rgba) {
        if (isInside(x, y)) {
            materials[x][y] = material;
            rgbas[x][y] = rgba;
            visualUpdate(x, y, 1, 1);
        }
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < SIZE_X && y >= 0 && y < SIZE_Y;
    }

    public boolean isObstacle(int x, int y) {
        return isInside(x, y) && materials[x][y].obstacle;
    }

    public void visualUpdate(int startX, int startY, int sizeX, int sizeY) {
        if (startX < 0) {
            sizeX += startX;
            startX = 0;
        }
        if (startY < 0) {
            sizeY += startY;
            startY = 0;
        }
        if (startX >= SIZE_X || startY >= SIZE_Y) {
            return;
        }
        sizeX = Math.min(sizeX, SIZE_X - startX);
        sizeY = Math.min(sizeY, SIZE_Y - startY);
        int endX = startX + sizeX - 1;
        int endY = startY + sizeY - 1;
        int volume = sizeX * sizeY;
        if (volume <= 0) return;
        ByteBuffer smallBuffer = Utils.sliceBuffer(buffer, 0, volume * Integer.BYTES, ByteOrder.BIG_ENDIAN);
        for (int x = startX, dx = 0; x <= endX; ++x, ++dx) {
            for (int y = startY, dy = 0; y <= endY; ++y, ++dy) {
                smallBuffer.putInt((dy * sizeX + dx) * Integer.BYTES, rgbas[x][y]);
            }
        }
        Soil.THREADS.client.textureRepository.updateTexture(
                Soil.THREADS.client.textureRepository.getTexture(TextureType.SOIL),
                smallBuffer,
                startX,
                startY,
                sizeX,
                sizeY,
                0
        );
    }

    public void fullVisualUpdate() {
        visualUpdate(0, 0, SIZE_X, SIZE_Y);
    }

    public void fillMaterial(Image image, int color, Material material, double rotationRadians, int midX, int midY, float scale) {
        float realSizeX = image.width * scale;
        float realSizeY = image.height * scale;
        int realSize = (int) Math.ceil(Math.sqrt(realSizeX * realSizeX + realSizeY * realSizeY));
        int offsetX = midX - realSize / 2;
        int offsetY = midY - realSize / 2;
        float halfRealSizeX = realSizeX * 0.5f;
        float halfRealSizeY = realSizeY * 0.5f;
        float halfRealSize = realSize * 0.5f;
        double cos = Math.cos(rotationRadians);
        double sin = Math.sin(rotationRadians);
        for (int x = offsetX, dx = 0; dx < realSize; ++x, ++dx) {
            for (int y = offsetY, dy = 0; dy < realSize; ++y, ++dy) {
                if (x < 0 || y < 0 || x >= SIZE_X || y >= SIZE_Y) continue;
                double rotatedDx = halfRealSizeX + cos * (dx - halfRealSize) - sin * (dy - halfRealSize);
                double rotatedDy = halfRealSizeY + sin * (dx - halfRealSize) + cos * (dy - halfRealSize);
                int cx = (int) (rotatedDx / scale);
                int cy = (int) (rotatedDy / scale);
                if (cx >= image.width || cy >= image.height || cy < 0 || cx < 0) continue;
                int offset = image.getOffset(cx, cy);
                int r = image.frameData[0][offset] & 0xFF;
                int g = image.frameData[0][offset] & 0xFF;
                int b = image.frameData[0][offset] & 0xFF;
                int a = image.frameData[0][offset] & 0xFF;
                if (r >= 128 && g >= 128 && b >= 128) {
                    materials[x][y] = material;
                    rgbas[x][y] = color;
                }
            }
        }
    }

    public void update() {

    }

    public void finish() {
    }

}
