package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This is called when the window is moved.
 * The callback is provided with the position, in screen coordinates,
 * of the upper-left corner of the content area of the window.
 */
@RequiredArgsConstructor
@Getter
public class WindowPosEvent implements Event {

    private final int x;
    private final int y;

}