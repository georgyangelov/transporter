package net.gangelov.transporter.network;

import net.gangelov.transporter.network.protocol.Packet;
import net.gangelov.transporter.network.protocol.ProtocolHandler;
import net.gangelov.transporter.network.protocol.clientPackets.AcceptFilePacket;
import net.gangelov.transporter.network.protocol.server.ClientConnectionHandler;
import net.gangelov.transporter.network.protocol.server.ConnectionListener;
import net.gangelov.transporter.network.protocol.serverPackets.HeadersPacket;

import java.io.File;
import java.io.IOException;

public class ServerHandler extends ProtocolHandler {
    private ConnectionListener listener;

    private String filePath;

    private boolean headersSent = false;

    public ServerHandler(int port, String filePath) throws IOException {
        this.filePath = filePath;
        listener = new ConnectionListener(this, port);
    }

    @Override
    public synchronized void socketConnected(ClientConnectionHandler connectionHandler) throws IOException {
        super.socketConnected(connectionHandler);

        if (!headersSent) {
            headersSent = true;

            File file = new File(filePath);
            HeadersPacket headers = new HeadersPacket(file.getName(), file.length());

            sendPacket(headers);
        }
    }

    @Override
    public void packetReceived(Packet packet) throws IOException {
        if (packet.opcode == AcceptFilePacket.OPCODE) {
            // TODO: Start sending data packets
        } else {
            throw new RuntimeException("Unknown packet opcode " + packet.opcode);
        }
    }

    public void start() throws IOException {
        listener.start();
    }

    public void shutdown() {
        listener.stop();
    }
}
