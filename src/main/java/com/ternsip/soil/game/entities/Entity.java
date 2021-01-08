package com.ternsip.soil.game.entities;

import com.ternsip.soil.Soil;

import java.util.UUID;

public class Entity {

    public UUID uuid = null;

    public boolean isRegistered() {
        return uuid != null;
    }

    public void register() {
        uuid = Soil.THREADS.client.entityRepository.register(this);
        Soil.THREADS.client.eventReceiver.register(this);
    }

    public void unregister() {
        Soil.THREADS.client.entityRepository.unregister(uuid);
        uuid = null;
        Soil.THREADS.client.eventReceiver.unregister(this);
    }


}
