package net.gangelov.transporter.network.protocol;

import net.gangelov.transporter.network.protocol.serverPackets.DataPacket;
import net.gangelov.transporter.network.protocol.serverPackets.FinalPacket;
import net.gangelov.transporter.network.protocol.serverPackets.HeadersPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Packet {
    private static final Map<Byte, Class> packetClasses = new HashMap<>();

    public static void registerPacketClass(byte opcode, Class klass) {
        packetClasses.put(opcode, klass);
    }

    static {
        Packet.registerPacketClass(HeadersPacket.OPCODE, HeadersPacket.class);
        Packet.registerPacketClass(DataPacket.OPCODE, DataPacket.class);
        Packet.registerPacketClass(FinalPacket.OPCODE, FinalPacket.class);
    }

    public static Packet newForOpcode(byte opcode) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        if (!packetClasses.containsKey(opcode)) {
            throw new IllegalArgumentException("Unknown packet for opcode " + opcode);
        }

        return (Packet)packetClasses.get(opcode).newInstance();
    }

    public final byte opcode;

    public Packet(byte opcode) {
        this.opcode = opcode;
    }

    abstract public void serialize(DataOutputStream out) throws IOException;
    abstract public void deserialize(DataInputStream in) throws IOException;
}
