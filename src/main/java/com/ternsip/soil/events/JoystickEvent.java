package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This is called when a joystick is connected to or disconnected from the system.
 */
@RequiredArgsConstructor
@Getter
public class JoystickEvent implements Event {

    private final int jid;
    private final int event;

}