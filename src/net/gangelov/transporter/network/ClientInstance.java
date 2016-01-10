package net.gangelov.transporter.network;

import net.gangelov.transporter.concurrency.Async;
import net.gangelov.transporter.concurrency.Callback;
import net.gangelov.transporter.network.protocol.ClientDataConnection;
import net.gangelov.transporter.network.protocol.ControlConnection;
import net.gangelov.transporter.network.protocol.Packet;
import net.gangelov.transporter.network.protocol.packets.ClosePacket;
import net.gangelov.transporter.network.protocol.packets.FileRequestPacket;
import net.gangelov.transporter.network.protocol.packets.HeadersPacket;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientInstance {
    private final ControlConnection controlConnection;
    private final List<ClientDataConnection> dataConnections = new ArrayList<>();

    private final String host;
    private final File file;
    private final int dataPort;

    private HeadersPacket headers;

    private Callback onComplete = () -> {};

    public ClientInstance(String host, int controlPort, int dataPort, File file) throws IOException {
        this.host = host;
        this.file = file;
        this.dataPort = dataPort;

        Socket controlConnectionSocket = new Socket(host, controlPort);

        controlConnection = new ControlConnection(controlConnectionSocket);
        controlConnection.onPacketReceived((packet) -> {
            try {
                onPacketReceived(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void onComplete(Callback onComplete) {
        this.onComplete = onComplete;
    }

    public void start() {
        Async.run(controlConnection);
    }

    public void stop() {
        dataConnections.forEach(ClientDataConnection::disconnect);
        controlConnection.disconnect();
    }

    private void onPacketReceived(Packet packet) throws Exception {
        System.out.println("[ClientInstance] Packet received: " + packet.toString());

        if (packet.opcode == HeadersPacket.OPCODE) {
            headers = (HeadersPacket)packet;

            allocateFile();
            openDataConnections();
        } else {
            throw new RuntimeException("Unknown packet type " + packet.opcode);
        }
    }

    private void allocateFile() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        randomAccessFile.setLength(headers.fileSize);
        randomAccessFile.close();
    }

    private void openDataConnections() throws IOException, InterruptedException {
        // TODO: Open multiple data connections
        Socket socket = new Socket(host, dataPort);
        FileRequestPacket request = new FileRequestPacket(headers.connectionId, 0, headers.fileSize);
        ClientDataConnection dataConnection = new ClientDataConnection(socket, request, file);

        dataConnections.add(dataConnection);

        Async.runAndWaitFor(dataConnections, () -> {
            stop();
            onComplete.execute();
        });
    }
}
