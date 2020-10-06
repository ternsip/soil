package com.ternsip.soil.graph.shader.base;

import com.ternsip.soil.common.logic.Utils;
import lombok.SneakyThrows;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.lwjgl.opengl.GL20.*;

public final class Shader {

    private static final File VERTEX_SHADER = new File("shaders/VertexShader.glsl");
    private static final File FRAGMENT_SHADER = new File("shaders/FragmentShader.glsl");

    private final int programID;

    private final Mesh mesh = new Mesh();

    // TODO finish all finishables, implement finishable
    private final BufferLayout blocksBuffer = new BufferLayout(512);
    private final BufferLayout blockTextureBuffer = new BufferLayout(128);

    public Shader() {
        int vertexShaderID = loadShader(VERTEX_SHADER, GL_VERTEX_SHADER);
        int fragmentShaderID = loadShader(FRAGMENT_SHADER, GL_FRAGMENT_SHADER);
        int programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        bindAttribute(programID, Mesh.INDICES);
        bindAttribute(programID, Mesh.VERTICES);
        glLinkProgram(programID);
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        locateInputs(programID);
        glValidateProgram(programID);
        this.programID = programID;
        glUseProgram(programID);
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

    private static void bindAttribute(int programID, AttributeData attributeData) {
        glBindAttribLocation(programID, attributeData.getIndex(), attributeData.getName());
    }

    public void render() {
        mesh.render(); // todo ensure it doesn't need  glUseProgram(0);
    }

    public void finish() {
        glUseProgram(0);
        mesh.finish();
        blocksBuffer.finish();
        blockTextureBuffer.finish();
        glDeleteProgram(programID);
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
