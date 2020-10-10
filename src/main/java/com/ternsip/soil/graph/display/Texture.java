package com.ternsip.soil.graph.display;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Texture {

    private final int atlasNumber;
    private final int layerStart;
    private final int layerEnd;
    private final float maxU;
    private final float maxV;

}