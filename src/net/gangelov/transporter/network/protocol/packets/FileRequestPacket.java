package net.gangelov.transporter.network.protocol.packets;

import net.gangelov.transporter.network.protocol.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FileRequestPacket extends Packet {
    public static final byte OPCODE = 2;

    public int connectionId;
    public long offset, size;

    public FileRequestPacket() {
        super(OPCODE);
    }

    public FileRequestPacket(int connectionId, long offset, long size) {
        super(OPCODE);

        this.connectionId = connectionId;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(connectionId);
        out.writeLong(offset);
        out.writeLong(size);
    }

    @Override
    public void deserialize(DataInputStream in) throws IOException {
        connectionId = in.readInt();
        offset = in.readLong();
        size = in.readLong();
    }
}
