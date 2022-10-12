package ru.aasmc.javaconcurrency.chapter_14;

public class BufferEmptyException extends Exception{
    public BufferEmptyException() {
    }

    public BufferEmptyException(String message) {
        super(message);
    }

    public BufferEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public BufferEmptyException(Throwable cause) {
        super(cause);
    }

    public BufferEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
