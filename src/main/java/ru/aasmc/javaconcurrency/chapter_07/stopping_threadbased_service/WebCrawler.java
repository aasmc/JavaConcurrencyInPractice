package ru.aasmc.javaconcurrency.chapter_07.stopping_threadbased_service;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates the usage of TrackingExecutor to make
 * webcrawler save tasks that have been cancelled for later use.
 */
public abstract class WebCrawler {
    private static final long TIMEOUT = 10;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private volatile TrackingExecutor exec;
    private final Set<URL> urlsToScan = new HashSet<>();

    public synchronized void start() {
        exec = new TrackingExecutor(Executors.newCachedThreadPool());
        for (URL url : urlsToScan) {
            submitCrawlTask(url);
            urlsToScan.clear();
        }
    }

    public synchronized void stop() throws InterruptedException {
        try {
            saveUncrawled(exec.shutdownNow());
            if (exec.awaitTermination(TIMEOUT, UNIT)) {
                saveUncrawled(exec.getCancelledTasks());
            }
        } finally {
            exec = null;
        }
    }

    protected abstract List<URL> processPage(URL page);

    private void saveUncrawled(List<Runnable> unclawled) {
        for (Runnable task : unclawled) {
            urlsToScan.add(((CrawlTask) task).getPage());
        }
    }

    private void submitCrawlTask(URL url) {
        exec.execute(new CrawlTask(url));
    }

    private class CrawlTask implements Runnable {
        private final URL url;

        public CrawlTask(URL url) {
            this.url = url;
        }

        @Override
        public void run() {
            for (URL link : processPage(url)) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                submitCrawlTask(link);
            }
        }

        public URL getPage() {
            return url;
        }
    }
}
