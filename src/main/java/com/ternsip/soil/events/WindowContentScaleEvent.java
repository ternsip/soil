package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WindowContentScaleEvent implements Event {

    private final float xScale;
    private final float yScale;

}