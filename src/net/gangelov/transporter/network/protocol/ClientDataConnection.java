package net.gangelov.transporter.network.protocol;

import net.gangelov.transporter.network.protocol.packets.FileRequestPacket;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientDataConnection implements Runnable {
    private final Socket socket;
    private final File file;

    public ClientDataConnection(Socket socket, FileRequestPacket request, File file) throws IOException {
        this.socket = socket;
        this.file = file;

        // Send the request packet
        request.serialize(new DataOutputStream(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            readFile();
        } catch (SocketException e) {
            // The socket is being disconnected
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            // Ignore close errors
        }
    }

    public void readFile() throws IOException {
        // TODO: Begin sending file
    }
}
