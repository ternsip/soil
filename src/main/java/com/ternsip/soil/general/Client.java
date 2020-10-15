package com.ternsip.soil.general;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.events.base.Callback;
import com.ternsip.soil.common.events.base.EventIOReceiver;
import com.ternsip.soil.common.events.network.OnConnectedToServer;
import com.ternsip.soil.graph.display.*;
import com.ternsip.soil.graph.shader.base.Shader;
import com.ternsip.soil.graph.shader.base.TextureType;
import com.ternsip.soil.universe.BlocksRepository;
import com.ternsip.soil.universe.EntityQuad;
import com.ternsip.soil.universe.EntityRepository;
import com.ternsip.soil.universe.EntityStatistics;
import com.ternsip.soil.universe.audio.SoundRepository;
import com.ternsip.soil.universe.bindings.Bind;
import com.ternsip.soil.universe.bindings.Bindings;
import com.ternsip.soil.universe.common.SettingsRepository;
import com.ternsip.soil.universe.protocol.ConsoleMessageServerPacket;

/**
 * Provides full control over user Input/Output channels
 * Uses OpenGL/OpenAl under the hood and maybe some other IO-libraries
 * In general words it is graphical representation of the universe state
 */
public class Client implements Threadable {

    // TODO make this callbacks automatic
    private final Callback<OnConnectedToServer> onConnectedToServerCallback = this::whenConnected;

    public WindowData windowData;
    public EventIOReceiver eventIOReceiver;
    public Camera camera;
    public TextureRepository textureRepository;
    public Shader shader;
    public AudioRepository audioRepository;
    public FpsCounter fpsCounter;

    public Bindings bindings; // TODO move bindings inside settings
    public SettingsRepository settingsRepository;
    public SoundRepository soundRepository;
    public EntityRepository entityRepository;
    public BlocksRepository blocksRepository;

    @Override
    public void init() {
        eventIOReceiver = new EventIOReceiver();
        windowData = new WindowData();
        camera = new Camera();
        textureRepository = new TextureRepository();
        shader = new Shader();
        fpsCounter = new FpsCounter();
        audioRepository = new AudioRepository();

        settingsRepository = new SettingsRepository();
        soundRepository = new SoundRepository();
        bindings = new Bindings();
        entityRepository = new EntityRepository();
        blocksRepository = new BlocksRepository();
        blocksRepository.init();
        eventIOReceiver.registerCallback(OnConnectedToServer.class, onConnectedToServerCallback);
        blocksRepository.fullVisualUpdate();
        new EntityStatistics().register();
        spawnMenu();
    }

    @Override
    public void update() {
        windowData.clear();
        //windowData.waitBuffer();
        eventIOReceiver.update();
        shader.render();
        fpsCounter.updateFps();
        entityRepository.update();
        eventIOReceiver.update();
        blocksRepository.update();
        //windowData.lockBuffer();
        windowData.swapBuffers();
        windowData.pollEvents();
        //audioRepository.update(); memory leaks
    }

    @Override
    public void finish() {
        shader.finish();
        textureRepository.finish();
        windowData.finish();
        audioRepository.finish();
        eventIOReceiver.unregisterCallback(OnConnectedToServer.class, onConnectedToServerCallback);
        bindings.finish();
        blocksRepository.finish();
    }

    public void startClient() {
        Soil.THREADS.getNetworkClient().connect("localhost", 6789);
    }

    public void stop() {
        // TODO Use main loop deactivator
        Soil.THREADS.getServer().stop();
        Soil.THREADS.getNetworkClient().stop();
    }

    private void spawnMenu() {
        new EntityQuad(0, TextureType.BACKGROUND, false, 1000.0f, -1, -1, 1, -1, 1, 1, -1, 1, 0, 0).register();
        new EntityQuad(1, TextureType.PLAYER_IDLE, false, 1000.0f, 0, 0, 0.9f, 0, 0.9f, 0.5f, 0, 0.5f, 0, 0).register();
        new EntityQuad(1, TextureType.PLAYER_ATTACK, false, 5000.0f, -0.2f, 0.2f, 0, 0.2f, 0, 0, -0.2f, 0, 0, 0).register();
        //TODO
        //If you have a 800x600 screen, and a 2D quad over the whole screen, that 2D quad will have 480000 fragment shader calls, although it has only 4 vertexes.
        //Now, moving further, let's assume you have 10 such quads, on on top of another.
        //If you don't sort your geometry Front to Back or if you are using alpha blend with no depth test, then you will end up with 10x800x600 = 4800000 fragment calls.
        for (int i = 0; i < 100; ++i)
            new EntityQuad(1, TextureType.HOMER, false, 1000.0f, -0.4f, 0.2f, -0.2f, 0.2f, -0.2f, 0, -0.4f, 0, 0, 0).register();
        //new EntityQuad(1, TextureType.FONT, 1000.0f, -0.4f, 0.2f, -0.2f, 0.2f, -0.2f, 0, -0.4f, 0, 'c', 0).register();
        new EntityQuad(1, TextureType.TEST, false, 1000.0f, -0.8f, 0.2f, -0.4f, 0.2f, -0.4f, 0, -0.8f, 0, 0, 0).register();
        new EntityQuad(1, TextureType.KITTY, false, 1000.0f, -0.8f, 0.4f, -0.4f, 0.4f, -0.4f, 0.2f, -0.8f, 0.2f, 0, 0).register();
        new EntityQuad(2, TextureType.PLAYER_IDLE, false, 1000.0f, 0, 0, -0.9f, -0, -0.9f, -0.5f, 0, -0.5f, 0, 0).register();
        new EntityQuad(3, TextureType.BLOCKS, true, 1000.0f, -1, -1, 1, -1, 1, 1, -1, 1, 0, 0).register();
    }

    private void spawnEntities() {
        bindings.addBindCallback(Bind.TEST_BUTTON, () -> Soil.THREADS.getNetworkClient().send(new ConsoleMessageServerPacket("HELLO 123")));
    }

    private void whenConnected(OnConnectedToServer onConnectedToServer) {
        spawnEntities();
    }

}