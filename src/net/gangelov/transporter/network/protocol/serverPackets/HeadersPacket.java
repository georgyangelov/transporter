package net.gangelov.transporter.network.protocol.serverPackets;

import net.gangelov.transporter.network.protocol.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HeadersPacket extends Packet {
    public static byte OPCODE = 1;

    public String fileName;
    public long fileSize;

    public HeadersPacket() {
        super(OPCODE);
    }

    public HeadersPacket(String fileName, long fileSize) {
        super(OPCODE);

        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(fileName);
        out.writeLong(fileSize);
    }

    @Override
    public void deserialize(DataInputStream in) throws IOException {
        fileName = in.readUTF();
        fileSize = in.readLong();
    }
}
