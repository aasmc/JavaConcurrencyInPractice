package ru.aasmc.javaconcurrency.chapter_07.stopping_threadbased_service;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class that uses BlockingQueue to save log messages and a separate
 * thread to actually log messages. This is a simple multiple producer single consumer
 * implementation. Stopping the LoggerThread is easy since it repeatedly calls
 * BlockingQueue.take() which is responsive to interruption. However we also need to
 * cancel producer threads, otherwise they will remain blocked forever. Therefore
 * we use a slightly more complex mechanism of stopping the service:
 * - we have a boolean flag to indicate that shutdown was requested
 * - the flag is set to true in LogWriter.stop() method under intrinsic lock.
 * - producer threads check the flag in LogWriter.log(String) method under same lock
 * and reserve the ability to log messages by incrementing the count of reservations field.
 * - consumer thread checks if shutdown was requested and if the number of reservations is 0
 * if so, it breaks out of the loop and finally closes the PrintWriter
 * - otherwise it takes a message from the queue (blocking if no messages are available)
 * - and then it decrements the count of reservations.
 */
public class LogWriter {
    private static final int CAPACITY = 16;
    private final BlockingQueue<String> queue;
    private final LoggerThread logger;
    private boolean isShutDown = false;
    private int reservations = 0;

    public LogWriter(Writer writer) {
        this.queue = new LinkedBlockingQueue<>(CAPACITY);
        this.logger = new LoggerThread(writer);
    }

    public void stop() {
        synchronized (this) {
            isShutDown = true;
        }
        logger.interrupt();
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(LogWriter.this::stop));
        logger.start();
    }

    public void log(String msg) throws InterruptedException {
        synchronized (this) {
            if (isShutDown) {
                throw new IllegalStateException("Logger is shutdown");
            }
            ++reservations;
        }
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        private final PrintWriter writer;

        public LoggerThread(Writer writer) {
            if (!(writer instanceof PrintWriter)) {
                throw new IllegalArgumentException("LoggerThread supports only PrintWriter implementations");
            }
            this.writer = (PrintWriter) writer;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        synchronized (this) {
                            if (isShutDown && reservations == 0) {
                                break;
                            }
                        }
                        String msg = queue.take();
                        synchronized (this) {
                            --reservations;
                        }
                        writer.println(msg);
                    } catch (InterruptedException ignored) {
                        // retry
                    }
                }
            } finally {
                writer.close();
            }
        }
    }
}
