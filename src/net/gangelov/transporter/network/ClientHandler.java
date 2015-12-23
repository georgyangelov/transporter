package net.gangelov.transporter.network;

import net.gangelov.transporter.network.protocol.Packet;
import net.gangelov.transporter.network.protocol.ProtocolHandler;
import net.gangelov.transporter.network.protocol.server.ClientConnectionHandler;
import net.gangelov.transporter.network.protocol.serverPackets.HeadersPacket;

import java.io.IOException;

public class ClientHandler extends ProtocolHandler {
    private int port;

    public ClientHandler(int port) throws IOException {
        this.port = port;
    }

    @Override
    public void packetReceived(Packet packet) throws IOException {
        if (packet.opcode == HeadersPacket.OPCODE) {
            // TODO: Create a file, open a few more connections, start receiving on them, then send AcceptFilePacket
        } else {
            throw new RuntimeException("Unknown packet opcode " + packet.opcode);
        }
    }

    public void start() throws IOException {
        // TODO: Connect to the server
    }

    public void shutdown() {
        // TODO: Disconnect from the server
    }
}
