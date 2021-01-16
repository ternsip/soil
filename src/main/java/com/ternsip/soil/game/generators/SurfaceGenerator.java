package com.ternsip.soil.game.generators;

import com.ternsip.soil.common.Maths;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.game.blocks.BlocksRepository;
import com.ternsip.soil.game.blocks.Material;
import com.ternsip.soil.graph.display.Image;
import com.ternsip.soil.graph.shader.Light;
import lombok.Getter;
import org.joml.Vector2i;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import static com.ternsip.soil.game.blocks.BlocksRepository.SIZE_X;
import static com.ternsip.soil.game.blocks.BlocksRepository.SIZE_Y;

@Getter
public class SurfaceGenerator implements ChunkGenerator {

    private final int caveNumber = 311;
    private final int waterPoolsNumber = 333;
    private final int lavaPoolsNumber = 555;
    private final ArrayList<Image> caves = loadCaves();
    private final ArrayList<Image> pools = loadPools();

    public static ArrayList<Image> loadPools() {
        return Utils.getResourceListing(new File("soil/terrain/pools"), Image.EXTENSIONS)
                .stream()
                .map(Image::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

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
        for (int i = 0; i < caveNumber && caves.size() > 0; ++i) {
            Image cave = caves.get(random.nextInt(caves.size()));
            blocksRepository.fillMaterial(
                    cave,
                    Maths.packRGBA(0, 0, 0, 0),
                    Material.AIR,
                    random.nextFloat() % (2 * Math.PI),
                    random.nextInt(SIZE_X),
                    random.nextInt(SIZE_Y),
                    Math.abs(random.nextFloat()) * 3
            );
        }
        for (int i = 0; i < waterPoolsNumber && pools.size() > 0; ++i) {
            Image pool = pools.get(random.nextInt(pools.size()));
            blocksRepository.fillMaterial(
                    pool,
                    Maths.packRGBA(0, 0, 255, 128),
                    Material.WATER,
                    random.nextFloat() % (2 * Math.PI),
                    random.nextInt(SIZE_X),
                    random.nextInt(SIZE_Y),
                    Math.abs(random.nextFloat()) * 3
            );
        }
        for (int i = 0; i < lavaPoolsNumber && pools.size() > 0; ++i) {
            Image pool = pools.get(random.nextInt(pools.size()));
            blocksRepository.fillMaterial(
                    pool,
                    Maths.packRGBA(207, 16, 32, 128),
                    Material.LAVA,
                    random.nextFloat() % (2 * Math.PI),
                    random.nextInt(SIZE_X),
                    random.nextInt(SIZE_Y),
                    Math.abs(random.nextFloat()) * 3
            );
        }
        for (int x = 0; x < SIZE_X; ++x) {
            for (int y = 0; y < SIZE_Y; ++y) {
                if (blocksRepository.materials[x][y] == Material.LAVA && random.nextInt(200) == 0) {
                    Light light = new Light(x, y, 30f + random.nextFloat() * 30, 1.0f);
                    blocksRepository.posToLight.put(new Vector2i(x, y), light);
                    light.register();
                }
            }
        }
    }


}
