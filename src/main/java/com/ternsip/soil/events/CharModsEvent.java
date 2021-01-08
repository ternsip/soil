package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Tis called when a Unicode character is input regardless of what modifier keys are used.
 */
@RequiredArgsConstructor
@Getter
public class CharModsEvent implements Event {

    private final int codepoint;
    private final int mods;

}