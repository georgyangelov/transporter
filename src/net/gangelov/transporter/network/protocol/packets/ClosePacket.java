package net.gangelov.transporter.network.protocol.packets;

import net.gangelov.transporter.network.protocol.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClosePacket extends Packet {
    public static final byte OPCODE = 3;

    public ClosePacket() {
        super(OPCODE);
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        // Nothing to do here
    }

    @Override
    public void deserialize(DataInputStream in) throws IOException {
        // Nothing to do here
    }

    @Override
    public String toString() {
        return "ClosePacket()";
    }
}
