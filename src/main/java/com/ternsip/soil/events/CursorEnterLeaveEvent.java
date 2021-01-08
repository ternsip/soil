package com.ternsip.soil.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CursorEnterLeaveEvent implements Event {

    private final boolean entered;

}