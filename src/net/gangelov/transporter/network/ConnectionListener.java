package net.gangelov.transporter.network;

import net.gangelov.transporter.concurrency.Async;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.function.Consumer;

public class ConnectionListener {
    private ConnectionListenerServer connectionServer;
    private Consumer<Socket> connectionHandler;

    public ConnectionListener(int port) throws IOException {
        connectionServer = new ConnectionListenerServer(port);
    }

    public void start() {
        Async.run(connectionServer);
    }

    public void stop() {
        connectionServer.stop();
    }

    public void onConnection(Consumer<Socket> handler) {
        connectionHandler = handler;
    }

    private class ConnectionListenerServer implements Runnable {
        private ServerSocket serverSocket;

        public boolean shouldRun = true;

        public ConnectionListenerServer(int port) throws IOException {
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
                } catch (SocketTimeoutException e) {
                    continue;
                } catch (IOException e) {
                    // Ignore connect errors
                    e.printStackTrace();
                    continue;
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
            connectionHandler.accept(client);
        }
    }
}
