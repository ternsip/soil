package com.ternsip.soil.game.entities;

import com.ternsip.soil.Soil;

import java.util.UUID;

public class Entity {

    public UUID uuid = null;

    public boolean isRegistered() {
        return uuid != null;
    }

    public void register() {
        Soil.THREADS.client.entityRepository.register(this);
        Soil.THREADS.client.eventIOReceiver.register(this);
    }

    public void unregister() {
        Soil.THREADS.client.entityRepository.unregister(uuid);
        Soil.THREADS.client.eventIOReceiver.unregister(this);
    }


}
