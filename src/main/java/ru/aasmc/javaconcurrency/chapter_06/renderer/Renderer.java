package ru.aasmc.javaconcurrency.chapter_06.renderer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Renderer {
    private final ExecutorService executor;

    public Renderer(ExecutorService executor) {
        this.executor = executor;
    }

    public void renderPage(CharSequence source) {
        List<ImageInfo> imageInfos = scanForImages(source);
        // ExecutorCompletionService uses blocking queue under the hood to
        // handle incoming callables. It combines the functionality of the Executor and BlockingQueue.
        ExecutorCompletionService<ImageData> completionService = new ExecutorCompletionService<>(executor);
        for (final ImageInfo imageInfo : imageInfos) {
            completionService.submit(imageInfo::downLoadImage);
        }

        renderText(source);

        try {
            for (int i = 0; i < imageInfos.size(); i++) {
                // here we block if no Future<ImageData> objects are available.
                Future<ImageData> f = completionService.take();
                // wait for the result of the Future.
                ImageData imageData = f.get();
                renderImage(imageData);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw launderThrowable(e);
        }
    }

    private void renderText(CharSequence source) {

    }

    private void renderImage(ImageData imageData) {

    }

    /**
     * If the Throwable is an Error, throw it, if it a RuntimeException return it,
     * otherwise throw IllegalStateException.
     */
    private RuntimeException launderThrowable(Throwable cause) {
        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        } else if (cause instanceof Error) {
            throw (Error) cause;
        } else {
            throw new IllegalStateException("Not checked", cause);
        }
    }

    private List<ImageInfo> scanForImages(CharSequence source) {
        return Collections.singletonList(new ImageInfo());
    }
}
