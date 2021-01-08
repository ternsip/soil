package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CharModsEvent implements Event {

    private final int codepoint;
    private final int mods;

}