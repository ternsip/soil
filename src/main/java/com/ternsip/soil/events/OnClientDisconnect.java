package com.ternsip.soil.events;

import com.ternsip.soil.network.Connection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OnClientDisconnect implements Event {

    private final Connection connection;

}
