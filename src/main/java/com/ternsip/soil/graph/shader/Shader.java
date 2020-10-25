package com.ternsip.soil.graph.shader;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Finishable;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.graph.display.Camera;
import com.ternsip.soil.graph.display.Texture;
import com.ternsip.soil.graph.display.TextureRepository;
import com.ternsip.soil.game.blocks.BlocksRepository;
import com.ternsip.soil.game.entities.EntityQuad;
import lombok.SneakyThrows;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.lwjgl.opengl.GL20.*;

public final class Shader implements Finishable {

    public static final int MAX_LAYERS = 16;

    private static final File VERTEX_SHADER = new File("shaders/VertexShader.glsl");
    private static final File FRAGMENT_SHADER = new File("shaders/FragmentShader.glsl");

    private final int programID;

    private final Mesh mesh = new Mesh();

    private final UniformVec2 cameraPos = new UniformVec2();
    private final UniformVec2 cameraScale = new UniformVec2();
    private final UniformVec2 aspect = new UniformVec2();
    private final UniformInteger layer = new UniformInteger();
    private final UniformInteger time = new UniformInteger();
    private final UniformBoolean debugging = new UniformBoolean();
    private final UniformSamplers2DArray samplers = new UniformSamplers2DArray(TextureRepository.ATLAS_RESOLUTIONS.length);

    public final BufferLayout blocksBuffer = new BufferLayout(BlocksRepository.SIZE_X * BlocksRepository.SIZE_Y, 3);
    public final BufferLayout textureBuffer = new BufferLayout(TextureType.values().length,  6);
    public final BufferLayout quadBuffer = new BufferLayout(MAX_LAYERS * Mesh.MAX_QUADS, 14);

    public Shader() {
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
        debugging.load(camera.scale.x < 0.01 || camera.scale.y < 0.01);
        //debugging.load(true);
        this.time.load((int) (System.currentTimeMillis() % Integer.MAX_VALUE));
        for (int layerIndex = 0; layerIndex < MAX_LAYERS; ++layerIndex) {
            int quads = EntityQuad.getCount(layerIndex);
            if (quads <= 0) {
                continue;
            }
            this.layer.load(layerIndex);
            mesh.render(quads);
        }
    }

    public void finish() {
        glUseProgram(0);
        finishInputs();
        glDeleteProgram(programID);
    }

    private void loadDefaultData() {
        int index = 0;
        TextureRepository textureRepository = Soil.THREADS.client.textureRepository;
        for (TextureType textureType : TextureType.values()) {
            Texture texture = textureRepository.getTexture(textureType.file);
            textureBuffer.writeInt(index, texture.layerStart);
            textureBuffer.writeInt(index + 1, texture.layerEnd);
            textureBuffer.writeInt(index + 2, texture.atlasNumber);
            textureBuffer.writeFloat(index + 3, texture.maxU);
            textureBuffer.writeFloat(index + 4, texture.maxV);
            textureBuffer.writeInt(index + 5, textureType.textureStyle.ordinal());
            index += textureBuffer.structureLength;
        }
        samplers.loadDefault();
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
