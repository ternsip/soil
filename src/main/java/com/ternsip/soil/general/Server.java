package com.ternsip.soil.general;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.events.base.EventReceiver;
import com.ternsip.soil.universe.common.SettingsRepository;
import lombok.SneakyThrows;

public class Server implements Threadable {

    public SettingsRepository settingsRepository;
    public EventReceiver networkEventReceiver;

    @Override
    public void init() {
        settingsRepository = new SettingsRepository();
        networkEventReceiver = new EventReceiver();
        spawnEntities();
    }

    @Override
    public void update() {
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
        Soil.THREADS.getServer().stop();
    }

    private void spawnEntities() {
    }

}
