package net.gangelov.transporter.network.protocol;

import net.gangelov.transporter.concurrency.Async;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ProtocolHandler {
    private List<IConnectionHandler> connections = new ArrayList<>();
    private RoundRobinSelectList<IConnectionHandler> connectionSelector = new RoundRobinSelectList<>(connections);

    public void socketConnected(IConnectionHandler connectionHandler) throws IOException {
        connections.add(connectionHandler);

        Async.run(connectionHandler);
    }

    public void socketDisconnected(IConnectionHandler clientConnectionHandler) throws IOException {
        connections.remove(clientConnectionHandler);
    }

    public abstract void packetReceived(Packet packet) throws IOException;

    public abstract void start() throws IOException;

    public void shutdown() {
        connections.stream().forEach((connection) -> connection.close());
    }

    protected void sendPacket(Packet packet) throws IOException {
        connectionSelector.select().getWriter().write(packet);
    }
}
