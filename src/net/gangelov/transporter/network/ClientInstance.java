package net.gangelov.transporter.network;

import net.gangelov.transporter.network.protocol.ClientDataConnection;
import net.gangelov.transporter.network.protocol.ControlConnection;
import net.gangelov.transporter.network.protocol.Packet;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientInstance {
    private final ControlConnection controlConnection;
    private final List<ClientDataConnection> dataConnections = new ArrayList<>();

    private final File file;
    private final int dataPort;

    public ClientInstance(String host, int controlPort, int dataPort, File file) throws IOException {
        this.file = file;
        this.dataPort = dataPort;

        Socket controlConnectionSocket = new Socket(host, controlPort);

        controlConnection = new ControlConnection(controlConnectionSocket);
        controlConnection.onPacketReceived(this::onPacketReceived);
    }

    private void onPacketReceived(Packet packet) {
        // TODO: Handle HeadersPacket
    }
}
