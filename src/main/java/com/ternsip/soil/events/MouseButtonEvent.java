package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MouseButtonEvent implements Event {

    private final int button;
    private final int action;
    private final int mods;

}