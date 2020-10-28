package com.ternsip.soil.game.entities;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Updatable;
import com.ternsip.soil.events.KeyEvent;
import com.ternsip.soil.events.MouseButtonEvent;
import com.ternsip.soil.graph.shader.TextureType;

import static org.lwjgl.glfw.GLFW.*;

public class EntityPlayer extends Entity implements Updatable {

    private final int layer;
    private final EntityQuad body;
    private float x = 50;
    private float y = 47;

    public EntityPlayer(int layer) {
        this.layer = layer;
        this.body = new EntityQuad(layer, TextureType.PLAYER_IDLE, false);
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
            y += 0.1;
        }
        if (Soil.THREADS.client.eventIOReceiver.isKeyDown(GLFW_KEY_S)) {
            y -= 0.1;
        }
        if (Soil.THREADS.client.eventIOReceiver.isKeyDown(GLFW_KEY_D)) {
            x += 0.1;
        }
        if (Soil.THREADS.client.eventIOReceiver.isKeyDown(GLFW_KEY_A)) {
            x -= 0.1;
        }
        body.x1 = x;
        body.x2 = x + 2;
        body.x3 = x + 2;
        body.x4 = x;
        body.y1 = y + 3;
        body.y2 = y + 3;
        body.y3 = y;
        body.y4 = y;
        body.writeToBufferLayout();
        Soil.THREADS.client.camera.pos.x = x;
        Soil.THREADS.client.camera.pos.y = y;
    }

    private void handleKeyEvent(KeyEvent event) {
        if (event.getKey() == GLFW_KEY_SPACE && event.getAction() == GLFW_PRESS) {
            // jump
        }
    }

    private void handleMouseButtonEvent(MouseButtonEvent event) {

    }
}
