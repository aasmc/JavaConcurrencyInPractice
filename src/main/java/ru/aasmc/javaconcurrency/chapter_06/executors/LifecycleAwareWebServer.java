package ru.aasmc.javaconcurrency.chapter_06.executors;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LifecycleAwareWebServer {
    private static final int N_THREADS = 100;
    private static final ExecutorService exec = Executors.newFixedThreadPool(N_THREADS);

    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (!exec.isShutdown()) {
            try {
                final Socket conn = socket.accept();
                exec.execute(() -> {
                    handleRequest(conn);
                });
            } catch (RejectedExecutionException e) {
                if (!exec.isShutdown()) {
                    Logger.getLogger("LifecycleAwareWebServer").log(Level.INFO, "The task has been rejected", e);
                }
            }
        }
    }

    public void stop() {
        exec.shutdown();
    }

    void handleRequest(Socket connection) {
        Request request = readRequest(connection);
        if (isShutdownRequest(request)) {
            stop();
        } else {
            dispatchRequest(request);
        }
    }

    /**
     * Stub method.
     */
    private void dispatchRequest(Request request) {

    }

    /**
     * Stub method.
     */
    private Request readRequest(Socket connection) {
        return new Request();
    }

    /**
     * Stub method.
     */
    private boolean isShutdownRequest(Request request) {
        return new Random(System.currentTimeMillis()).nextInt() == 10;
    }
}
