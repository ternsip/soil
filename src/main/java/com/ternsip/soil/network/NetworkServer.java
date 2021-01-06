package com.ternsip.soil.network;

import com.ternsip.soil.Soil;
import com.ternsip.soil.common.Threadable;
import com.ternsip.soil.events.OnClientConnect;
import com.ternsip.soil.events.OnClientDisconnect;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Getter
@Setter
public class NetworkServer implements Threadable {

    private final long RETRY_INTERVAL = 500L;
    private final Set<Connection> connections = ConcurrentHashMap.newKeySet();
    private ServerSocket socket = null;

    @SneakyThrows
    public void bind(int port) {
        socket = new ServerSocket(port);
    }

    public boolean isActive() {
        return socket != null && !socket.isClosed();
    }

    @SneakyThrows
    public Connection accept() {
        return new Connection(socket.accept());
    }

    @Override
    public void init() {
    }

    @Override
    public void update() {
        if (isActive()) {
            removeInactiveConnections();
            getConnections().forEach(this::receive);
        } else {
            snooze();
        }
    }

    @Override
    public void finish() {}

    @SneakyThrows
    public void stop() {
        getConnections().forEach(this::disconnectClient);
        if (isActive()) {
            socket.close();
        }
    }

    public synchronized void send(ClientPacket clientPacket, Function<Connection, Boolean> connectionCondition) {
        getConnections().forEach(connection -> {
            if (connectionCondition.apply(connection)) {
                send(clientPacket, connection);
            }
        });
    }

    public synchronized void send(ClientPacket clientPacket, Connection connection) {
        try {
            connection.writeObject(clientPacket);
        } catch (SocketException | EOFException e) {
            handleClientTermination(connection, e);
        } catch (Exception e) {
            String errMsg = String.format("Error while sending packet %s packet to client (%s) - %s", e.getClass().getSimpleName(), connection, e.getMessage());
            log.error(errMsg);
            log.debug(errMsg, e);
        }
    }

    public void addConnection(Connection connection) {
        getConnections().add(connection);
        Soil.THREADS.getServer().networkEventReceiver.registerEvent(new OnClientConnect(connection));
    }

    private void removeInactiveConnections() {
        getConnections().removeIf(connection -> {
            if (!connection.isActive()) {
                Soil.THREADS.getServer().networkEventReceiver.registerEvent(new OnClientDisconnect(connection));
                return true;
            }
            return false;
        });
    }

    private void receive(Connection connection) {
        try {
            if (connection.isAvailable()) {
                ServerPacket serverPacket = (ServerPacket) connection.readObject();
                serverPacket.apply(connection);
            }
        } catch (SocketException | EOFException e) {
            handleClientTermination(connection, e);
        } catch (Exception e) {
            String errMsg = String.format("Can not apply %s packet from client (%s) - %s", e.getClass().getSimpleName(), connection, e.getMessage());
            log.error(errMsg);
            log.debug(errMsg, e);
        }
    }

    @SneakyThrows
    private void snooze() {
        Thread.sleep(RETRY_INTERVAL);
    }

    private void disconnectClient(Connection connection) {
        if (connection.isActive()) {
            connection.close();
            Soil.THREADS.getServer().networkEventReceiver.registerEvent(new OnClientDisconnect(connection));
            getConnections().remove(connection);
            log.info(String.format("Client %s has been disconnected", connection));
        } else {
            throw new IllegalArgumentException(String.format("Can not disconnect client because connection %s is not active", connection));
        }
    }

    private void handleClientTermination(Connection connection, Exception e) {
        disconnectClient(connection);
        log.debug("Connection to client has been terminated", e);
    }


}
