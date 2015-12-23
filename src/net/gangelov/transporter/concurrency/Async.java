package net.gangelov.transporter.concurrency;

import java.util.concurrent.*;

public class Async {
    @FunctionalInterface
    public interface CheckedSupplier<R> {
        R get() throws Exception;
    }

    public static <T> Future<T> run(CheckedSupplier<T> function) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            return executor.submit(() -> function.get());
        } finally {
            executor.shutdown();
        }
    }
}
