package com.ternsip.soil.graph.shader.base;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.logic.Finishable;
import com.ternsip.soil.common.logic.Utils;
import com.ternsip.soil.graph.display.Texture;
import com.ternsip.soil.graph.display.TextureRepository;
import com.ternsip.soil.graph.shader.uniforms.UniformInteger;
import com.ternsip.soil.universe.EntityQuad;
import lombok.SneakyThrows;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.lwjgl.opengl.GL20.*;

public final class Shader implements Finishable {

    public static final int MAX_LAYERS = 16;
    public static final int TEXTURE_BUFFER_CELL_SIZE = 4;
    public static final int QUAD_BUFFER_CELL_SIZE = 1;
    public static final int VERTEX_BUFFER_CELL_SIZE = 2;
    public static final int QUAD_BUFFER_SIZE = Mesh.MAX_QUADS * QUAD_BUFFER_CELL_SIZE;
    public static final int VERTEX_BUFFER_SIZE = Mesh.MAX_QUADS * Mesh.QUAD_VERTICES * VERTEX_BUFFER_CELL_SIZE;

    private static final File VERTEX_SHADER = new File("shaders/VertexShader.glsl");
    private static final File FRAGMENT_SHADER = new File("shaders/FragmentShader.glsl");

    private final int programID;

    private final Mesh mesh = new Mesh();

    private final UniformInteger layer = new UniformInteger();

    public final BufferLayout blocksBuffer = new BufferLayout(512);
    public final BufferLayout textureBuffer = new BufferLayout(TextureType.values().length * TEXTURE_BUFFER_CELL_SIZE);
    public final BufferLayout quadBuffer = new BufferLayout(MAX_LAYERS * QUAD_BUFFER_SIZE);
    public final BufferLayout vertexBuffer = new BufferLayout(MAX_LAYERS * VERTEX_BUFFER_SIZE);

    public Shader() {
        int vertexShaderID = loadShader(VERTEX_SHADER, GL_VERTEX_SHADER);
        int fragmentShaderID = loadShader(FRAGMENT_SHADER, GL_FRAGMENT_SHADER);
        int programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        glBindAttribLocation(programID, Mesh.INDICES_ATTRIBUTE_POINTER,  "index"); // TODO not sure if this needed, use gl_VertexID
        glLinkProgram(programID);
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        locateInputs(programID);
        glValidateProgram(programID);
        this.programID = programID;
        glUseProgram(programID);
        fillStaticBuffers();
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
        for (int layerIndex = 0; layerIndex < MAX_LAYERS; ++layerIndex) {
            int quads = EntityQuad.getCountThreadSafe(layerIndex);
            if (quads <= 0) {
                continue;
            }
            this.layer.load(layerIndex);
            quadBuffer.writeToGpu(layerIndex * QUAD_BUFFER_SIZE, layerIndex * QUAD_BUFFER_SIZE + quads);
            vertexBuffer.writeToGpu(layerIndex * VERTEX_BUFFER_SIZE, layerIndex * VERTEX_BUFFER_SIZE + quads * Mesh.QUAD_VERTICES);
            mesh.render(quads);
        }
        // todo ensure it doesn't need  glUseProgram(0); each call
    }

    public void finish() {
        glUseProgram(0);
        finishInputs();
        glDeleteProgram(programID);
    }

    private void fillStaticBuffers() {
        int index = 0;
        TextureRepository textureRepository = Soil.THREADS.getGraphics().textureRepository;
        for (TextureType textureType : TextureType.values()) {
            Texture texture = textureRepository.getTexture(textureType.file);
            textureBuffer.writeInt(index, texture.getLayer());
            textureBuffer.writeInt(index + 1, texture.getAtlasNumber());
            textureBuffer.writeFloat(index + 2, texture.getMaxU());
            textureBuffer.writeFloat(index + 3, texture.getMaxV());
            index += TEXTURE_BUFFER_CELL_SIZE;
        }
        textureBuffer.writeToGpu(0, TextureType.values().length * TEXTURE_BUFFER_CELL_SIZE);
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
