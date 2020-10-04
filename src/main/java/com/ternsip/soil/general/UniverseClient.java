package com.ternsip.soil.general;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.events.base.Callback;
import com.ternsip.soil.common.events.base.EventIOReceiver;
import com.ternsip.soil.common.events.base.EventReceiver;
import com.ternsip.soil.common.events.network.OnConnectedToServer;
import com.ternsip.soil.universe.audio.SoundRepository;
import com.ternsip.soil.universe.bindings.Bind;
import com.ternsip.soil.universe.bindings.Bindings;
import com.ternsip.soil.universe.common.SettingsRepository;
import com.ternsip.soil.universe.protocol.ConsoleMessageServerPacket;
import lombok.SneakyThrows;

public class UniverseClient implements Threadable {

    private final Callback<OnConnectedToServer> onConnectedToServerCallback = this::whenConnected;
    public Bindings bindings; // TODO move bindings inside settings
    public SettingsRepository settingsRepository;
    public SoundRepository soundRepository;
    public EventIOReceiver eventIOReceiver;
    public EventReceiver networkEventReceiver;

    @Override
    public void init() {
        settingsRepository = new SettingsRepository();
        soundRepository = new SoundRepository();
        eventIOReceiver = new EventIOReceiver();
        bindings = new Bindings();
        networkEventReceiver = new EventReceiver();
        spawnMenu();
        networkEventReceiver.registerCallback(OnConnectedToServer.class, onConnectedToServerCallback);
    }

    @Override
    public void update() {
        eventIOReceiver.update();
        networkEventReceiver.update();
    }

    @SneakyThrows
    @Override
    public void finish() {
        networkEventReceiver.unregisterCallback(OnConnectedToServer.class, onConnectedToServerCallback);
        bindings.finish();
    }

    public void startClient() {
        Soil.THREADS.getNetworkClient().connect("localhost", 6789);
    }

    public void stop() {
        Soil.THREADS.getUniverseServer().stop();
        Soil.THREADS.getNetworkClient().stop();
    }

    private void spawnMenu() {
    }

    private void spawnEntities() {
        bindings.addBindCallback(Bind.TEST_BUTTON, () -> Soil.THREADS.getNetworkClient().send(new ConsoleMessageServerPacket("HELLO 123")));
    }

    private void whenConnected(OnConnectedToServer onConnectedToServer) {
        spawnEntities();
    }

}
