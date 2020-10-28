package com.ternsip.soil.game.entities;

import com.ternsip.soil.common.Updatable;
import com.ternsip.soil.graph.shader.TextureType;

public class EntityPlayer extends Entity implements Updatable {

    private final int layer;
    private final EntityQuad body;

    public EntityPlayer(int layer) {
        this.layer = layer;
        this.body = new EntityQuad(layer, TextureType.PLAYER_IDLE, false);
    }

    @Override
    public void register() {
        super.register();
        body.register();
    }

    @Override
    public void unregister() {
        super.unregister();
        body.unregister();
    }

    @Override
    public void update() {

    }
}
