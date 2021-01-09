package com.ternsip.soil.game.generators;

import com.ternsip.soil.common.Maths;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.game.blocks.BlocksRepository;
import com.ternsip.soil.game.blocks.Material;
import com.ternsip.soil.graph.display.Image;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
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
            int realSizeX = (int) (cave.width * scale);
            int realSizeY = (int) (cave.height * scale);
            int offsetX = Math.abs(random.nextInt()) % (SIZE_X + realSizeX) - realSizeX;
            int offsetY = Math.abs(random.nextInt()) % (SIZE_Y + realSizeY) - realSizeY;
            for (int x = offsetX, dx = 0; dx < realSizeX; ++x, ++dx) {
                for (int y = offsetY, dy = 0; dy < realSizeY; ++y, ++dy) {
                    if (x < 0 || y < 0 || x >= SIZE_X || y >= SIZE_Y) continue;
                    int cx = (int) (dx / scale) % cave.width;
                    int cy = (int) (dy / scale) % cave.height;
                    int offset = cave.getOffset(cx, cy);
                    byte r = cave.frameData[0][offset];
                    byte g = cave.frameData[0][offset];
                    byte b = cave.frameData[0][offset];
                    byte a = cave.frameData[0][offset];
                    if (r == (byte) 255 && g == (byte) 255 && b == (byte) 255) {
                        blocksRepository.materials[x][y] = Material.AIR;
                        blocksRepository.rgbas[x][y] = Maths.packRGBA(0, 0, 0, 0);
                    }
                }
            }
        }
    }


}
