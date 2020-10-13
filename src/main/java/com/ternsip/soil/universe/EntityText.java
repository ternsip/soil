package com.ternsip.soil.universe;

import com.ternsip.soil.graph.shader.base.TextureType;

public class EntityText extends Entity {

    private final int layer;
    private EntityQuad[] quads;
    private String text;
    private boolean shiftX = false;
    private boolean shiftY = false;
    private float textCompression = 1f;
    private float posX;
    private float posY;
    private float scaleX;
    private float scaleY;

    public EntityText(String text, int layer, float posX, float posY, float scaleX, float scaleY) {
        this.layer = layer;
        this.quads = new EntityQuad[text.length()];
        this.text = text;
        for (int i = 0; i < quads.length; ++i) {
            quads[i] = new EntityQuad(layer, TextureType.FONT, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        applyText();
    }

    public void setText(String text) {
        this.text = text;
        EntityQuad[] newQuads = new EntityQuad[text.length()];
        for (int i = 0; i < quads.length; ++i) {
            if (i < newQuads.length) {
                newQuads[i] = quads[i];
            } else {
                quads[i].unregister();
            }
        }
        for (int i = quads.length; i < newQuads.length; ++i) {
            newQuads[i] = new EntityQuad(layer, TextureType.FONT, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        quads = newQuads;
        applyText();
    }

    private void applyText() {
        for (int i = 0; i < text.length(); ++i) {
            quads[i].x1 = (posX - 1 + i) * scaleX;
            quads[i].y1 = (posY - 1) * scaleY;
            quads[i].x2 = (posX + 1 + i) * scaleX;
            quads[i].y2 = (posY - 1) * scaleY;
            quads[i].x3 = (posX + 1 + i) * scaleX;
            quads[i].y3 = (posY + 1) * scaleY;
            quads[i].x4 = (posX - 1 + i) * scaleX;
            quads[i].y4 = (posY + 1) * scaleY;
            quads[i].metadataI = text.charAt(i);
            quads[i].writeToBufferLayout();
        }
    }

}
