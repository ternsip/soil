package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.events.CursorPosEvent;
import com.ternsip.soil.events.ResizeEvent;
import com.ternsip.soil.events.ScrollEvent;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class Camera {

    public static final float MIN_SCALE = 0.000001f; // TODO rework

    public Vector2f pos = new Vector2f(0);
    public Vector2f scale = new Vector2f(1f);
    public double mousePosX = 0;
    public double mousePosY = 0;
    public int width = 1;
    public int height = 1;
    public float aspectX = 1;
    public float aspectY = 1;

    public Camera() {
        Soil.THREADS.client.eventIOReceiver.register(this);
    }

    public void update() {
        Soil.THREADS.client.soundRepository.setListenerPosition(pos.x, pos.y);
    }

    public void finish() {
        Soil.THREADS.client.eventIOReceiver.unregister(this);
    }

    private void recalculatePos(CursorPosEvent event) {
        if (Soil.THREADS.client.eventIOReceiver.isMouseDown(GLFW_MOUSE_BUTTON_2)) {
            pos.x -= event.dx / (scale.x * 500.0f);
            pos.y += event.dy / (scale.y * 500.0f);
        }
        mousePosX = event.normalX;
        mousePosY = event.normalY;
    }

    private void recalculateZoom(ScrollEvent event) {
        scale.x += scale.x * event.getYOffset() / 25.0f;
        scale.y += scale.y * event.getYOffset() / 25.0f;
        scale.x = Math.max(MIN_SCALE, scale.x);
        scale.y = Math.max(MIN_SCALE, scale.y);
    }

    private void handleResize(ResizeEvent resizeEvent) {
        width = resizeEvent.getWidth();
        height = resizeEvent.getHeight();
        if (width >= height) {
            aspectX = 1;
            aspectY = (float)width / height;
        } else {
            aspectX = (float)height / width;
            aspectY = 1;
        }
    }

}