package ru.aasmc.javaconcurrency.chapter_05.desktopcrawler;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DesktopCrawlerDriver {
    private static final int BOUND = 100;
    private static final int N_CONSUMERS = 10;

    public static void startIndexing(File[] roots) {
        BlockingQueue<File> queue = new LinkedBlockingQueue<>(BOUND);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        };
        for (File root :
                roots) {
            new Thread(new FileCrawler(queue, fileFilter, root)).start();
        }
        for (int i = 0; i < N_CONSUMERS; i++) {
            new Thread(new Indexer(queue)).start();
        }
    }
}
