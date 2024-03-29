package com.ternsip.soil.general;

import com.ternsip.soil.common.ThreadWrapper;
import com.ternsip.soil.network.NetworkClient;
import com.ternsip.soil.network.NetworkServer;
import com.ternsip.soil.network.NetworkServerAcceptor;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class Threads {

    public final Client client = new Client();
    private final Thread mainThread = Thread.currentThread();
    private final Scanner scanner = new Scanner(System.in);
    private final ThreadWrapper<Server> universeServerThreadWrapper = new ThreadWrapper<>(Server::new, 1000L / 128);
    private final ThreadWrapper<NetworkClient> networkClientThreadWrapper = new ThreadWrapper<>(NetworkClient::new);
    private final ThreadWrapper<NetworkServer> networkServerThreadWrapper = new ThreadWrapper<>(NetworkServer::new);
    private final ThreadWrapper<NetworkServerAcceptor> networkServerAcceptorThread = new ThreadWrapper<>(NetworkServerAcceptor::new);

    public void runClient() {
        networkClientThreadWrapper.start();
        networkClientThreadWrapper.waitInitialization();
        client.init();
        while (client.windowData.isActive()) {
            client.update();
        }
        networkClientThreadWrapper.getObjective().stop();
        networkClientThreadWrapper.stop();
        networkClientThreadWrapper.join();
        client.finish();
    }

    public void runServer() {
        networkServerThreadWrapper.start();
        networkServerThreadWrapper.waitInitialization();
        universeServerThreadWrapper.start();
        universeServerThreadWrapper.waitInitialization();
        networkServerAcceptorThread.start();
        networkServerAcceptorThread.waitInitialization();
        while (scanner.hasNext()) {
            String inputMessage = scanner.next();
            if (inputMessage.equalsIgnoreCase("exit")) {
                System.out.println("Stopping the sever...");
                networkServerAcceptorThread.stop();
                universeServerThreadWrapper.stop();
                networkServerThreadWrapper.getObjective().stop();
                networkServerThreadWrapper.stop();
                networkServerThreadWrapper.join();
                break;
            } else {
                System.out.println("Unknown command");
            }
        }
    }

    public Thread getMainThread() {
        return mainThread;
    }

    public Server getServer() {
        return universeServerThreadWrapper.getObjective();
    }

    public NetworkServer getNetworkServer() {
        return networkServerThreadWrapper.getObjective();
    }

    public NetworkClient getNetworkClient() {
        return networkClientThreadWrapper.getObjective();
    }

}
