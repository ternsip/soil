package com.ternsip.soil.universe;

import com.ternsip.soil.graph.shader.TextureType;

public class EntityText extends Entity {

    private final int layer;
    private EntityQuad[] quads;
    private String text;
    private boolean centered;
    private boolean pinned;
    private float textCompression = 0.8f;
    private float posX;
    private float posY;
    private float scaleX;
    private float scaleY;

    public EntityText(String text, int layer, float posX, float posY, float scaleX, float scaleY, boolean centered, boolean pinned) {
        this.layer = layer;
        this.quads = new EntityQuad[text.length()];
        this.text = text;
        this.posX = posX;
        this.posY = posY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.centered = centered;
        this.pinned = pinned;
        for (int i = 0; i < quads.length; ++i) {
            quads[i] = new EntityQuad(layer, TextureType.FONT, pinned);
        }
        applyText();
    }

    public void setText(String text) {
        this.text = text;
        if (text.length() > quads.length) {
            for (EntityQuad quad : quads) {
                if (quad.isRegistered()) {
                    quad.unregister();
                }
            }
            quads = new EntityQuad[text.length()];
            for (int i = 0; i < quads.length; ++i) {
                quads[i] = new EntityQuad(layer, TextureType.FONT, pinned);
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
        for (EntityQuad quad : quads) {
            quad.register();
        }
    }

    @Override
    public void unregister() {
        super.unregister();
        for (EntityQuad quad : quads) {
            quad.register();
        }
    }

    private void applyText() {
        float offsetX = centered ? (1 - text.length()) * 0.5f * textCompression : 0.5f;
        float offsetY = centered ? 0 : -0.5f;
        for (int i = 0; i < text.length(); ++i) {
            quads[i].x1 = posX + (offsetX - 1 + i * textCompression) * scaleX;
            quads[i].y1 = posY + (offsetY + 1) * scaleY;
            quads[i].x2 = posX + (offsetX + 1 + i * textCompression) * scaleX;
            quads[i].y2 = posY + (offsetY + 1) * scaleY;
            quads[i].x3 = posX + (offsetX + 1 + i * textCompression) * scaleX;
            quads[i].y3 = posY + (offsetY - 1) * scaleY;
            quads[i].x4 = posX + (offsetX - 1 + i * textCompression) * scaleX;
            quads[i].y4 = posY + (offsetY - 1) * scaleY;
            quads[i].metadata1 = text.charAt(i);
            if (quads[i].isRegistered()) {
                quads[i].writeToBufferLayout();
            }
        }
    }

}
