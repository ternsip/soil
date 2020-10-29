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
    private final PhysicalPoint leftLower = new PhysicalPoint(0, 0);
    private final PhysicalPoint rightLower = new PhysicalPoint(0, 0);
    private final PhysicalPoint rightTop = new PhysicalPoint(0, 0);
    private final PhysicalPoint leftTop = new PhysicalPoint(0, 0);
    private float x = 50;
    private float y = 47;
    private float fallingSpeed = 0;
    private boolean touchingLeft = false;
    private boolean touchingRight = false;
    private boolean touchingBottom = false;
    private boolean touchingTop = false;

    public EntityPlayer(int layer) {
        this.layer = layer;
        this.body = new EntityQuad(layer, TextureType.PLAYER_IDLE, false);
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
        this.leftLower.x = x - 0.75f;
        this.leftLower.y = y;
        this.rightLower.x = x + 0.75f;
        this.rightLower.y = y;
        this.rightTop.x = x + 0.75f;
        this.rightTop.y = y + 1.75f;
        this.leftTop.x = x - 0.75f;
        this.leftTop.y = y + 1.75f;
    }

    private void processMovement(float dx, float dy) {
        dy -= fallingSpeed;
        leftLower.processMovement(dx, dy);
        rightLower.processMovement(dx, dy);
        leftTop.processMovement(dx, dy);
        rightTop.processMovement(dx, dy);
        dx = Maths.minByAbs(dx, leftLower.x - leftLower.prevX);
        dy = Maths.minByAbs(dy, leftLower.y - leftLower.prevY);
        dx = Maths.minByAbs(dx, rightLower.x - rightLower.prevX);
        dy = Maths.minByAbs(dy, rightLower.y - rightLower.prevY);
        dx = Maths.minByAbs(dx, leftTop.x - leftTop.prevX);
        dy = Maths.minByAbs(dy, leftTop.y - leftTop.prevY);
        dx = Maths.minByAbs(dx, rightTop.x - rightTop.prevX);
        dy = Maths.minByAbs(dy, rightTop.y - rightTop.prevY);
        touchingBottom = leftLower.touchingBottom | rightLower.touchingBottom | leftTop.touchingBottom | rightTop.touchingBottom;
        touchingTop = leftLower.touchingTop | rightLower.touchingTop | leftTop.touchingTop | rightTop.touchingTop;
        touchingLeft = leftLower.touchingLeft | rightLower.touchingLeft | leftTop.touchingLeft | rightTop.touchingLeft;
        touchingRight = leftLower.touchingRight | rightLower.touchingRight | leftTop.touchingRight | rightTop.touchingRight;
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
        body.x1 = leftLower.x;
        body.x2 = rightLower.x;
        body.x3 = rightTop.x;
        body.x4 = leftTop.x;
        body.y1 = leftTop.y;
        body.y2 = rightTop.y;
        body.y3 = rightLower.y;
        body.y4 = leftLower.y;
        body.writeToBufferLayout();
        Soil.THREADS.client.camera.pos.x = x;
        Soil.THREADS.client.camera.pos.y = y;
    }

    private void handleKeyEvent(KeyEvent event) {
        if (event.getKey() == GLFW_KEY_T && event.getAction() == GLFW_PRESS) {
            x = 50;
            y = 50;
            processMovement(0, 0);
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
