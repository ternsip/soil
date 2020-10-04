package com.ternsip.soil.common.events.base;

import com.ternsip.soil.common.events.display.KeyEvent;
import com.ternsip.soil.common.events.display.MouseButtonEvent;
import lombok.Getter;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

@Getter
public class EventIOReceiver extends EventReceiver {

    private final boolean[] keyPressed = new boolean[512];
    private final boolean[] mouseButtonPressed = new boolean[8];

    public EventIOReceiver() {
        registerCallback(KeyEvent.class, (KeyEvent keyEvent) -> {
            if (keyEvent.getKey() >= 0 && keyEvent.getKey() < keyPressed.length) {
                keyPressed[keyEvent.getKey()] = keyEvent.getAction() != GLFW_RELEASE;
            }
        });
        registerCallback(MouseButtonEvent.class, (MouseButtonEvent mouseButtonEvent) -> {
            if (mouseButtonEvent.getButton() >= 0 && mouseButtonEvent.getButton() < mouseButtonPressed.length) {
                mouseButtonPressed[mouseButtonEvent.getButton()] = mouseButtonEvent.getAction() != GLFW_RELEASE;
            }
        });
    }

    public boolean isKeyDown(int key) {
        return keyPressed[key];
    }

    public boolean isMouseDown(int button) {
        return mouseButtonPressed[button];
    }

}
