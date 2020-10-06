package com.ternsip.soil.graph.display;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.events.display.ResizeEvent;
import com.ternsip.soil.general.Graphics;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@Getter
@Setter
public class Camera {

    private Vector2fc position = new Vector2f(0);
    private Vector2fc scale = new Vector2f(1);
    private float rotation = 0;

    public Camera() {
        //Graphics graphics = Soil.THREADS.getGraphics();
        //graphics.eventIOReceiver.registerCallback(ResizeEvent.class, e -> this.recalculateProjectionMatrices(e.getWidth(), e.getHeight()));
        //recalculateProjectionMatrices(graphics.windowData.getWidth(), graphics.windowData.getHeight());
    }

}