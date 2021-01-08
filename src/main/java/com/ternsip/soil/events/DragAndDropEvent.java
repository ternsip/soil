package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *  Called when one or more dragged paths are dropped on the window.
 */
@RequiredArgsConstructor
@Getter
public class DragAndDropEvent implements Event {

    private final int count;
    private final String[] names;

}