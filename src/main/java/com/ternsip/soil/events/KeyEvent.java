package com.ternsip.soil.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class KeyEvent implements Event {

    private final int key;
    private final int scanCode;
    private final int action;
    private final int mods;

}