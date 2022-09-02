package ru.aasmc.javaconcurrency.chapter_07.cancellation;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ReaderThread extends Thread {
    private static final int BUFF_SIZE = 1024;
    private final Socket socket;
    private final InputStream in;

    public ReaderThread(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
    }

    /**
     * Encapsulates non-standard cancellation. It both delivers a standard interrupt
     * and closes the underlying socket.
     */
    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException ex) {
            // ignored
        } finally {
            super.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[BUFF_SIZE];
            while (true) {
                int count = in.read(buffer);
                if (count < 0) {
                    break;
                } else if (count > 0) {
                    processBuffer(buffer, count);
                }
            }
        } catch (IOException e) {
            // allow thread to exit
        }
    }

    private void processBuffer(byte[] buffer, int count) {
        // stub method
    }
}
