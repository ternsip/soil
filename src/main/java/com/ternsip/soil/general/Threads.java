package com.ternsip.soil.general;

import com.ternsip.soil.network.NetworkServerAcceptor;
import com.ternsip.soil.network.NetworkClient;
import com.ternsip.soil.network.NetworkServer;

import java.util.Scanner;

public class Threads {

    private final Thread mainThread = Thread.currentThread();
    private final Scanner scanner = new Scanner(System.in);
    private final Graphics graphics = new Graphics();
    private final ThreadWrapper<UniverseClient> universeClientThreadWrapper = new ThreadWrapper<>(UniverseClient::new, 1000L / 128);
    private final ThreadWrapper<UniverseServer> universeServerThreadWrapper = new ThreadWrapper<>(UniverseServer::new, 1000L / 128);
    private final ThreadWrapper<NetworkClient> networkClientThreadWrapper = new ThreadWrapper<>(NetworkClient::new);
    private final ThreadWrapper<NetworkServer> networkServerThreadWrapper = new ThreadWrapper<>(NetworkServer::new);
    private final ThreadWrapper<NetworkServerAcceptor> networkServerAcceptorThread = new ThreadWrapper<>(NetworkServerAcceptor::new);

    public void runClient() {
        universeClientThreadWrapper.start();
        networkClientThreadWrapper.start();
        graphics.init();
        while (graphics.windowData.isActive() && universeClientThreadWrapper.isActive()) {
            graphics.update();
        }
        networkClientThreadWrapper.getObjective().stop();
        networkClientThreadWrapper.stop();
        networkClientThreadWrapper.join();
        graphics.finish();
        universeClientThreadWrapper.stop();
    }

    public void runServer() {
        networkServerThreadWrapper.start();
        universeServerThreadWrapper.start();
        networkServerAcceptorThread.start();
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

    public Graphics getGraphics() {
        if (Thread.currentThread() != mainThread) {
            throw new IllegalArgumentException("You should call graphics only from the main thread");
        }
        return graphics;
    }

    public UniverseClient getUniverseClient() {
        Thread thread = Thread.currentThread();
        if (thread != mainThread &&
            thread != universeClientThreadWrapper.getThread() &&
            thread != networkClientThreadWrapper.getThread()
        ) {
            throw new IllegalArgumentException("You can not call client universe from this thread");
        }
        return universeClientThreadWrapper.getObjective();
    }

    public UniverseServer getUniverseServer() {
        Thread thread = Thread.currentThread();
        if (thread != mainThread &&
            thread != universeClientThreadWrapper.getThread() &&
            thread != networkClientThreadWrapper.getThread()
        ) {
            throw new IllegalArgumentException("You can not call server universe from this thread");
        }
        return universeServerThreadWrapper.getObjective();
    }

    public NetworkServer getNetworkServer() {
        return networkServerThreadWrapper.getObjective();
    }

    public NetworkClient getNetworkClient() {
        return networkClientThreadWrapper.getObjective();
    }

}
