package net.gangelov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Streams {
    public static void pipe(InputStream in, OutputStream out,
                            int bufferSize, boolean eagerFlush,
                            long maxBytesToRead) throws IOException {
        byte[] buffer = new byte[bufferSize];
        int bytesRead;

        while ((bytesRead = in.read(buffer, 0, bytesToRead(bufferSize, maxBytesToRead))) > 0) {
            maxBytesToRead -= bytesRead;

            out.write(buffer, 0, bytesRead);

            if (eagerFlush) {
                out.flush();
            }
        }

        out.flush();
    }

    public static void pipe(InputStream in, OutputStream out) throws IOException {
        pipe(in, out, 4096, false, 0);
    }

    private static int bytesToRead(int bufferSize, long remainingBytesToRead) {
        if (bufferSize < remainingBytesToRead) {
            return bufferSize;
        } else {
            return (int)remainingBytesToRead;
        }
    }
}
