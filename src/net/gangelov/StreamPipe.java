package net.gangelov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamPipe {
    private long bytesLeftToRead;
    private int bufferSize;

    private InputStream in;
    private OutputStream out;

    private ProgressTracker speedTracker;

    public StreamPipe(InputStream in, OutputStream out,
                      long maxBytesToRead, int bufferSize,
                      ProgressTracker speedTracker) {
        this.in = in;
        this.out = out;
        this.bytesLeftToRead = maxBytesToRead;
        this.bufferSize = bufferSize;

        this.speedTracker = speedTracker;
    }

    public void pipe() throws IOException {
        byte[] buffer = new byte[bufferSize];
        int bytesRead;

        while ((bytesRead = in.read(buffer, 0, bytesToRead(bytesLeftToRead))) > 0) {
            bytesLeftToRead -= bytesRead;

            out.write(buffer, 0, bytesRead);

            speedTracker.transferBytes(bytesRead);

            out.flush();
        }

        out.flush();
    }

    private int bytesToRead(long remainingBytesToRead) {
        if (bufferSize < remainingBytesToRead) {
            return bufferSize;
        } else {
            return (int)remainingBytesToRead;
        }
    }
}
