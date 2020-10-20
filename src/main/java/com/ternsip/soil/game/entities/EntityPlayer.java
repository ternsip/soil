package com.ternsip.soil.game.entities;

import com.ternsip.soil.common.Updatable;
import com.ternsip.soil.graph.shader.TextureType;

public class EntityPlayer extends Entity implements Updatable {

    private final int layer;
    private final EntityQuad head;


    public EntityPlayer(int layer) {
        this.layer = layer;
        this.head = new EntityQuad(layer, TextureType.FONT, false);
    }

    @Override
    public void register() {
        super.register();
        head.register();
    }

    @Override
    public void unregister() {
        super.unregister();
        head.unregister();
    }

    @Override
    public void update() {

    }
}
