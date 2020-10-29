package com.ternsip.soil.game.entities;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Maths;
import com.ternsip.soil.common.Updatable;
import com.ternsip.soil.events.KeyEvent;
import com.ternsip.soil.events.MouseButtonEvent;
import com.ternsip.soil.game.blocks.Block;
import com.ternsip.soil.game.common.PhysicalPoint;
import com.ternsip.soil.graph.shader.TextureType;

import static org.lwjgl.glfw.GLFW.*;

public class EntityPlayer extends Entity implements Updatable {

    private final int layer;
    private final EntityQuad body;
    private final PhysicalPoint[] points = new PhysicalPoint[8];
    private float x = 50;
    private float y = 47;
    private float fallingSpeed = 0;
    private float width = 1.5f;
    private float height = 2;
    private boolean touchingLeft = false;
    private boolean touchingRight = false;
    private boolean touchingBottom = false;
    private boolean touchingTop = false;

    public EntityPlayer(int layer) {
        this.layer = layer;
        this.body = new EntityQuad(layer, TextureType.PLAYER_IDLE, false);
        for (int i = 0; i < points.length; ++i) {
            points[i] = new PhysicalPoint(0, 0);
        }
        calibratePhysicalPoints();
    }

    @Override
    public void register() {
        super.register();
        body.register();
        Soil.THREADS.client.eventIOReceiver.register(this);
    }

    @Override
    public void unregister() {
        super.unregister();
        body.unregister();
        Soil.THREADS.client.eventIOReceiver.unregister(this);
    }

    @Override
    public void update() {
        if (Soil.THREADS.client.eventIOReceiver.isKeyDown(GLFW_KEY_W)) {
            processMovement(0, 0.1f);
        }
        if (Soil.THREADS.client.eventIOReceiver.isKeyDown(GLFW_KEY_S)) {
            processMovement(0, -0.1f);
        }
        if (Soil.THREADS.client.eventIOReceiver.isKeyDown(GLFW_KEY_D)) {
            processMovement(0.1f, 0);
        }
        if (Soil.THREADS.client.eventIOReceiver.isKeyDown(GLFW_KEY_A)) {
            processMovement(-0.1f, 0);
        }
        if (!touchingBottom) {
            processMovement(0, 0);
        }
    }

    private void calibratePhysicalPoints() {
        this.points[0].x = x - width * 0.5f;
        this.points[0].y = y;
        this.points[1].x = x + width * 0.5f;
        this.points[1].y = y;
        this.points[2].x = x + width * 0.5f;
        this.points[2].y = y + height;
        this.points[3].x = x - width * 0.5f;
        this.points[3].y = y + height;
        this.points[4].x = x;
        this.points[4].y = y;
        this.points[5].x = x;
        this.points[5].y = y + height;
        this.points[6].x = x + width * 0.5f;
        this.points[6].y = y + height * 0.5f;
        this.points[7].x = x - width * 0.5f;
        this.points[7].y = y + height * 0.5f;
    }

    private void processMovement(float dx, float dy) {
        dy -= fallingSpeed;
        for (PhysicalPoint point : points) {
            point.processMovement(dx, dy);
        }
        touchingBottom = false;
        touchingTop = false;
        touchingLeft = false;
        touchingRight = false;
        for (PhysicalPoint point : points) {
            dx = Maths.minByAbs(dx, point.x - point.prevX);
            dy = Maths.minByAbs(dy, point.y - point.prevY);
            touchingBottom |= point.touchingBottom;
            touchingTop |= point.touchingTop;
            touchingLeft |= point.touchingLeft;
            touchingRight |= point.touchingRight;
        }
        x += dx;
        y += dy;
        calibratePhysicalPoints();
        if (touchingBottom) {
            fallingSpeed = Math.min(fallingSpeed, 0);
        } else {
            fallingSpeed += Soil.THREADS.client.settings.gravity;
        }
        if (touchingTop) {
            fallingSpeed = Math.max(fallingSpeed, 0);
        }
        body.x1 = x - width * 0.5f;
        body.x2 = x + width * 0.5f;
        body.x3 = x + width * 0.5f;
        body.x4 = x - width * 0.5f;
        body.y1 = y + height;
        body.y2 = y + height;
        body.y3 = y;
        body.y4 = y;
        body.writeToBufferLayout();
        Soil.THREADS.client.camera.pos.x = x;
        Soil.THREADS.client.camera.pos.y = y;
    }

    private void teleport(int newX, int newY) {
        x = newX;
        y = newY;
        calibratePhysicalPoints();
        processMovement(0, 0);
    }

    private void handleKeyEvent(KeyEvent event) {
        if (event.getKey() == GLFW_KEY_T && event.getAction() == GLFW_PRESS) {
            teleport(50, 50);
        }
        if (event.getKey() == GLFW_KEY_Q && event.getAction() == GLFW_PRESS) {
            int blockX = (int) Math.floor(x);
            int blockY = (int) Math.floor(y - 1);
            Soil.THREADS.client.blocksRepository.setBlockSafe(blockX, blockY, Block.AIR);
            processMovement(0, 0);
        }
        if (event.getKey() == GLFW_KEY_SPACE && event.getAction() == GLFW_PRESS && touchingBottom) {
            fallingSpeed = -0.1f;
            processMovement(0, 0);
        }
    }

    private void handleMouseButtonEvent(MouseButtonEvent event) {

    }
}
