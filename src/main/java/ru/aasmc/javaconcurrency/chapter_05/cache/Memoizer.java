package ru.aasmc.javaconcurrency.chapter_05.cache;

import java.util.concurrent.*;

public class Memoizer<A, V> implements Computable<A, V> {
    private final ConcurrentMap<A, Future<V>> cache =
            new ConcurrentHashMap<>();

    private final Computable<A, V> computable;

    public Memoizer(Computable<A, V> computable) {
        this.computable = computable;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        while (true) {
            Future<V> f = cache.get(arg);
            if (f == null) { // check if the computation was started
                Callable<V> eval = () -> {
                    return computable.compute(arg);
                };

                FutureTask<V> ft = new FutureTask<>(eval);
                // atomic operation putIfAbsent. returns value previously associated with the key
                // or null if there was no mapping for the key
                // This prevents more than one thread to perform the action in parallel
                f = cache.putIfAbsent(arg, ft);
                if (f == null) { // this means there was no previous mapping to the key
                    // but now we are sure that our Future object is in the map.
                    f = ft;
                    // Idk if this operation is idempotent... What if not?
                    // we start the computation here
                    ft.run();
                }
            }
            try {
                // wait for the result of the computation
                return f.get();
            } catch (CancellationException e) {
                // remove the future from the cache if computation was cancelled
                // this prevents cache pollution with objects of Future that will
                // never complete the computation.
                cache.remove(arg, f);
            } catch (ExecutionException e) {
                throw launderThrowable(e);
            }
        }
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
}
