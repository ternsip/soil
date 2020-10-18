package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.events.Callback;
import com.ternsip.soil.events.CursorPosEvent;
import com.ternsip.soil.events.ScrollEvent;
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
        Soil.THREADS.client.eventIOReceiver.register(this);
    }

    public void update() {
        Soil.THREADS.client.soundRepository.setListenerPosition(pos.x, pos.y);
        //Soil.THREADS.client.soundRepository.setListenerOrientationFront(getLookDirection());
        //Soil.THREADS.client.soundRepository.setListenerOrientationUp(getUpDirection());
    }

    public void finish() {
        Soil.THREADS.client.eventIOReceiver.unregister(this);
    }

    private void recalculatePos(CursorPosEvent event) {
        if (Soil.THREADS.client.eventIOReceiver.isMouseDown(GLFW_MOUSE_BUTTON_2)) {
            pos.x += event.getDx() / (scale.x * 500.0f);
            pos.y -= event.getDy() / (scale.y * 500.0f);
        }
        if (Soil.THREADS.client.eventIOReceiver.isMouseDown(GLFW_MOUSE_BUTTON_1)) {
            pos.x -= scale.x * event.getDx() / (scale.x * 500.0f);
            pos.y += scale.y * event.getDy() / (scale.y * 500.0f);
        }
    }

    private void recalculateZoom(ScrollEvent event) {
        scale.x += scale.x * event.getYOffset() / 25.0f;
        scale.y += scale.y * event.getYOffset() / 25.0f;
        scale.x = Math.max(MIN_SCALE, scale.x);
        scale.y = Math.max(MIN_SCALE, scale.y);
    }

}