package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.events.base.Callback;
import com.ternsip.soil.common.events.display.CursorPosEvent;
import com.ternsip.soil.common.events.display.ScrollEvent;
import lombok.Getter;
import lombok.Setter;
import org.joml.*;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

@Getter
@Setter
public class Camera {

    public static final float MIN_SCALE = 0.000001f; // TODO rework

    private Vector2f pos = new Vector2f(0);
    private Vector2f scale = new Vector2f(1);

    private Callback<ScrollEvent> scrollCallback = this::recalculateZoom;
    private Callback<CursorPosEvent> cursorPosCallback = this::recalculatePos;

    public Camera() {
        Soil.THREADS.getGraphics().eventIOReceiver.registerCallback(ScrollEvent.class, scrollCallback);
        Soil.THREADS.getGraphics().eventIOReceiver.registerCallback(CursorPosEvent.class, cursorPosCallback);
    }

    public void update() {
        // TODO finish audio
        //getUniverseClient().getSoundRepository().setListenerPosition(eye);
        //getUniverseClient().getSoundRepository().setListenerOrientationFront(getLookDirection());
        //getUniverseClient().getSoundRepository().setListenerOrientationUp(getUpDirection());
    }

    public void finish() {
        Soil.THREADS.getGraphics().eventIOReceiver.unregisterCallback(ScrollEvent.class, scrollCallback);
        Soil.THREADS.getGraphics().eventIOReceiver.unregisterCallback(CursorPosEvent.class, cursorPosCallback);
    }

    private void recalculatePos(CursorPosEvent event) {
        if (Soil.THREADS.getGraphics().eventIOReceiver.isMouseDown(GLFW_MOUSE_BUTTON_2)) {
            pos.x += event.getDx() / 100.0f;
            pos.y -= event.getDy() / 100.0f;
        }
        if (Soil.THREADS.getGraphics().eventIOReceiver.isMouseDown(GLFW_MOUSE_BUTTON_1)) {
            pos.x -= event.getDx() / 100.0f;
            pos.y += event.getDy() / 100.0f;
        }
    }

    private void recalculateZoom(ScrollEvent event) {
        scale.x += event.getYOffset() / 25.0f;
        scale.y += event.getYOffset() / 25.0f;
        scale.x = Math.max(MIN_SCALE, scale.x);
        scale.y = Math.max(MIN_SCALE, scale.y);
    }

}