package net.gangelov.transporter.network.protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Consumer;

public class ControlConnection implements Runnable {
    private Socket socket;

    private Consumer<Packet> packetReceivedCallback;

    protected PacketReader packetReader;
    protected PacketWriter packetWriter;

    public ControlConnection(Socket socket) throws IOException {
        this.socket = socket;

        this.packetReader = new PacketReader(new BufferedInputStream(socket.getInputStream()));
        this.packetWriter = new PacketWriter(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void onPacketReceived(Consumer<Packet> callback) {
        packetReceivedCallback = callback;
    }

    public void sendPacket(Packet packet) throws IOException {
        packetWriter.write(packet);
    }

    @Override
    public void run() {
        while (true) {
            try {
                packetReceivedCallback.accept(packetReader.read());
            } catch (SocketException e) {
                // The socket is being closed
                break;
            } catch (IOException e) {
                // Read error
                e.printStackTrace();
            } catch (Exception e) {
                // Other exception
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            // Ignore closing errors
        }
    }
}
