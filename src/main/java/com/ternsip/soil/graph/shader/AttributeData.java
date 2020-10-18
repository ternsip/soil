package com.ternsip.soil.graph.shader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@RequiredArgsConstructor
@Getter
public class AttributeData {

    private final int index;
    private final String name;
    private final int numberPerVertex;

}