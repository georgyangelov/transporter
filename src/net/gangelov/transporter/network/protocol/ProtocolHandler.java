package net.gangelov.transporter.network.protocol;

import net.gangelov.transporter.network.protocol.server.ClientConnectionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ProtocolHandler {
    private List<ClientConnectionHandler> connections = new ArrayList<>();
    private RoundRobinSelectList<ClientConnectionHandler> connectionSelector = new RoundRobinSelectList<>(connections);

    public void socketConnected(ClientConnectionHandler connectionHandler) throws IOException {
        connections.add(connectionHandler);
    }

    public void socketDisconnected(ClientConnectionHandler clientConnectionHandler) throws IOException {
        connections.remove(clientConnectionHandler);
    }

    public abstract void packetReceived(Packet packet) throws IOException;

    public abstract void start() throws IOException;
    public abstract void shutdown();

    protected void sendPacket(Packet packet) throws IOException {
        connectionSelector.select().writer.write(packet);
    }
}
