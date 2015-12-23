package net.gangelov.transporter.network.protocol;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PacketReader {
    private final DataInputStream in;

    public PacketReader(InputStream inputStream) {
        in = new DataInputStream(new BufferedInputStream(inputStream));
    }

    public Packet read() throws IOException, InstantiationException, IllegalAccessException {
        byte opcode = in.readByte();

        Packet packet = Packet.newForOpcode(opcode);

        packet.deserialize(in);

        return packet;
    }

    public void close() throws IOException {
        in.close();
    }
}
