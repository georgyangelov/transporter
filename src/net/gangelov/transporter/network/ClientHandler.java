package net.gangelov.transporter.network;

import net.gangelov.transporter.files.ChunkFileWriter;
import net.gangelov.transporter.network.protocol.Packet;
import net.gangelov.transporter.network.protocol.ProtocolHandler;
import net.gangelov.transporter.network.protocol.client.ClientConnection;
import net.gangelov.transporter.network.protocol.clientPackets.AcceptFilePacket;
import net.gangelov.transporter.network.protocol.serverPackets.DataPacket;
import net.gangelov.transporter.network.protocol.serverPackets.FinalPacket;
import net.gangelov.transporter.network.protocol.serverPackets.HeadersPacket;

import java.io.File;
import java.io.IOException;

public class ClientHandler extends ProtocolHandler {
    String host;
    private int port, concurrency;
    private ChunkFileWriter fileWriter;

    public ClientHandler(String host, int port, int concurrency) throws IOException {
        this.host = host;
        this.port = port;
        this.concurrency = concurrency;
    }

    @Override
    public void packetReceived(Packet packet) throws IOException {
        if (packet.opcode == HeadersPacket.OPCODE) {
            HeadersPacket headers = (HeadersPacket) packet;

            // Create a file and a writer to it
            fileWriter = new ChunkFileWriter(new File(headers.fileName), headers.fileSize);

            // Open a few more connections. `concurrency - 1` connections, to be precise.
            for (int i = 1; i < concurrency; concurrency++) {
                openNewConnection();
            }

            // Tell the server to start sending data
            sendPacket(new AcceptFilePacket());
        } else if (packet.opcode == DataPacket.OPCODE) {
            DataPacket dataPacket = (DataPacket) packet;

            fileWriter.write(dataPacket.offset, dataPacket.data, 0, dataPacket.data.length);
        } else if (packet.opcode == FinalPacket.OPCODE) {
            // TODO: Handle
        } else {
            throw new RuntimeException("Unknown packet opcode " + packet.opcode);
        }
    }

    public void start() throws IOException {
        // Create the first connection to the server
        openNewConnection();
    }

    private void openNewConnection() throws IOException {
        ClientConnection connection = new ClientConnection(this, host, port);
        socketConnected(connection);
    }
}
