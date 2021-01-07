package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WindowMaximizeEvent implements Event {

    private final boolean maximized;

}