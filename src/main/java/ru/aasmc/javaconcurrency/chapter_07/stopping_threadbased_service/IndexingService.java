package ru.aasmc.javaconcurrency.chapter_07.stopping_threadbased_service;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

/**
 * Example of using a POISON pill to stop a thread-based service.
 * This implementation assumes Single Producer Single Consumer
 * pattern. To work well with multiple producers
 * we need to send N (producer) POISON pills, thus it requires an unbounded
 * BlockingQueue.
 */
public class IndexingService {
    private static final File POISON = new File("");
    private final IndexerThread consumer = new IndexerThread();
    private final CrawlerThread producer = new CrawlerThread();
    private final BlockingQueue<File> queue;
    private final FileFilter fileFilter;
    private final File root;

    public IndexingService(BlockingQueue<File> queue, FileFilter fileFilter, File root) {
        this.queue = queue;
        this.fileFilter = fileFilter;
        this.root = root;
    }

    public void start() {
        producer.start();
        consumer.start();
    }

    public void awaitTermination() throws InterruptedException {
        consumer.join();
    }

    public class CrawlerThread extends Thread {
        @Override
        public void run() {
            try {
                crawl(root);
            } catch (InterruptedException e) {
                // fall through
            } finally {
                while (true) {
                    try {
                        queue.put(POISON);
                        break;
                    } catch (InterruptedException e1) {
                        // retry
                    }
                }
            }
        }

        private void crawl(File root) throws InterruptedException {
            // stub method
        }
    }

    private class IndexerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    File file = queue.take();
                    if (file == POISON) {
                        break;
                    } else {
                        indexFile(file);
                    }
                }
            } catch (InterruptedException e) {
                // consumed
            }
        }

        private void indexFile(File file) {
            // stub method
        }
    }
}
