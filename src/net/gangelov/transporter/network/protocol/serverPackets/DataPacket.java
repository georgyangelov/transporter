package net.gangelov.transporter.network.protocol.serverPackets;

import net.gangelov.transporter.network.protocol.Packet;

import java.io.*;

public class DataPacket extends Packet {
    public static byte OPCODE = 2;

    public long offset;
    public byte[] data;

    public DataPacket() {
        super(OPCODE);
    }

    public DataPacket(long offset, byte[] data) {
        super(OPCODE);

        this.offset = offset;
        this.data = data;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeLong(offset);
        out.writeInt(data.length);
        out.write(data);
    }

    @Override
    public void deserialize(DataInputStream in) throws IOException {
        offset = in.readLong();

        int dataLength = in.readInt();

        data = new byte[dataLength];
        in.readFully(data);
    }
}
