package com.ternsip.soil.common.events.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ErrorEvent implements Event {

    private final int error;
    private final long description;

}