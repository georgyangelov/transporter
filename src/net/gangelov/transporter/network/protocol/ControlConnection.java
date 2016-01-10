package net.gangelov.transporter.network.protocol;

import net.gangelov.transporter.network.protocol.packets.ClosePacket;
import net.gangelov.transporter.network.protocol.packets.HeadersPacket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Consumer;

public class ControlConnection implements Runnable {
    private Socket socket;

    private Consumer<Packet> packetReceivedCallback;

    protected PacketReader packetReader;
    protected PacketWriter packetWriter;

    private boolean disconnecting = false;

    public ControlConnection(Socket socket) throws IOException {
        this.socket = socket;

        this.packetReader = new PacketReader(socket.getInputStream());
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
        while (!disconnecting) {
            try {
                Packet packet = packetReader.read();

                packetReceivedCallback.accept(packet);
            } catch (SocketException e) {
                // The socket is being closed by us
                break;
            } catch (Exception e) {
                if (!disconnecting) {
                    // Read error
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    public void disconnect() {
        disconnecting = true;

        try {
            sendPacket(new ClosePacket());
        } catch (IOException e) {
            // Cannot send close packet. Whatever...
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            // Ignore closing errors
        }
    }
}
