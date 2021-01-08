package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *  This is called when the window is maximized or restored.
 */
@RequiredArgsConstructor
@Getter
public class WindowMaximizeEvent implements Event {

    private final boolean maximized;

}