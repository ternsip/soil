package com.ternsip.soil.game.entities;

import com.ternsip.soil.graph.display.Quad;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntitySprite extends Entity {

    private final Quad quad;

    @Override
    public void register() {
        super.register();
        quad.register();
    }

    @Override
    public void unregister() {
        super.unregister();
        quad.unregister();
    }

}
