package net.gangelov.transporter.concurrency;

import java.util.List;
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

    public static Future<?> run(Runnable runnable) {
        return executor.submit(runnable);
    }

    public static void runAndWaitFor(List<? extends Runnable> runnables, Callback callback) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(runnables.size());
        CompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(executor);

        runnables.forEach((runnable) -> completionService.submit(runnable, true));

        run(() -> {
            for (int i = 0; i < runnables.size(); i++) {
                try {
                    completionService.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            executor.shutdown();
            callback.execute();
        });
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
