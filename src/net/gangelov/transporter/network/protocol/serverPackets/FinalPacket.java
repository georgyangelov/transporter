package net.gangelov.transporter.network.protocol.serverPackets;

import net.gangelov.transporter.network.protocol.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FinalPacket extends Packet {
    public static byte OPCODE = 3;

    public FinalPacket() {
        super(OPCODE);
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        // No payload in this packet
    }

    @Override
    public void deserialize(DataInputStream in) throws IOException {
        // No payload in this packet
    }
}
