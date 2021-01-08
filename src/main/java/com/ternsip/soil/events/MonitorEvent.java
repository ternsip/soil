package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *  This is called when a monitor is connected to or disconnected from the system.
 */
@RequiredArgsConstructor
@Getter
public class MonitorEvent implements Event {

    private final long monitor;
    private final int event;

}