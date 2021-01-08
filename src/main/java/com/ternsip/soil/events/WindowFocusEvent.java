package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This is called when the window gains or loses input focus.
 */
@RequiredArgsConstructor
@Getter
public class WindowFocusEvent implements Event {

    private final boolean focused;

}