package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This is called when the cursor enters or leaves the content area of the window.
 */
@RequiredArgsConstructor
@Getter
public class CursorEnterEvent implements Event {

    private final boolean entered;

}