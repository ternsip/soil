package com.ternsip.soil.network;

import com.ternsip.soil.Soil;
import com.ternsip.soil.events.OnConnectToServer;
import com.ternsip.soil.events.OnDisconnectedFromServer;
import com.ternsip.soil.common.Threadable;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;

@Slf4j
@Getter
@Setter
public class NetworkClient implements Threadable {

    private final long RETRY_INTERVAL = 500L;
    private final int MAX_CONNECTION_ATTEMPTS = 10;

    private Connection connection = new Connection();

    public void connect(String host, int port) {
        for (int attempt = 0; attempt < MAX_CONNECTION_ATTEMPTS; ++attempt) {
            try {
                establishConnection(new Connection(new Socket(host, port)));
                return;
            } catch (Exception e) {
                String errMsg = String.format("Unable to connect to %s:%s, Attempt: #%s, Reason: %s retrying...", host, port, attempt, e.getMessage());
                log.error(errMsg);
                log.debug(errMsg, e);
                snooze();
            }
        }
        throw new IllegalArgumentException("Give up trying to connect!");
    }

    @Override
    public void init() {
    }

    @Override
    public void update() {
        if (getConnection().isActive()) {
            receive();
        } else {
            snooze();
        }
    }

    @Override
    public void finish() {}

    public synchronized void send(ServerPacket serverPacket) {
        try {
            getConnection().writeObject(serverPacket);
        } catch (SocketException | EOFException e) {
            handleTermination(e);
        } catch (Exception e) {
            String errMsg = String.format("Error while sending packet %s to server - %s", e.getClass().getSimpleName(), e.getMessage());
            log.error(errMsg);
            log.debug(errMsg, e);
        }
    }

    public void stop() {
        if (getConnection().isActive()) {
            disconnect();
        }
    }

    private void receive() {
        try {
            ClientPacket clientPacket = (ClientPacket) getConnection().readObject();
            clientPacket.apply(getConnection());
        } catch (SocketException | EOFException e) {
            handleTermination(e);
        } catch (Exception e) {
            String errMsg = String.format("Can not apply packet %s from server - %s", e.getClass().getSimpleName(), e.getMessage());
            log.error(errMsg);
            log.debug(errMsg, e);
        }
    }

    @SneakyThrows
    private void snooze() {
        Thread.sleep(RETRY_INTERVAL);
    }

    private void establishConnection(Connection connection) {
        setConnection(connection);
        Soil.THREADS.client.eventIOReceiver.registerEvent(new OnConnectToServer(connection));
    }

    private void disconnect() {
        if (getConnection().isActive()) {
            getConnection().close();
            Soil.THREADS.client.eventIOReceiver.registerEvent(new OnDisconnectedFromServer());
            log.info("Disconnected from server");
        } else {
            throw new IllegalArgumentException(String.format("Can not disconnect because connection %s is not active", getConnection()));
        }
    }

    private void handleTermination(Exception e) {
        if (getConnection().isActive()) {
            disconnect();
            log.info("Connection to server has been terminated", e);
        }
    }

}
