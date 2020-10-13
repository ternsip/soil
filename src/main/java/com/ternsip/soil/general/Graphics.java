package com.ternsip.soil.general;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.events.base.EventIOReceiver;
import com.ternsip.soil.common.events.display.GraphicsReadyEvent;
import com.ternsip.soil.graph.display.*;
import com.ternsip.soil.graph.shader.base.Shader;

/**
 * Provides full control over user Input/Output channels
 * Uses OpenGL/OpenAl under the hood and maybe some other IO-libraries
 * In general words it is graphical representation of the universe state
 */
public class Graphics implements Threadable {

    public WindowData windowData;
    public EventIOReceiver eventIOReceiver;
    public Camera camera;
    public TextureRepository textureRepository;
    public Shader shader;
    public AudioRepository audioRepository;
    public FpsCounter fpsCounter;

    @Override
    public void init() {
        eventIOReceiver = new EventIOReceiver();
        windowData = new WindowData();
        camera = new Camera();
        textureRepository = new TextureRepository();
        shader = new Shader();
        fpsCounter = new FpsCounter();
        audioRepository = new AudioRepository();
        Soil.THREADS.getUniverseClient().eventIOReceiver.registerEvent(GraphicsReadyEvent.class, new GraphicsReadyEvent(shader, fpsCounter));
    }

    @Override
    public void update() {
        windowData.clear();
        eventIOReceiver.update();
        shader.render();
        fpsCounter.updateFps();
        windowData.draw();
        windowData.pollEvents();
        //audioRepository.update(); memory leaks
    }

    @Override
    public void finish() {
        shader.finish();
        textureRepository.finish();
        windowData.finish();
        audioRepository.finish();
    }

}