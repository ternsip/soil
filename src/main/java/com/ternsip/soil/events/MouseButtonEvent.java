package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This is called when a mouse button is pressed or released.
 */
@RequiredArgsConstructor
@Getter
public class MouseButtonEvent implements Event {

    private final int button;
    private final int action;
    private final int mods;

}