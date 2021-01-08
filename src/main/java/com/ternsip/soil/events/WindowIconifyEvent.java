package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This is called when the window is iconified or restored.
 */
@RequiredArgsConstructor
@Getter
public class WindowIconifyEvent implements Event {

    private final boolean iconified;

}