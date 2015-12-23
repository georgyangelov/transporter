package net.gangelov.transporter.network.protocol.server;

import net.gangelov.transporter.network.ServerHandler;
import net.gangelov.transporter.network.protocol.Packet;
import net.gangelov.transporter.network.protocol.PacketReader;
import net.gangelov.transporter.network.protocol.PacketWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientConnectionHandler implements Runnable {
    private final ServerHandler server;

    private final Socket client;
    public final PacketReader reader;
    public final PacketWriter writer;

    public ClientConnectionHandler(ServerHandler server, Socket client) throws IOException {
        this.server = server;
        this.client = client;
        this.reader = new PacketReader(new BufferedInputStream(client.getInputStream()));
        this.writer = new PacketWriter(new BufferedOutputStream(client.getOutputStream()));
    }

    @Override
    public void run() {
        while (true) {
            try {
                Packet packet = reader.read();

                server.packetReceived(packet);
            } catch (SocketException e) {
                // The socket is being closed
                server.socketDisconnected(this);
            } catch (IOException e) {
                // Read error
                e.printStackTrace();
            } catch (Exception e) {
                // Other exception
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            // Ignore errors while closing connection
        }

        try {
            reader.close();
        } catch (IOException e) {
            // Ignore errors while closing connection
        }

        try {
            client.close();
        } catch (IOException e) {
            // Ignore errors while closing connection
        }
    }
}
