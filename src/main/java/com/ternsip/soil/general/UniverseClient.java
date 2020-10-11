package com.ternsip.soil.general;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.events.base.Callback;
import com.ternsip.soil.common.events.base.EventIOReceiver;
import com.ternsip.soil.common.events.display.ShaderRegisteredEvent;
import com.ternsip.soil.common.events.network.OnConnectedToServer;
import com.ternsip.soil.graph.shader.base.TextureType;
import com.ternsip.soil.universe.BlocksRepository;
import com.ternsip.soil.universe.EntityQuad;
import com.ternsip.soil.universe.EntityRepository;
import com.ternsip.soil.universe.audio.SoundRepository;
import com.ternsip.soil.universe.bindings.Bind;
import com.ternsip.soil.universe.bindings.Bindings;
import com.ternsip.soil.universe.common.SettingsRepository;
import com.ternsip.soil.universe.protocol.ConsoleMessageServerPacket;
import lombok.SneakyThrows;

public class UniverseClient implements Threadable {

    // TODO make this callbacks automatic
    private final Callback<OnConnectedToServer> onConnectedToServerCallback = this::whenConnected;
    private final Callback<ShaderRegisteredEvent> registerShaderCallback = this::registerShader;
    public Bindings bindings; // TODO move bindings inside settings
    public SettingsRepository settingsRepository;
    public SoundRepository soundRepository;
    public EventIOReceiver eventIOReceiver;
    public EntityRepository entityRepository;
    public BlocksRepository blocksRepository;

    @Override
    public void init() {
        settingsRepository = new SettingsRepository();
        soundRepository = new SoundRepository();
        eventIOReceiver = new EventIOReceiver();
        bindings = new Bindings();
        entityRepository = new EntityRepository();
        blocksRepository = new BlocksRepository();
        blocksRepository.init();
        eventIOReceiver.registerCallback(OnConnectedToServer.class, onConnectedToServerCallback);
        eventIOReceiver.registerCallback(ShaderRegisteredEvent.class, registerShaderCallback);
    }

    @Override
    public void update() {
        eventIOReceiver.update();
        blocksRepository.update();
    }

    @SneakyThrows
    @Override
    public void finish() {
        eventIOReceiver.unregisterCallback(OnConnectedToServer.class, onConnectedToServerCallback);
        eventIOReceiver.unregisterCallback(ShaderRegisteredEvent.class, registerShaderCallback);
        bindings.finish();
        blocksRepository.finish();
    }

    public void startClient() {
        Soil.THREADS.getNetworkClient().connect("localhost", 6789);
    }

    public void stop() {
        Soil.THREADS.getUniverseServer().stop();
        Soil.THREADS.getNetworkClient().stop();
    }

    private void spawnMenu() {
        new EntityQuad(0, TextureType.BACKGROUND, 1000.0f, -1, -1, 1, -1, 1, 1, -1, 1).register();
        new EntityQuad(1, TextureType.PLAYER_IDLE, 1000.0f, 0, 0, 0.9f, 0, 0.9f, 0.5f, 0, 0.5f).register();
        new EntityQuad(1, TextureType.PLAYER_ATTACK, 5000.0f, -0.2f, 0.2f, 0, 0.2f, 0, 0, -0.2f, 0).register();
        new EntityQuad(1, TextureType.HOMER, 1000.0f, -0.4f, 0.2f, -0.2f, 0.2f, -0.2f, 0, -0.4f, 0).register();
        new EntityQuad(1, TextureType.TEST, 1000.0f, -0.8f, 0.2f, -0.4f, 0.2f, -0.4f, 0, -0.8f, 0).register();
        new EntityQuad(1, TextureType.KITTY, 1000.0f, -0.8f, 0.4f, -0.4f, 0.4f, -0.4f, 0.2f, -0.8f, 0.2f).register();
        new EntityQuad(2, TextureType.PLAYER_IDLE, 1000.0f, 0, 0, -0.9f, -0, -0.9f, -0.5f, 0, -0.5f).register();
    }

    private void spawnEntities() {
        bindings.addBindCallback(Bind.TEST_BUTTON, () -> Soil.THREADS.getNetworkClient().send(new ConsoleMessageServerPacket("HELLO 123")));
    }

    private void whenConnected(OnConnectedToServer onConnectedToServer) {
        spawnEntities();
    }

    private void registerShader(ShaderRegisteredEvent shaderRegisteredEvent) {
        EntityQuad.SHADER = shaderRegisteredEvent.getShader();
        BlocksRepository.SHADER = shaderRegisteredEvent.getShader();
        blocksRepository.fullVisualUpdate();
        spawnMenu();
    }

}
