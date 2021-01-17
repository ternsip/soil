package com.ternsip.soil.general;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Threadable;
import com.ternsip.soil.common.Timer;
import com.ternsip.soil.events.EventHook;
import com.ternsip.soil.events.EventReceiver;
import com.ternsip.soil.events.OnConnectedToServer;
import com.ternsip.soil.game.blocks.BlocksRepository;
import com.ternsip.soil.game.entities.EntityPlayer;
import com.ternsip.soil.game.entities.EntityRepository;
import com.ternsip.soil.game.entities.EntityStatistics;
import com.ternsip.soil.graph.display.*;
import com.ternsip.soil.graph.shader.Light;
import com.ternsip.soil.graph.shader.Quad;
import com.ternsip.soil.graph.shader.Shader;
import com.ternsip.soil.graph.shader.BaseTextures;

import java.util.Random;

import static com.ternsip.soil.game.blocks.BlocksRepository.SIZE_X;
import static com.ternsip.soil.game.blocks.BlocksRepository.SIZE_Y;
import static com.ternsip.soil.graph.shader.Shader.*;

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
        blocksRepository = new BlocksRepository();
        textureRepository = new TextureRepository();
        shader = new Shader();
        fpsCounter = new FpsCounter();
        audioRepository = new AudioRepository();
        settings = new Settings();
        soundRepository = new SoundRepository();
        entityRepository = new EntityRepository();
        blocksRepository.init();
        blocksRepository.fullVisualUpdate();
        eventReceiver.registerWithSubObjects(this);
        physicsClock = new Timer(1000 / settings.physicalTicksPerSecond);
        spawnMenu();
    }

    @Override
    // TODO think about sync order
    public void update() {
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
        EntityStatistics entityStatistics = new EntityStatistics();
        entityStatistics.register();
        new Quad(-1, BaseTextures.BACKGROUND, QUAD_FLAG_PINNED, 1000.0f, -1, -1, 1, -1, 1, 1, -1, 1, 0, 0).register();
        new Quad(7, BaseTextures.SHADOW, QUAD_FLAG_PINNED | QUAD_FLAG_SHADOW, 1000.0f, -1, -1, 1, -1, 1, 1, -1, 1, 0, 0).register();
        new Quad(1, BaseTextures.PLAYER_IDLE, 0, 1000.0f, 0, 0, 0.9f, 0, 0.9f, 0.5f, 0, 0.5f, 0, 0).register();
        new Quad(1, BaseTextures.PLAYER_ATTACK, 0, 5000.0f, -0.2f, 0.2f, 0, 0.2f, 0, 0, -0.2f, 0, 0, 0).register();
        new Quad(1, BaseTextures.PLAYER_ATTACK, 0, 5000.0f, -0.1f, 0.2f, 0, 0.2f, 0, 0, -0.2f, 0, 0, 0).register();
        for (int i = 0; i < 100; ++i)
            new Quad(1, BaseTextures.HOMER, 0, 1000.0f, -0.4f, 0.2f, -0.2f, 0.2f, -0.2f, 0, -0.4f, 0, 0, 0).register();
        new Quad(1, BaseTextures.TEST, 0, 3000.0f, -0.8f, 0.2f, -0.4f, 0.2f, -0.4f, 0, -0.8f, 0, 0, 0).register();
        new Quad(1, BaseTextures.KITTY, 0, 1000.0f, -0.8f, 0.4f, -0.4f, 0.4f, -0.4f, 0.2f, -0.8f, 0.2f, 0, 0).register();
        new Quad(2, BaseTextures.PLAYER_IDLE, 0, 1000.0f, 0, 0, -0.9f, -0, -0.9f, -0.5f, 0, -0.5f, 0, 0).register();
        Quad overlay = new Quad(5, BaseTextures.OVERLAY, 0, 1000.0f, -0.5f, -1, 0.5f, -1, 1, 1, -1, 1, 0, 0);
        new EntityPlayer(1).register();
        overlay.register();
        overlay.setLayer(0);
        overlay.setLayer(5);

        Random random = new Random(333);
        Quad[] e = new Quad[100];
        for (int i = 0; i < e.length; ++i) {
            e[i] = new Quad(Math.abs(random.nextInt()) % 25, BaseTextures.HOMER, 0, 1000.0f, -0.4f + i * 0.1f, 0.2f + i * 0.1f, -0.2f + i * 0.1f, 0.2f + i * 0.1f, -0.2f + i * 0.1f, 0 + i * 0.1f, -0.4f + i * 0.1f, +i * 0.1f, 0, 0);
            e[i].register();
            e[i].unregister();
            e[i].register();
        }
        for (int i = 0; i < 1500; i++) {
            int idx = random.nextInt(100);
            entityStatistics.unregister();
            if (random.nextBoolean()) {
                if (e[idx].isRegistered()) {
                    e[idx].unregister();
                } else {
                    e[idx].register();
                }
            }
            entityStatistics.register();
        }
        new Light(0, 0, 1, 3).register();
        new Light(1, 1, 0.5f, 1).register();

        for (int i = 0; i < 100; ++i) {
            new Light(2 + random.nextInt(25), 2 + random.nextInt(25), (float) (0.5f + random.nextDouble() * 2), 1).register();
        }
    }

    private void spawnEntities() {
    }

    @EventHook
    private void whenConnected(OnConnectedToServer onConnectedToServer) {
        spawnEntities();
    }

}