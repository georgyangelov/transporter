package net.gangelov.transporter.network.nat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OpenPortTest {
    public static boolean test(String host, int port) throws IOException, ExecutionException, InterruptedException {
        PingServer server = new PingServer(port);

        server.start();

        try {
            return testOpenPort(host, port);
        } finally {
            server.stop();
        }
    }

    private static boolean testOpenPort(String host, int port) throws IOException {
        Socket client = new Socket(host, port);

        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(client.getInputStream())
            );

            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(client.getOutputStream())
            );

            writer.write("ping");
            writer.newLine();
            writer.flush();

            return reader.readLine().equals("pong");
        } finally {
            client.close();
        }
    }

    private static class PingServer implements Runnable {
        private ServerSocket serverSocket;
        private boolean shouldRun = false;
        private ExecutorService executor;

        public PingServer(int port) throws IOException {
            serverSocket = new ServerSocket(port);

            // Allow the thread to exit while waiting for clients
            serverSocket.setSoTimeout(1000);
        }

        public void start() {
            shouldRun = true;

            executor = Executors.newSingleThreadExecutor();
            executor.submit(this);
        }

        public void stop() {
            shouldRun = false;

            executor.shutdown();
        }

        @Override
        public void run() {
            while (shouldRun) {
                Socket client = null;

                try {
                    client = serverSocket.accept();

                    handleClient(client);
                } catch (IOException e) {
                    // Ignore errors. They will be handled in the client.
                } finally {
                    if (client != null) {
                        try {
                            client.close();
                        } catch (IOException e) {
                            // Ignore close errors
                        }
                    }
                }
            }
        }

        private void handleClient(Socket client) throws IOException {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(client.getInputStream())
            );

            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(client.getOutputStream())
            );

            if (reader.readLine().equals("ping")) {
                writer.write("pong");
                writer.newLine();
                writer.close();
            } else {
                throw new IOException("Unknown data received from the ping client");
            }
        }
    }
}
