package com.ternsip.soil.common.events.display;

import com.ternsip.soil.common.events.base.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResizeEvent implements Event {

    private final int width;
    private final int height;

}