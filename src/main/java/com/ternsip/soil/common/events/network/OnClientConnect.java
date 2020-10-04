package com.ternsip.soil.common.events.network;

import com.ternsip.soil.common.events.base.Event;
import com.ternsip.soil.network.Connection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OnClientConnect implements Event {

    private final Connection connection;

}
