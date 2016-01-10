package net.gangelov.transporter.network;

import net.gangelov.transporter.concurrency.Async;
import net.gangelov.transporter.network.protocol.Packet;
import net.gangelov.transporter.network.protocol.ControlConnection;
import net.gangelov.transporter.network.protocol.packets.ClosePacket;
import net.gangelov.transporter.network.protocol.packets.HeadersPacket;
import net.gangelov.transporter.network.protocol.ServerDataConnection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerInstance {
    private final ControlConnection controlConnection;
    private final List<ServerDataConnection> dataConnections = new ArrayList<ServerDataConnection>();

    private int connectionId;
    private final File file;

    public ServerInstance(ControlConnection controlConnection, int connectionId, File file) {
        this.controlConnection = controlConnection;
        this.connectionId = connectionId;
        this.file = file;

        controlConnection.onPacketReceived(this::handlePacketReceived);
    }

    public void start() throws IOException {
        sendInitialPacket();

        Async.run(controlConnection);
    }

    public void stop() {
        dataConnections.forEach(ServerDataConnection::disconnect);

        controlConnection.disconnect();
    }

    public void onNewDataConnection(ServerDataConnection dataConnection) {
        dataConnections.add(dataConnection);

        Async.run(dataConnection);
    }

    private void sendInitialPacket() throws IOException {
        HeadersPacket headers = new HeadersPacket(connectionId, file.getName(), file.length());
        controlConnection.sendPacket(headers);
    }

    private void handlePacketReceived(Packet packet) {
        System.out.println("[ServerInstance] Packet received: " + packet.toString());

        if (packet.opcode == ClosePacket.OPCODE) {
            stop();
        } else {
            System.err.println("Unknown packet received " + packet.toString());
        }
    }
}
