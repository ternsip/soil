package com.ternsip.soil.game.blocks;

import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public enum Material {

    AIR(new File("soil/interface/lava.png"), false),
    DIRT(new File("soil/interface/lava.png"), true),
    STONE(new File("soil/interface/lava.png"), true),
    LAWN(new File("soil/interface/lava.png"), true),
    GRASS(new File("soil/interface/lava.png"), false),
    WOOD(new File("soil/interface/lava.png"), true),
    WATER(new File("soil/interface/lava.png"), true),
    LAVA(new File("soil/interface/lava.png"), false),
    SAND(new File("soil/interface/lava.png"), true);

    public final File texture;
    public final boolean obstacle;


}
