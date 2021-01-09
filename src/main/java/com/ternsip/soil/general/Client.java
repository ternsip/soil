package com.ternsip.soil.general;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Maths;
import com.ternsip.soil.common.Threadable;
import com.ternsip.soil.common.Timer;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.events.EventHook;
import com.ternsip.soil.events.EventReceiver;
import com.ternsip.soil.events.OnConnectedToServer;
import com.ternsip.soil.game.blocks.BlocksRepository;
import com.ternsip.soil.game.entities.EntityPlayer;
import com.ternsip.soil.game.entities.EntityQuad;
import com.ternsip.soil.game.entities.EntityRepository;
import com.ternsip.soil.game.entities.EntityStatistics;
import com.ternsip.soil.graph.display.*;
import com.ternsip.soil.graph.shader.Shader;
import com.ternsip.soil.graph.shader.TextureType;

import java.nio.ByteBuffer;
import java.util.Random;

import static com.ternsip.soil.game.blocks.BlocksRepository.SIZE_X;
import static com.ternsip.soil.game.blocks.BlocksRepository.SIZE_Y;

/**
 * Provides full control over user Input/Output channels
 * Uses OpenGL/OpenAl under the hood and maybe some other IO-libraries
 * In general words it is graphical representation of the universe state
 */
public class Client implements Threadable {

    public WindowData windowData;
    public EventReceiver eventReceiver;
    public Camera camera;
    public TextureRepository textureRepository;
    public Shader shader;
    public AudioRepository audioRepository;
    public FpsCounter fpsCounter;
    public Settings settings;
    public SoundRepository soundRepository;
    public EntityRepository entityRepository;
    public BlocksRepository blocksRepository;
    public Timer physicsClock;

    @Override
    public void init() {
        eventReceiver = new EventReceiver();
        windowData = new WindowData();
        camera = new Camera();
        textureRepository = new TextureRepository();
        shader = new Shader();
        fpsCounter = new FpsCounter();
        audioRepository = new AudioRepository();
        settings = new Settings();
        soundRepository = new SoundRepository();
        entityRepository = new EntityRepository();
        blocksRepository = new BlocksRepository();
        blocksRepository.init();
        blocksRepository.fullVisualUpdate();
        eventReceiver.registerWithSubObjects(this);
        physicsClock = new Timer(1000 / settings.physicalTicksPerSecond);
        new EntityStatistics().register();
        spawnMenu();
    }

    @Override
    // TODO think about sync order
    public void update() {
        windowData.clear();
        windowData.update();
        windowData.waitBuffer();
        fpsCounter.updateFps();
        eventReceiver.update();
        if (physicsClock.isOver()) {
            physicsClock.drop();
            blocksRepository.update();
            entityRepository.update();
        }
        shader.render();
        windowData.lockBuffer();
        windowData.swapBuffers();
        windowData.pollEvents();
        audioRepository.update();
    }

    @Override
    public void finish() {
        eventReceiver.unregisterWithSubObjects(this);
        shader.finish();
        textureRepository.finish();
        windowData.finish();
        audioRepository.finish();
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
        new EntityQuad(0, TextureType.BACKGROUND, true, 1000.0f, -1, -1, 1, -1, 1, 1, -1, 1, 0, 0).register();
        new EntityQuad(1, TextureType.PLAYER_IDLE, false, 1000.0f, 0, 0, 0.9f, 0, 0.9f, 0.5f, 0, 0.5f, 0, 0).register();
        new EntityQuad(1, TextureType.PLAYER_ATTACK, false, 5000.0f, -0.2f, 0.2f, 0, 0.2f, 0, 0, -0.2f, 0, 0, 0).register();
        new EntityQuad(1, TextureType.PLAYER_ATTACK, false, 5000.0f, -0.1f, 0.2f, 0, 0.2f, 0, 0, -0.2f, 0, 0, 0).register();
        //TODO
        //If you have a 800x600 screen, and a 2D quad over the whole screen, that 2D quad will have 480000 fragment shader calls, although it has only 4 vertexes.
        //Now, moving further, let's assume you have 10 such quads, on on top of another.
        //If you don't sort your geometry Front to Back or if you are using alpha blend with no depth test, then you will end up with 10x800x600 = 4800000 fragment calls.
        for (int i = 0; i < 100; ++i)
            new EntityQuad(1, TextureType.HOMER, false, 1000.0f, -0.4f, 0.2f, -0.2f, 0.2f, -0.2f, 0, -0.4f, 0, 0, 0).register();
        //new EntityQuad(1, TextureType.FONT, 1000.0f, -0.4f, 0.2f, -0.2f, 0.2f, -0.2f, 0, -0.4f, 0, 'c', 0).register();
        new EntityQuad(1, TextureType.TEST, false, 3000.0f, -0.8f, 0.2f, -0.4f, 0.2f, -0.4f, 0, -0.8f, 0, 0, 0).register();
        new EntityQuad(1, TextureType.KITTY, false, 1000.0f, -0.8f, 0.4f, -0.4f, 0.4f, -0.4f, 0.2f, -0.8f, 0.2f, 0, 0).register();
        new EntityQuad(2, TextureType.PLAYER_IDLE, false, 1000.0f, 0, 0, -0.9f, -0, -0.9f, -0.5f, 0, -0.5f, 0, 0).register();
        new EntityQuad(5, TextureType.OVERLAY, false, 1000.0f, -0.5f, -1, 0.5f, -1, 1, 1, -1, 1, 0, 0).register();
        new EntityQuad(6, TextureType.SOIL, false, 1000.0f, 0, 0, SIZE_X, 0, SIZE_X, SIZE_Y, 0, SIZE_Y, 0, 0).register();
        new EntityPlayer(1).register();
    }

    private void spawnEntities() {
    }

    @EventHook
    private void whenConnected(OnConnectedToServer onConnectedToServer) {
        spawnEntities();
    }

}