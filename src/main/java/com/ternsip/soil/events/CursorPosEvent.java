package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CursorPosEvent implements Event {

    private final double x;
    private final double y;
    private final double dx;
    private final double dy;
    private final double normalX;
    private final double normalY;

}