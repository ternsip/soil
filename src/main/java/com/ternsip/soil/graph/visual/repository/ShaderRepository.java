package com.ternsip.soil.graph.visual.repository;

import com.ternsip.soil.graph.shader.base.Shader;

import java.util.HashMap;
import java.util.Map;

public class ShaderRepository {

    private final Map<Class<? extends Shader>, Shader> keyToShader = new HashMap<>();

    public void finish() {
        keyToShader.values().forEach(Shader::finish);
    }

    @SuppressWarnings("unchecked")
    public <T extends Shader> T getShader(Class<T> shaderClass) {
        return (T) keyToShader.computeIfAbsent(shaderClass, e -> Shader.createShader(shaderClass));
    }

}
