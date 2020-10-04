package com.ternsip.soil.universe.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@RequiredArgsConstructor
@Getter
@Setter
public class Settings {

    private int viewDistance = 8;
    private int physicalTicksPerSecond = 128;
    private int networkTicksPerSecond = 20;

}
