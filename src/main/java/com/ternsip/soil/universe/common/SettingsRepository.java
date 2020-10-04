package com.ternsip.soil.universe.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class SettingsRepository {

    private int viewDistance = 8;
    private int physicalTicksPerSecond = 128;
    private int networkTicksPerSecond = 20;

}
