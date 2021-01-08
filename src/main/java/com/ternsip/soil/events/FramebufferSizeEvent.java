package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This is called when the framebuffer of the specified window is resized.
 */
@RequiredArgsConstructor
@Getter
public class FramebufferSizeEvent implements Event {

    private final int width;
    private final int height;

}