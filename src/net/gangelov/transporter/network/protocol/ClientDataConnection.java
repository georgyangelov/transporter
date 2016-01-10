package net.gangelov.transporter.network.protocol;

import net.gangelov.Streams;
import net.gangelov.transporter.network.protocol.packets.FileRequestPacket;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.Channels;

public class ClientDataConnection implements Runnable {
    private final Socket socket;
    private final File file;
    private final FileRequestPacket request;

    public ClientDataConnection(Socket socket, FileRequestPacket request, File file) throws IOException {
        this.socket = socket;
        this.request = request;
        this.file = file;

        // Send the request packet
        request.serialize(new DataOutputStream(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            transfer();
        } catch (SocketException e) {
            // The socket is being disconnected
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("File transferred");
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            // Ignore close errors
        }
    }

    public void transfer() throws IOException {
        // TODO: Maybe lock the file while writing to it
        RandomAccessFile fileAccess = new RandomAccessFile(file, "rw");
        BufferedOutputStream out = new BufferedOutputStream(
            Channels.newOutputStream(
                fileAccess.getChannel().position(request.offset)
            )
        );

        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

        Streams.pipe(in, out, 4096, false, request.size);
    }
}
