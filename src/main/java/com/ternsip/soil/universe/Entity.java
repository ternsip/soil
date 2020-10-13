package com.ternsip.soil.universe;

import com.ternsip.soil.Soil;

import java.util.UUID;

public class Entity {

    public UUID uuid = null;

    public boolean isRegistered() {
        return uuid != null;
    }

    public void register() {
        Soil.THREADS.getUniverseClient().entityRepository.register(this);
    }

    public void unregister() {
        Soil.THREADS.getUniverseClient().entityRepository.unregister(uuid);
    }


}
