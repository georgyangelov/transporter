package net.gangelov.transporter.network;

import net.gangelov.transporter.network.protocol.ControlConnection;
import net.gangelov.transporter.network.protocol.ServerDataConnection;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private ConnectionListener controlListener;
    private ConnectionListener dataListener;

    private Map<Integer, ServerInstance> serverInstances = new HashMap<>();
    private int connectionIdCounter = 1;

    private File file;

    public Server(int controlPort, int dataPort, File file) throws IOException {
        this.file = file;

        controlListener = new ConnectionListener(controlPort);
        controlListener.onConnection((socket) -> {
            try {
                onNewControlConnection(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        dataListener = new ConnectionListener(dataPort);
        dataListener.onConnection((socket) -> {
            try {
                onNewDataConnection(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void start() {
        controlListener.start();
        dataListener.start();
    }

    public void stop() {
        controlListener.stop();
        dataListener.stop();
    }

    private synchronized void onNewControlConnection(Socket socket) throws IOException {
        // NOTE: This may overflow, but is unlikely
        int serverInstanceId = connectionIdCounter++;

        ControlConnection controlConnection = new ControlConnection(socket);
        ServerInstance serverInstance = new ServerInstance(controlConnection, serverInstanceId, file);

        serverInstances.put(serverInstanceId, serverInstance);

        serverInstance.start();
    }

    private void onNewDataConnection(Socket socket) throws IOException {
        ServerDataConnection dataConnection = new ServerDataConnection(socket, file);

        serverInstances.get(dataConnection.requestPacket.connectionId).onNewDataConnection(dataConnection);
    }
}
