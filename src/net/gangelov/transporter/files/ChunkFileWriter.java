package net.gangelov.transporter.files;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ChunkFileWriter {
    private final RandomAccessFile out;

    public ChunkFileWriter(File file, long fileSize) throws IOException {
        out = new RandomAccessFile(file, "w");
        out.setLength(fileSize);
    }

    // TODO: Experiment using MappedByteBuffer and writing directly to memory, then ask the OS to write that.
    //       A sliding window algorithm must be used for this, since it is limited to the available memory
    //       on the system.
    public void write(long fileOffset, byte[] buffer, int bufferOffset, int size) throws IOException {
        out.seek(bufferOffset);
        out.write(buffer, bufferOffset, size);
    }

    public void close() throws IOException {
        out.close();
    }
}
