package com.ternsip.soil.general;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.events.base.EventIOReceiver;
import com.ternsip.soil.common.events.display.ShaderRegisteredEvent;
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

    @Override
    public void init() {
        eventIOReceiver = new EventIOReceiver();
        windowData = new WindowData();
        camera = new Camera();
        textureRepository = new TextureRepository();
        shader = new Shader();
        Soil.THREADS.getUniverseClient().eventIOReceiver.registerEvent(ShaderRegisteredEvent.class, new ShaderRegisteredEvent(shader));
        audioRepository = new AudioRepository();
    }

    @Override
    public void update() {
        windowData.clear();
        eventIOReceiver.update();
        shader.render();
        windowData.getFpsCounter().updateFps();
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
    }

}