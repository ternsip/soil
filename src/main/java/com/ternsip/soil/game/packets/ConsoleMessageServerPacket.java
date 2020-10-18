package com.ternsip.soil.game.packets;

import com.ternsip.soil.network.Connection;
import com.ternsip.soil.network.ServerPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Getter
@Slf4j
public class ConsoleMessageServerPacket extends ServerPacket {

    private final String message;

    @Override
    public void apply(Connection connection) {
        log.info("Received message: " + message);
    }

}
