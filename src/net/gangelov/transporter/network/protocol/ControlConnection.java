package net.gangelov.transporter.network.protocol;

import net.gangelov.transporter.network.protocol.packets.HeadersPacket;

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
        while (true) {
            try {
                Packet packet = packetReader.read();

                packetReceivedCallback.accept(packet);
            } catch (SocketException e) {
                // The socket is being closed
                break;
            } catch (IOException e) {
                // Read error
                e.printStackTrace();
                break;
            } catch (Exception e) {
                // Other exception
                e.printStackTrace();
                break;
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
