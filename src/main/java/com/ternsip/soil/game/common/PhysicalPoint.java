package com.ternsip.soil.game.common;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Maths;

public class PhysicalPoint {

    public float x = 0;
    public float y = 0;
    public float prevX = 0;
    public float prevY = 0;
    public boolean touchingLeft = false;
    public boolean touchingRight = false;
    public boolean touchingBottom = false;
    public boolean touchingTop = false;

    public PhysicalPoint(float x, float y) {
        this.x = x;
        this.y = y;
        this.prevX = x;
        this.prevY = y;
    }

    public void processMovement(float dx, float dy) {
        float ray = Soil.THREADS.client.blocksRepository.trace(x, y, x + dx, y + dy);
        prevX = x;
        prevY = y;
        x += dx * ray;
        y += dy * ray;
        int blockX = (int) Math.floor(x);
        int blockY = (int) Math.floor(y);
        if (Soil.THREADS.client.blocksRepository.isObstacle(blockX, blockY)) {
            int prevBlockX = (int) Math.floor(prevX);
            int prevBlockY = (int) Math.floor(prevY);
            if (dx > 0 && prevBlockX != blockX) {
                blockX = prevBlockX;
                x = (int) Math.floor(x) - Maths.EPS_F;
            }
            if (dy > 0 && prevBlockY != blockY) {
                blockY = prevBlockY;
                y = (int) Math.floor(y) - Maths.EPS_F;
            }
        }
        touchingBottom = Soil.THREADS.client.blocksRepository.isObstacle(blockX, blockY - 1) && Maths.fract(y) <= Maths.EPS_F;
        touchingLeft = Soil.THREADS.client.blocksRepository.isObstacle(blockX - 1, blockY) && Maths.fract(x) <= Maths.EPS_F;
        touchingRight = Soil.THREADS.client.blocksRepository.isObstacle(blockX + 1, blockY) && Maths.fract(x) >= (1 - Maths.EPS_F);
        touchingTop = Soil.THREADS.client.blocksRepository.isObstacle(blockX, blockY + 1) && Maths.fract(y) >= (1 - Maths.EPS_F);
    }

}
