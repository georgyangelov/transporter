package net.gangelov;

public class ProgressTracker {
    private long[] window;
    private long lastTimestamp = getTimestamp();
    private long totalBytes, transferredBytes = 0;
    private long startTimestamp = getTimestamp();

    public ProgressTracker(long totalBytes) {
        window = new long[5];
        this.totalBytes = totalBytes;
    }

    public ProgressTracker(long totalBytes, int speedWindowSizeInSeconds) {
        window = new long[speedWindowSizeInSeconds];
        this.totalBytes = totalBytes;
    }

    public synchronized void transferBytes(long bytes) {
        long currentTimestamp = getTimestamp();
        int currentIndex = (int)(currentTimestamp % window.length);

        if (currentTimestamp != lastTimestamp) {
            int secondsDifference = (int)(currentTimestamp - lastTimestamp);

            // Clear any bytes we might have jumped over due to slow transfer
            for (int i = 0; i < Math.min(window.length, secondsDifference); i++) {
                window[Math.floorMod(currentIndex - i, window.length)] = 0;
            }

            lastTimestamp = currentTimestamp;
        }

        transferredBytes += bytes;
        window[currentIndex] += bytes;
    }

    public long getTransferredBytes() {
        return transferredBytes;
    }

    public float getProgress() {
        return (float)transferredBytes / totalBytes;
    }

    public long getSpeedInBytesPerSecond() {
        return totalBytesTransferredInWindow() / window.length;
    }

    public int getExecutionTime() {
        return (int)(getTimestamp() - startTimestamp);
    }

    public String getSpeedAsString() {
        long speed = getSpeedInBytesPerSecond();

        return speedToString(speed);
    }

    private static final int MEGABYTE = 1024 * 1024;
    private static final int KILOBYTE = 1024;

    public static final String speedToString(long speed) {
        if (speed > MEGABYTE) {
            return "" + speed / MEGABYTE + " MiB/s";
        } else if (speed > KILOBYTE) {
            return "" + speed / KILOBYTE + " KiB/s";
        } else {
            return "" + speed + " B/s";
        }
    }

    private long totalBytesTransferredInWindow() {
        long sum = 0;

        for (int i = 0; i < window.length; i++) {
            sum += window[i];
        }

        return sum;
    }

    private static long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }
}
