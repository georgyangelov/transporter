package net.gangelov.transporter.network.protocol.client;

import net.gangelov.transporter.network.ClientHandler;
import net.gangelov.transporter.network.protocol.IConnectionHandler;
import net.gangelov.transporter.network.protocol.Packet;
import net.gangelov.transporter.network.protocol.PacketReader;
import net.gangelov.transporter.network.protocol.PacketWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientConnection implements IConnectionHandler {
    private final ClientHandler client;

    private final Socket clientSocket;
    public final PacketReader reader;
    public final PacketWriter writer;

    public ClientConnection(ClientHandler client, String host, int port) throws IOException {
        this.client = client;
        this.clientSocket = new Socket(host, port);
        this.reader = new PacketReader(new BufferedInputStream(clientSocket.getInputStream()));
        this.writer = new PacketWriter(new BufferedOutputStream(clientSocket.getOutputStream()));
    }

    @Override
    public void run() {
        while (true) {
            try {
                Packet packet = reader.read();

                client.packetReceived(packet);
            } catch (SocketException e) {
                // The socket is being closed
                try {
                    client.socketDisconnected(this);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
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
            clientSocket.close();
        } catch (IOException e) {
            // Ignore errors while closing connection
        }
    }

    @Override
    public PacketReader getReader() {
        return reader;
    }

    @Override
    public PacketWriter getWriter() {
        return writer;
    }
}
