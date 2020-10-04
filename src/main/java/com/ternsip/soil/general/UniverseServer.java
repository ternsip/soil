package com.ternsip.soil.general;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.events.base.EventReceiver;
import com.ternsip.soil.universe.collisions.base.Collisions;
import com.ternsip.soil.universe.common.Settings;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

@Getter
@Setter
public class UniverseServer implements Threadable {

    public Collisions collisions;
    public Settings settings;
    public EventReceiver networkEventReceiver;

    @Override
    public void init() {
        collisions = new Collisions();
        settings = new Settings();
        networkEventReceiver = new EventReceiver();
        spawnEntities();
    }

    @Override
    public void update() {
        collisions.update();
        networkEventReceiver.update();
    }

    @SneakyThrows
    @Override
    public void finish() {
    }

    public void startServer() {
        Soil.THREADS.getNetworkServer().bind(6789);
    }

    public void stop() {
        Soil.THREADS.getUniverseServer().stop();
    }

    private void spawnEntities() {
    }

}
