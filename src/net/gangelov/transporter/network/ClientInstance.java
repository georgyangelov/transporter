package net.gangelov.transporter.network;

import net.gangelov.ProgressTracker;
import net.gangelov.transporter.concurrency.Async;
import net.gangelov.transporter.concurrency.Callback;
import net.gangelov.transporter.network.protocol.ClientDataConnection;
import net.gangelov.transporter.network.protocol.ControlConnection;
import net.gangelov.transporter.network.protocol.Packet;
import net.gangelov.transporter.network.protocol.packets.FileRequestPacket;
import net.gangelov.transporter.network.protocol.packets.HeadersPacket;

import java.io.File;
import java.io.IOException;
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

    private ProgressTracker progressTracker;
    private boolean shouldLogProgress = true;

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

            progressTracker = new ProgressTracker(headers.fileSize);
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
        int connectionCount = 1;
        long chunkSize = headers.fileSize / connectionCount;

        for (int i = 0; i < connectionCount; i++) {
            long offset = chunkSize * i;
            long size = chunkSize;

            // Ensure that any bytes left over because of rounding errors and integer divide
            // are compensated for in the last connection.
            if (i == connectionCount - 1) {
                size = headers.fileSize - offset;
            }

            Socket socket = new Socket(host, dataPort);
            FileRequestPacket request = new FileRequestPacket(headers.connectionId, offset, size);
            ClientDataConnection dataConnection = new ClientDataConnection(socket, request, file, progressTracker);

            dataConnections.add(dataConnection);
        }

        Async.run(() -> {
            try {
                while (shouldLogProgress) {
                    Thread.currentThread().sleep(1000);

                    int progressPercentage = Math.round(progressTracker.getProgress() * 100);

                    System.out.println("Progress: " + progressPercentage + "%, " +
                                       "Speed: " + progressTracker.getSpeedAsString());

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Async.runAndWaitFor(dataConnections, () -> {
            stop();
            shouldLogProgress = false;

            System.out.println("Done in " + progressTracker.getExecutionTime() + " seconds");
            System.out.println("Average speed: " + ProgressTracker.speedToString(
                progressTracker.getTransferredBytes() / progressTracker.getExecutionTime()
            ));

            onComplete.execute();
        });
    }
}
