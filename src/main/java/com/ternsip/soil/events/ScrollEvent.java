package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *  This is called when a scrolling device is used.
 */
@RequiredArgsConstructor
@Getter
public class ScrollEvent implements Event {

    private final double xOffset;
    private final double yOffset;

}