package com.ternsip.soil.network;

import com.ternsip.soil.Soil;
import com.ternsip.soil.general.Threadable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetworkServerAcceptor implements Threadable {

    @Override
    public void init() {}

    @Override
    public void update() {
        NetworkServer networkServer = Soil.THREADS.getNetworkServer();
        if (networkServer.isActive()) {
            try {
                networkServer.addConnection(networkServer.accept());
            } catch (Exception e) {
                if (networkServer.isActive()) {
                    String errMsg = String.format("Error while accepting new connection to server %s", e.getMessage());
                    log.error(errMsg);
                    log.debug(errMsg, e);
                }
            }
        }
    }

    @Override
    public void finish() {}

}