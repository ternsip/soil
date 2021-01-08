package com.ternsip.soil.game.entities;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Updatable;
import com.ternsip.soil.events.EventHook;
import com.ternsip.soil.events.KeyEvent;
import lombok.RequiredArgsConstructor;

import static org.lwjgl.glfw.GLFW.*;

@RequiredArgsConstructor
public class EntityStatistics extends Entity implements Updatable {

    private final EntityText entityText = new EntityText("FPS:XXX", 10, -1, 1, 0.1f, 0.1f, false, true);

    @Override
    public void update() {
        entityText.setText("FPS:" + Soil.THREADS.client.fpsCounter.fps);
    }

    @Override
    public void register() {
        super.register();
        entityText.register();
    }

    @Override
    public void unregister() {
        super.unregister();
        entityText.unregister();
    }

    @EventHook
    private void handleKeyEvent(KeyEvent event) {
        if (event.getKey() == GLFW_KEY_F3 && event.getAction() == GLFW_PRESS) {
            if (entityText.isRegistered()) {
                entityText.unregister();
            } else {
                entityText.register();
            }
        }
    }


}
