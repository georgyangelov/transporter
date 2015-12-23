package net.gangelov.transporter.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

public class Async {
    @FunctionalInterface
    public interface CheckedSupplier<R> {
        R get() throws Exception;
    }

    public static <T> Future<T> run(CheckedSupplier<T> function) {
        // TODO: Shutdown executor
        return Executors.newSingleThreadExecutor().submit(() -> function.get());
    }
}
