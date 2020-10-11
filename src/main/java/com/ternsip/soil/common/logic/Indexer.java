package com.ternsip.soil.common.logic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Indexer {

    private final int sizeX;
    private final int sizeY;

    public long getIndex(int x, int y) {
        return x + y * sizeX;
    }

    public long getIndexLooping(int x, int y) {
        int na = Maths.positiveLoop(x, sizeX);
        int nb = Maths.positiveLoop(y, sizeY);
        return getIndex(na, nb);
    }

    public int getX(long index) {
        return (int) (index % sizeX);
    }

    public int getY(long index) {
        return (int) (index / sizeX);
    }

    public long getVolume() {
        return ((long) sizeX) * sizeY;
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < sizeX && y >= 0 && y < sizeY;
    }

    public boolean isOnBorder(int x, int y) {
        return x == 0 || x == sizeX - 1 || y == 0 || y == sizeY - 1;
    }

}