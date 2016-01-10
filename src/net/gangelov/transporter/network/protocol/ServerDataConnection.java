package net.gangelov.transporter.network.protocol;

import net.gangelov.Streams;
import net.gangelov.transporter.network.protocol.packets.FileRequestPacket;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ServerDataConnection implements Runnable {
    private final Socket socket;
    private final File file;

    public final FileRequestPacket requestPacket = new FileRequestPacket();

    public ServerDataConnection(Socket socket, File file) throws IOException {
        this.socket = socket;
        this.file = file;

        // Read the request packet
        requestPacket.deserialize(new DataInputStream(socket.getInputStream()));
        System.out.println("[ServerDataConnection] Packet received: " + requestPacket.toString());
    }

    @Override
    public void run() {
        try {
            sendFile();
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

    public void sendFile() throws IOException {
        FileInputStream in = new FileInputStream(file);
        in.skip(requestPacket.offset);

        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

        Streams.pipe(in, out, 4096, false, requestPacket.size);
    }
}
