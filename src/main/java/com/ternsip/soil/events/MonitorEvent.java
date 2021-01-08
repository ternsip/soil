package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MonitorEvent implements Event {

    private final long monitor;
    private final int event;

}