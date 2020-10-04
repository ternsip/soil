package com.ternsip.soil.graph.camera;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.events.display.ResizeEvent;
import com.ternsip.soil.general.Graphics;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Getter
@Setter
public class Camera {

    public static final float FOV = (float) Math.toRadians(80);
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 10000f;

    private Matrix4fc projectionMatrix;

    private Vector3fc position = new Vector3f(0);
    private Matrix4fc viewMatrix = new Matrix4f();

    public Camera() {
        Graphics graphics = Soil.THREADS.getGraphics();
        graphics.eventIOReceiver.registerCallback(ResizeEvent.class, e -> this.recalculateProjectionMatrices(e.getWidth(), e.getHeight()));
        recalculateProjectionMatrices(graphics.windowData.getWidth(), graphics.windowData.getHeight());
    }

    private void recalculateProjectionMatrices(int width, int height) {
        float ratio = (float) width / height;
        projectionMatrix = new Matrix4f().perspective(FOV, ratio, NEAR_PLANE, FAR_PLANE);
    }

}