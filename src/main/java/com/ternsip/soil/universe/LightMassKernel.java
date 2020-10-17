package com.ternsip.soil.universe;

import com.aparapi.Kernel;
import com.aparapi.Range;

import static com.ternsip.soil.universe.BlocksRepository.*;

public class LightMassKernel extends Kernel {

    private final int[] offsetX = {-1, 1, 0, 0};
    private final int[] offsetY = {0, 0, 1, -1};

    final int[] light = new int[SIZE_X * SIZE_Y];
    final int[] lightMeta = new int[SIZE_X * SIZE_Y];
    final int[] height = new int[SIZE_X];

    private int calcSize;
    private int startX;
    private int startY;
    private int sizeX;
    private int sizeY;
    private int endX;
    private int endY;
    private int cleaning;

    public void setLightMeta(int index, int opacity, int selfEmit) {
        lightMeta[index] = (opacity << 8) + selfEmit;
    }

    public int getLightSky(int index) {
        return Math.min((light[index] >> 24) & 0xFF, (light[index] >> 16) & 0xFF);
    }

    public int getLightEmit(int index) {
        return Math.min((light[index] >> 8) & 0xFF, light[index] & 0xFF);
    }

    public void update(int startX, int startY, int sizeX, int sizeY) {
        this.startX = startX - MAX_LIGHT;
        this.startY = startY - MAX_LIGHT;
        this.endX = startX + MAX_LIGHT + sizeX - 1;
        this.endY = startY + MAX_LIGHT + sizeY - 1;
        this.sizeX = sizeX + 2 * MAX_LIGHT;
        this.sizeY = sizeY + 2 * MAX_LIGHT;
        this.calcSize = this.sizeX * this.sizeY;
        this.cleaning = 1;
        execute(Range.create(calcSize));
        this.cleaning = 0;
        execute(Range.create(calcSize * MAX_LIGHT));
    }

    @Override
    public void run() {
        int frameIndex = getGlobalId() % calcSize;
        int x = startX + frameIndex % sizeX;
        int y = startY + frameIndex / sizeX;
        if (startX == x || endX == x || startY == y || endY == y || x < 0 || y < 0 || x >= SIZE_X || y >= SIZE_Y) {
            return;
        }
        int realIndex = y * SIZE_X + x;
        if (cleaning > 0) {
            light[realIndex] = 0;
            return;
        }
        int lightMetaValue = lightMeta[realIndex];
        int opacity = (lightMetaValue >> 8) & 0xFF;
        int selfEmit = lightMetaValue & 0xFF;
        int bestSkyLightX = (startY + y) >= height[x] ? MAX_LIGHT : 0;
        int bestSkyLightY = (startY + y) >= height[x] ? MAX_LIGHT : 0;
        int bestEmitLightX = selfEmit;
        int bestEmitLightY = selfEmit;
        for (int k = 0; k < 4; ++k) {
            int nx = x + offsetX[k];
            int ny = y + offsetY[k];
            if (nx < 0 || nx >= SIZE_X || ny < 0 || ny >= SIZE_Y) {
                continue;
            }
            int opacityX = (offsetX[k] == 0 ? 0 : opacity);
            int opacityY = (offsetY[k] == 0 ? 0 : opacity);
            int nLightValue = light[ny * SIZE_X + nx];
            int newSkyX = ((nLightValue >> 24) & 0xFF) - opacityX;
            int newSkyY = ((nLightValue >> 16) & 0xFF) - opacityY;
            int newEmitX = ((nLightValue >> 8) & 0xFF) - opacityX;
            int newEmitY = (nLightValue & 0xFF) - opacityY;
            if (min(newSkyX, newSkyY) > min(bestSkyLightX, bestSkyLightY)) {
                bestSkyLightX = newSkyX;
                bestSkyLightY = newSkyY;
            }
            if (min(newEmitX, newEmitY) > min(bestEmitLightX, bestEmitLightY)) {
                bestEmitLightX = newEmitX;
                bestEmitLightY = newEmitY;
            }
        }
        light[realIndex] = (bestSkyLightX << 24) + (bestSkyLightY << 16) + (bestEmitLightX << 8) + bestEmitLightY;
    }

}