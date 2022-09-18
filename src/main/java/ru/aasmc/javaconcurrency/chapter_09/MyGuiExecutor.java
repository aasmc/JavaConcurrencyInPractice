package ru.aasmc.javaconcurrency.chapter_09;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

public class MyGuiExecutor extends AbstractExecutorService {
    // a singleton
    private static final MyGuiExecutor instance = new MyGuiExecutor();

    private MyGuiExecutor() {}

    public static MyGuiExecutor instance() {
        return instance;
    }

    @Override
    public void execute(Runnable command) {
        if (MySwingUtilities.isEventDispatchThread()) {
            command.run();
        } else {
            MySwingUtilities.invokeLater(command);
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }
}
