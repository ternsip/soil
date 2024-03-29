package com.ternsip.soil.game.entities;

import com.ternsip.soil.graph.shader.BaseTextures;
import com.ternsip.soil.graph.shader.Quad;

import static com.ternsip.soil.graph.shader.Shader.QUAD_FLAG_FONT256;
import static com.ternsip.soil.graph.shader.Shader.QUAD_FLAG_PINNED;

public class EntityText extends Entity {

    private final int layer;
    private Quad[] quads;
    private String text;
    private boolean centered;
    private boolean pinned; // TODO handle changes
    private float textCompression = 0.8f;
    private float posX;
    private float posY;
    private float scaleX;
    private float scaleY;

    public EntityText(String text, int layer, float posX, float posY, float scaleX, float scaleY, boolean centered, boolean pinned) {
        this.layer = layer;
        this.quads = new Quad[text.length()];
        this.text = text;
        this.posX = posX;
        this.posY = posY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.centered = centered;
        this.pinned = pinned;
        for (int i = 0; i < quads.length; ++i) {
            quads[i] = new Quad(layer, BaseTextures.FONT, (pinned ? QUAD_FLAG_PINNED : 0) | QUAD_FLAG_FONT256);
        }
        applyText();
    }

    public void setText(String text) {
        this.text = text;
        if (text.length() > quads.length) {
            for (Quad quad : quads) {
                if (quad.isRegistered()) {
                    quad.unregister();
                }
            }
            quads = new Quad[text.length()];
            for (int i = 0; i < quads.length; ++i) {
                quads[i] = new Quad(layer, BaseTextures.FONT, (pinned ? QUAD_FLAG_PINNED : 0) | QUAD_FLAG_FONT256);
            }
        }
        for (int i = 0; i < text.length(); ++i) {
            if (isRegistered() && !quads[i].isRegistered()) {
                quads[i].register();
            }
        }
        for (int i = text.length(); i < quads.length; ++i) {
            if (quads[i].isRegistered()) {
                quads[i].unregister();
            }
        }
        applyText();
    }

    @Override
    public void register() {
        super.register();
        for (Quad quad : quads) {
            if (!quad.isRegistered()) {
                quad.register();
            }
        }
    }

    @Override
    public void unregister() {
        super.unregister();
        for (Quad quad : quads) {
            if (quad.isRegistered()) {
                quad.unregister();
            }
        }
    }

    private void applyText() {
        float scaleX = this.scaleX * textCompression;
        float offsetX = centered ? (1 - text.length()) * 0.5f : 1;
        float offsetY = centered ? 0 : -1;
        for (int i = 0; i < text.length(); ++i) {
            quads[i].x1 = posX + (offsetX - 1 + i) * scaleX;
            quads[i].y1 = posY + (offsetY + 1) * scaleY;
            quads[i].x2 = posX + (offsetX + 1 + i) * scaleX;
            quads[i].y2 = posY + (offsetY + 1) * scaleY;
            quads[i].x3 = posX + (offsetX + 1 + i) * scaleX;
            quads[i].y3 = posY + (offsetY - 1) * scaleY;
            quads[i].x4 = posX + (offsetX - 1 + i) * scaleX;
            quads[i].y4 = posY + (offsetY - 1) * scaleY;
            quads[i].metadata1 = text.charAt(i);
            if (quads[i].isRegistered()) {
                quads[i].updateBuffers();
            }
        }
    }

}
