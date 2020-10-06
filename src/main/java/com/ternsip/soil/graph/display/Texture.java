package com.ternsip.soil.graph.display;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2f;

@RequiredArgsConstructor
@Getter
public class Texture {

    private final int atlasNumber;
    private final int layer;
    private final Vector2f maxUV;

}