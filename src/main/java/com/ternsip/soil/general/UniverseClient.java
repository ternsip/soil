package com.ternsip.soil.general;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.events.base.Callback;
import com.ternsip.soil.common.events.base.EventIOReceiver;
import com.ternsip.soil.common.events.base.EventReceiver;
import com.ternsip.soil.common.events.network.OnConnectedToServer;
import com.ternsip.soil.universe.audio.SoundRepository;
import com.ternsip.soil.universe.bindings.Bind;
import com.ternsip.soil.universe.bindings.Bindings;
import com.ternsip.soil.universe.collisions.base.Collisions;
import com.ternsip.soil.universe.common.Settings;
import com.ternsip.soil.universe.protocol.ConsoleMessageServerPacket;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

@Getter
@Setter
public class UniverseClient implements Threadable {

    public Collisions collisions;
    public Bindings bindings; // TODO move bindings inside settings
    public Settings settings;
    public SoundRepository soundRepository;
    public EventIOReceiver eventIOReceiver;
    public EventReceiver networkEventReceiver;

    private final Callback<OnConnectedToServer> onConnectedToServerCallback = this::whenConnected;

    @Override
    public void init() {
        collisions = new Collisions();
        settings = new Settings();
        soundRepository = new SoundRepository();
        eventIOReceiver = new EventIOReceiver();
        bindings = new Bindings();
        networkEventReceiver = new EventReceiver();
        spawnMenu();
        networkEventReceiver.registerCallback(OnConnectedToServer.class, getOnConnectedToServerCallback());
    }

    @Override
    public void update() {
        eventIOReceiver.update();
        networkEventReceiver.update();
    }

    @SneakyThrows
    @Override
    public void finish() {
        networkEventReceiver.unregisterCallback(OnConnectedToServer.class, getOnConnectedToServerCallback());
        getBindings().finish();
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
        getBindings().addBindCallback(Bind.TEST_BUTTON, () -> Soil.THREADS.getNetworkClient().send(new ConsoleMessageServerPacket("HELLO 123")));
    }

    private void whenConnected(OnConnectedToServer onConnectedToServer) {
        spawnEntities();
    }

}
