package com.ternsip.soil.graph.shader;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Finishable;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.graph.display.*;
import lombok.SneakyThrows;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static com.ternsip.soil.graph.display.WindowData.MAXIMUM_WINDOW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public final class Shader implements Finishable {

    public static final int MAX_MESHES = 16;
    public static final int MAX_QUADS = MAX_MESHES * Mesh.MAX_QUADS;
    public static final int MAX_LIGHTS = 8 * (1 << 16);
    public static final int UNASSIGNED_INDEX = -1;
    public static final int QUAD_FLAG_PINNED = 0x1;
    public static final int QUAD_FLAG_SHADOW = 0x2;
    public static final int QUAD_FLAG_FONT256 = 0x4;

    private static final File VERTEX_SHADER = new File("soil/shaders/VertexShader.glsl");
    private static final File FRAGMENT_SHADER = new File("soil/shaders/FragmentShader.glsl");
    private final BufferLayout textureBuffer;
    private final BufferLayout lightBuffer = new BufferLayout(MAX_LIGHTS, 4);
    private final BufferLayout quadBuffer = new BufferLayout(MAX_QUADS, 14);
    private final BufferLayout quadOrderBuffer = new BufferLayout(MAX_QUADS, 1);
    private final ArrayList<Light> lights = new ArrayList<>();
    private final ArrayList<Quad> quads = new ArrayList<>();
    private final ArrayList<Integer> quadOrder = new ArrayList<>();
    private final TreeMap<Integer, Integer> layerToQuadOrderOffset = new TreeMap<>(Collections.reverseOrder());
    private final int programID;
    private final FrameBuffer frameBuffer = new FrameBuffer(MAXIMUM_WINDOW.x, MAXIMUM_WINDOW.y); // TODO this should be resolved
    private final Mesh mesh = new Mesh();
    private final UniformVec2 cameraPos = new UniformVec2();
    private final UniformVec2 cameraScale = new UniformVec2();
    private final UniformVec2 aspect = new UniformVec2();
    private final UniformInteger meshIndex = new UniformInteger();
    private final UniformInteger time = new UniformInteger();
    private final UniformBoolean debugging = new UniformBoolean();
    private final UniformBoolean processingLight = new UniformBoolean();
    private final UniformSampler shadowTexture = new UniformSampler();
    private final UniformSamplers2DArray samplers = new UniformSamplers2DArray(TextureRepository.ATLAS_RESOLUTIONS.length);

    public Shader() {
        this.textureBuffer = new BufferLayout(Soil.THREADS.client.textureRepository.fileToTexture.size(), 5);
        int vertexShaderID = loadShader(VERTEX_SHADER, GL_VERTEX_SHADER);
        int fragmentShaderID = loadShader(FRAGMENT_SHADER, GL_FRAGMENT_SHADER);
        int programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        glLinkProgram(programID);
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        locateInputs(programID);
        glValidateProgram(programID);
        this.programID = programID;
        glUseProgram(programID);
        loadDefaultData();
        glClearColor(0, 0, 0, 0);
        GLInfo.logAttributeInfo(programID);
    }

    private static int loadShader(File file, int type) {
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, new String(Utils.loadResourceAsByteArray(file)));
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String error = glGetShaderInfoLog(shaderID, 1024);
            throw new IllegalArgumentException(String.format("Could not compile shader %s - %s", file.getName(), error));
        }
        return shaderID;
    }

    public void render() {
        Camera camera = Soil.THREADS.client.camera;
        cameraPos.load(camera.pos.x, camera.pos.y);
        cameraScale.load(camera.scale.x, camera.scale.y);
        aspect.load(camera.aspectX, camera.aspectY);
        //debugging.load(camera.scale.x < 0.01 || camera.scale.y < 0.01);
        debugging.load(false);
        time.load((int) (System.currentTimeMillis() % Integer.MAX_VALUE));

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer.fbo);
        glClear(GL_COLOR_BUFFER_BIT);
        processingLight.load(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        int lightsToRender = lights.size();
        for (int mshIdx = 0; lightsToRender > 0; ++mshIdx) {
            int lights = Math.min(Mesh.MAX_QUADS, lightsToRender);
            meshIndex.load(mshIdx);
            mesh.render(lights);
            lightsToRender -= lights;
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClear(GL_COLOR_BUFFER_BIT);
        processingLight.load(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        int quadsToRender = quads.size();
        for (int mshIdx = 0; quadsToRender > 0; ++mshIdx) {
            int quads = Math.min(Mesh.MAX_QUADS, quadsToRender);
            meshIndex.load(mshIdx);
            mesh.render(quads);
            quadsToRender -= quads;
        }

    }

    public void finish() {
        glUseProgram(0);
        finishInputs();
        glDeleteProgram(programID);
    }

    public void register(Quad quad) {
        if (quad.index != UNASSIGNED_INDEX) {
            throw new IllegalArgumentException("Quad is already registered");
        }
        if (quads.size() >= MAX_QUADS) {
            throw new IllegalArgumentException("Trying to register too much quads");
        }
        int lastIndex = quads.size();
        quads.add(quad);
        quadOrder.add(lastIndex);
        setQuadOrder(lastIndex, lastIndex);
        int orderPointer = lastIndex;
        for (Map.Entry<Integer, Integer> entry : layerToQuadOrderOffset.entrySet()) {
            int entryLayer = entry.getKey();
            int layerStart = entry.getValue();
            if (entryLayer > quad.layer) {
                setQuadOrder(orderPointer, quadOrder.get(layerStart));
                setQuadOrder(layerStart, lastIndex);
                orderPointer = layerStart;
                entry.setValue(layerStart + 1);
            } else {
                break;
            }
        }
        if (!layerToQuadOrderOffset.containsKey(quad.layer)) {
            layerToQuadOrderOffset.put(quad.layer, orderPointer);
        }
        quad.index = lastIndex;
        quad.orderIndex = orderPointer;
        updateBuffers(quad);
    }

    public void unregister(Quad quad) {
        if (quad.index == UNASSIGNED_INDEX) {
            throw new IllegalArgumentException("Quad is not registered yet to be unregistered");
        }
        int lastIndex = quads.size() - 1;
        int initialQuadOrderIndex = quad.orderIndex;
        Quad lastQuad = quads.get(lastIndex);
        setQuadOrder(lastQuad.orderIndex, quad.index);
        lastQuad.index = quad.index;
        quads.set(quad.index, lastQuad);
        updateBuffers(lastQuad);
        int orderValuePointer = quadOrder.get(lastIndex);
        int orderIndexPointer = lastIndex;
        boolean layerDestroyed = false;
        for (Map.Entry<Integer, Integer> entry : layerToQuadOrderOffset.entrySet()) {
            int entryLayer = entry.getKey();
            int layerStart = entry.getValue();
            if (entryLayer > quad.layer) {
                int beforeLayer = layerStart - 1;
                int oldOrderValue = quadOrder.get(beforeLayer);
                setQuadOrder(beforeLayer, orderValuePointer);
                entry.setValue(beforeLayer);
                orderValuePointer = oldOrderValue;
                orderIndexPointer = beforeLayer;
            } else {
                if (orderIndexPointer == layerStart) {
                    layerDestroyed = true;
                } else if (initialQuadOrderIndex != orderIndexPointer) {
                    setQuadOrder(initialQuadOrderIndex, orderValuePointer);
                }
                break;
            }
        }
        if (layerDestroyed) {
            layerToQuadOrderOffset.remove(quad.layer);
        }
        quads.remove(lastIndex);
        quadOrder.remove(lastIndex);
        quad.index = UNASSIGNED_INDEX;
        quad.orderIndex = UNASSIGNED_INDEX;
    }

    public void updateBuffers(Quad quad) {
        if (quad.index == UNASSIGNED_INDEX) {
            throw new IllegalArgumentException("Quad is not registered yet");
        }
        int quadOffset = quad.index * quadBuffer.structureLength;
        quadBuffer.writeInt(quadOffset, quad.textureIndex);
        quadBuffer.writeInt(quadOffset + 1, quad.animationStart);
        quadBuffer.writeFloat(quadOffset + 2, quad.animationPeriod);
        quadBuffer.writeInt(quadOffset + 3, quad.flags);
        quadBuffer.writeInt(quadOffset + 4, quad.metadata1);
        quadBuffer.writeInt(quadOffset + 5, quad.metadata2);
        quadBuffer.writeFloat(quadOffset + 6, quad.x1);
        quadBuffer.writeFloat(quadOffset + 7, quad.y1);
        quadBuffer.writeFloat(quadOffset + 8, quad.x2);
        quadBuffer.writeFloat(quadOffset + 9, quad.y2);
        quadBuffer.writeFloat(quadOffset + 10, quad.x3);
        quadBuffer.writeFloat(quadOffset + 11, quad.y3);
        quadBuffer.writeFloat(quadOffset + 12, quad.x4);
        quadBuffer.writeFloat(quadOffset + 13, quad.y4);
    }

    public void register(Light light) {
        if (light.index != UNASSIGNED_INDEX) {
            throw new IllegalArgumentException("Light is already registered");
        }
        if (lights.size() >= MAX_LIGHTS) {
            throw new IllegalArgumentException("Trying to register too much lights");
        }
        light.index = lights.size();
        lights.add(light);
        updateBuffers(light);
    }

    public void unregister(Light light) {
        if (light.index == UNASSIGNED_INDEX) {
            throw new IllegalArgumentException("Light is not registered yet");
        }
        int lastElement = lights.size() - 1;
        Light lastLight = lights.get(lastElement);
        if (light.index < lastElement) {
            lights.set(light.index, lastLight);
            lastLight.index = light.index;
            updateBuffers(lastLight);
        }
        light.index = UNASSIGNED_INDEX;
        lights.remove(lastElement);
    }

    public void updateBuffers(Light light) {
        if (light.index == UNASSIGNED_INDEX) {
            throw new IllegalArgumentException("Light is not registered yet");
        }
        int offset = light.index * lightBuffer.structureLength;
        lightBuffer.writeFloat(offset, light.x);
        lightBuffer.writeFloat(offset + 1, light.y);
        lightBuffer.writeFloat(offset + 2, light.radius);
        lightBuffer.writeFloat(offset + 3, light.power);
    }

    private void setQuadOrder(int quadOrderedIndex, int quadRealIndex) {
        quadOrder.set(quadOrderedIndex, quadRealIndex);
        quads.get(quadRealIndex).orderIndex = quadOrderedIndex;
        quadOrderBuffer.writeInt(quadOrderedIndex, quadRealIndex);
    }

    private void loadDefaultData() {
        TextureRepository textureRepository = Soil.THREADS.client.textureRepository;
        for (Texture texture : textureRepository.fileToTexture.values()) {
            int index = texture.index * textureBuffer.structureLength;
            textureBuffer.writeInt(index, texture.layerStart);
            textureBuffer.writeInt(index + 1, texture.layerEnd);
            textureBuffer.writeInt(index + 2, texture.atlasNumber);
            textureBuffer.writeFloat(index + 3, texture.maxU);
            textureBuffer.writeFloat(index + 4, texture.maxV);
        }
        Texture2D[] textures = textureRepository.atlases;
        for (int i = 0; i < textures.length; ++i) {
            samplers.load(i, textures[i].activationId);
        }
        this.shadowTexture.load(frameBuffer.mainTexture.activationId);
    }

    @SneakyThrows
    private void finishInputs() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                Object object = field.get(this);
                if (object instanceof Finishable) {
                    Finishable finishable = (Finishable) object;
                    finishable.finish();
                }
            }
        }
    }

    @SneakyThrows
    private void locateInputs(int programID) {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                Object object = field.get(this);
                if (object instanceof Locatable) {
                    Locatable locatable = (Locatable) object;
                    String fieldName = field.getName();
                    locatable.locate(programID, fieldName);
                }
            }
        }
    }

}
