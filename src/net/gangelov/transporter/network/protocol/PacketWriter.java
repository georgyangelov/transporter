package net.gangelov.transporter.network.protocol;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PacketWriter {
    private final DataOutputStream out;

    public PacketWriter(OutputStream outputStream) {
        out = new DataOutputStream(new BufferedOutputStream(outputStream));
    }

    public void write(Packet packet) throws IOException {
        out.writeByte(packet.opcode);
        packet.serialize(out);

        out.flush();
    }

    public void close() throws IOException {
        out.close();
    }
}
