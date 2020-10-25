package com.ternsip.soil.graph.display;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Texture {

    public final int atlasNumber;
    public final int layerStart;
    public final int layerEnd;
    public final float maxU;
    public final float maxV;

}