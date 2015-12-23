package net.gangelov.transporter.network.protocol.server;

import net.gangelov.transporter.concurrency.Async;
import net.gangelov.transporter.network.ServerHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener {
    private ConnectionListenerServer connectionServer;

    public ConnectionListener(ServerHandler server, int port) throws IOException {
        connectionServer = new ConnectionListenerServer(server, port);
    }

    public void start() {
        Async.run(connectionServer);
    }

    public void stop() {
        connectionServer.stop();
    }

    private static class ConnectionListenerServer implements Runnable {
        private ServerSocket serverSocket;
        private ServerHandler server;

        public boolean shouldRun = true;

        public ConnectionListenerServer(ServerHandler server, int port) throws IOException {
            this.server = server;
            serverSocket = new ServerSocket(port);

            // Allow the thread to exit while waiting for clients
            serverSocket.setSoTimeout(1000);
        }

        @Override
        public void run() {
            while (shouldRun) {
                Socket client = null;

                try {
                    client = serverSocket.accept();
                } catch (IOException e) {
                    // Ignore connect errors
                }

                try {
                    handleClient(client);
                } catch (Exception e) {
                    // Do not allow exceptions to bring down the whole connectionServer
                    e.printStackTrace();
                } finally {
                    try {
                        client.close();
                    } catch (IOException e) {
                        // Ignore close errors
                    }
                }
            }
        }

        public void stop() {
            shouldRun = false;
        }

        private void handleClient(Socket client) throws IOException {
            ClientConnectionHandler connectionHandler = new ClientConnectionHandler(server, client);

            Async.run(connectionHandler);

            server.socketConnected(connectionHandler);
        }
    }
};
