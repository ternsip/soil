package com.ternsip.soil.general;

import com.ternsip.soil.common.events.base.EventIOReceiver;
import com.ternsip.soil.graph.camera.Camera;
import com.ternsip.soil.graph.display.AudioRepository;
import com.ternsip.soil.graph.display.FrameBuffers;
import com.ternsip.soil.graph.display.WindowData;
import com.ternsip.soil.graph.visual.repository.EffigyRepository;
import com.ternsip.soil.graph.visual.repository.ModelRepository;
import com.ternsip.soil.graph.visual.repository.ShaderRepository;
import com.ternsip.soil.graph.visual.repository.TextureRepository;

/**
 * Provides full control over user Input/Output channels
 * Uses OpenGL/OpenAl under the hood and maybe some other IO-libraries
 * In general words it is graphical representation of the universe state
 */
public class Graphics implements Threadable {

    public WindowData windowData;
    public EventIOReceiver eventIOReceiver;
    public FrameBuffers frameBuffers;
    public Camera camera;
    public TextureRepository textureRepository;
    public ModelRepository modelRepository;
    public ShaderRepository shaderRepository;
    public EffigyRepository effigyRepository;
    public AudioRepository audioRepository;

    @Override
    public void init() {
        eventIOReceiver = new EventIOReceiver();
        windowData = new WindowData();
        frameBuffers = new FrameBuffers();
        camera = new Camera();
        textureRepository = new TextureRepository();
        modelRepository = new ModelRepository();
        shaderRepository = new ShaderRepository();
        effigyRepository = new EffigyRepository();
        audioRepository = new AudioRepository();
    }

    @Override
    public void update() {
        frameBuffers.bindBuffer();
        windowData.clear();
        eventIOReceiver.update();
        effigyRepository.render();
        windowData.getFpsCounter().updateFps();
        frameBuffers.resolveBuffer();
        windowData.swapBuffers();
        windowData.pollEvents();
        audioRepository.update();
    }

    @Override
    public void finish() {
        modelRepository.finish();
        shaderRepository.finish();
        textureRepository.finish();
        windowData.finish();
        audioRepository.finish();
        effigyRepository.finish();
    }

}