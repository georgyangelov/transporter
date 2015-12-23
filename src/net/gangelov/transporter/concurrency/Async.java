package net.gangelov.transporter.concurrency;

import java.util.concurrent.*;

public class Async {
    @FunctionalInterface
    public interface CheckedSupplier<R> {
        R get() throws Exception;
    }

    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static <T> Future<T> run(CheckedSupplier<T> function) {
        return executor.submit(() -> function.get());
    }

    public static void run(Runnable runnable) {
        executor.submit(runnable);
    }
}
