package com.ternsip.soil.universe.bindings;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class KeyState {

    private final int key;
    private final int action;
    private final int mods;

}
