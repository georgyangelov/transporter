package net.gangelov.transporter.network.protocol.packets;

import net.gangelov.transporter.network.protocol.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HeadersPacket extends Packet {
    public static final byte OPCODE = 1;

    public int connectionId;
    public String fileName;
    public long fileSize;

    public HeadersPacket() {
        super(OPCODE);
    }

    public HeadersPacket(int connectionId, String fileName, long fileSize) {
        super(OPCODE);

        this.connectionId = connectionId;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(connectionId);
        out.writeUTF(fileName);
        out.writeLong(fileSize);
    }

    @Override
    public void deserialize(DataInputStream in) throws IOException {
        connectionId = in.readInt();
        fileName = in.readUTF();
        fileSize = in.readLong();
    }

    @Override
    public String toString() {
        return "HeadersPacket(connectionId: " + connectionId + ", " +
            "fileName: " + fileName + ", " +
            "fileSize: " + fileSize + ")";
    }
}
