package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This is called when the window is resized.
 * The callback is provided with the size, in screen coordinates, of the content area of the window.
 */
@RequiredArgsConstructor
@Getter
public class WindowSizeEvent implements Event {

    private final int width;
    private final int height;

}