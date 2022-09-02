package ru.aasmc.javaconcurrency.chapter_07.cancellabletask;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * If a SocketUsingTask is cancelled through its RunnableFuture (i.e. it is submitted to
 * CancellingExecutor and at some point in time the Future returned by ThreadPoolExecutor.submit(Callable)
 * gets cancelled) the socket is closed and the executing thread is interrupted.
 * @param <T>
 */
public abstract class SocketUsingTask<T> implements CancellableTask<T> {

    private Socket socket;

    protected synchronized void setSocket(Socket s) {
        this.socket = s;
    }

    @Override
    public synchronized void cancel() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public RunnableFuture<T> newTask() {
        return new FutureTask<T>(this) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    SocketUsingTask.this.cancel();
                } finally {
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }
}
