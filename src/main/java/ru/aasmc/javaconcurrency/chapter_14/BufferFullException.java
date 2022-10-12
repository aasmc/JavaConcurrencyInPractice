package ru.aasmc.javaconcurrency.chapter_14;

public class BufferFullException extends Exception{
    public BufferFullException() {
    }

    public BufferFullException(String message) {
        super(message);
    }

    public BufferFullException(String message, Throwable cause) {
        super(message, cause);
    }

    public BufferFullException(Throwable cause) {
        super(cause);
    }

    public BufferFullException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
