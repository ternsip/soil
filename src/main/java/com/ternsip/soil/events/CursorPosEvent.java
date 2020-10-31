package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CursorPosEvent implements Event {

    public final double x;
    public final double y;
    public final double dx;
    public final double dy;
    public final double normalX;
    public final double normalY;

}