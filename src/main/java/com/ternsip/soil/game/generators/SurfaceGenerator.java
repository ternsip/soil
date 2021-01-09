package com.ternsip.soil.game.generators;

import com.ternsip.soil.common.Maths;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.game.blocks.BlocksRepository;
import com.ternsip.soil.game.blocks.Material;
import com.ternsip.soil.graph.display.Image;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import static com.ternsip.soil.game.blocks.BlocksRepository.SIZE_X;
import static com.ternsip.soil.game.blocks.BlocksRepository.SIZE_Y;

@Getter
public class SurfaceGenerator implements ChunkGenerator {

    private final int caveNumber = 311;
    private final ArrayList<Image> caves = loadCaves();

    public static ArrayList<Image> loadCaves() {
        return Utils.getResourceListing(new File("soil/terrain/caves"), Image.EXTENSIONS)
                .stream()
                .map(Image::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void populate(BlocksRepository blocksRepository) {
        Random random = new Random(0);
        Image sand = new Image(Material.SAND.texture);
        for (int x = 0; x < SIZE_X; ++x) {
            for (int y = 0; y < SIZE_Y; ++y) {
                blocksRepository.materials[x][y] = Material.SAND;
                blocksRepository.rgbas[x][y] = sand.getRGBA(0, x % sand.width, y % sand.height);
            }
        }
        for (int i = 0; i < caveNumber; ++i) {
            Image cave = caves.get(Math.abs(random.nextInt()) % caves.size());
            float scale = Math.abs(random.nextFloat()) * 3;
            float realSizeX = cave.width * scale;
            float realSizeY = cave.height * scale;
            int realSize = (int) Math.ceil(Math.sqrt(realSizeX * realSizeX + realSizeY * realSizeY));
            int offsetX = Math.abs(random.nextInt()) % (SIZE_X + realSize) - realSize;
            int offsetY = Math.abs(random.nextInt()) % (SIZE_Y + realSize) - realSize;
            float halfRealSizeX = realSizeX * 0.5f;
            float halfRealSizeY = realSizeY * 0.5f;
            float halfRealSize = realSize * 0.5f;
            double rotationRadians = random.nextFloat() % (2 * Math.PI);
            double cos = Math.cos(rotationRadians);
            double sin = Math.sin(rotationRadians);
            for (int x = offsetX, dx = 0; dx < realSize; ++x, ++dx) {
                for (int y = offsetY, dy = 0; dy < realSize; ++y, ++dy) {
                    if (x < 0 || y < 0 || x >= SIZE_X || y >= SIZE_Y) continue;
                    double rotatedDx = halfRealSizeX + cos * (dx - halfRealSize) - sin * (dy - halfRealSize);
                    double rotatedDy = halfRealSizeY + sin * (dx - halfRealSize) + cos * (dy - halfRealSize);
                    int cx = (int) (rotatedDx / scale);
                    int cy = (int) (rotatedDy / scale);
                    if (cx >= cave.width || cy >= cave.height || cy < 0 || cx < 0) continue;
                    int offset = cave.getOffset(cx, cy);
                    int r = cave.frameData[0][offset] & 0xFF;
                    int g = cave.frameData[0][offset] & 0xFF;
                    int b = cave.frameData[0][offset] & 0xFF;
                    int a = cave.frameData[0][offset] & 0xFF;
                    if (r >= 128 && g >= 128 && b >= 128) {
                        blocksRepository.materials[x][y] = Material.AIR;
                        blocksRepository.rgbas[x][y] = Maths.packRGBA(0, 0, 0, 0);
                    }
                }
            }
        }
    }


}
