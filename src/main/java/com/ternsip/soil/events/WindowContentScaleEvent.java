package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This is called when the content scale of the specified window changes.
 */
@RequiredArgsConstructor
@Getter
public class WindowContentScaleEvent implements Event {

    private final float xScale;
    private final float yScale;

}