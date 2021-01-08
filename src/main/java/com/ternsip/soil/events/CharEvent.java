package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This is called when a Unicode character is input.
 */
@RequiredArgsConstructor
@Getter
public class CharEvent implements Event {

    private final int unicodePoint;

}