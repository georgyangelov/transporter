package net.gangelov;

import net.gangelov.args.ArgumentParser;
import net.gangelov.transporter.network.ClientInstance;
import net.gangelov.transporter.network.Server;
import net.gangelov.transporter.network.nat.NatTraversal;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogManager;

public class CLIMain {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        ArgumentParser cli = new ArgumentParser(
            // Flags
            new String[] {},

            // Arguments
            new String[] {"send", "receive"}
        );

        try {
            cli.parse(args);
        } catch (ArgumentParser.ArgumentParserException e) {
            System.err.println(e.getMessage());
            return;
        }

        // Disable logging to stdout ignoring those CLING messages
        LogManager.getLogManager().reset();

        if (cli.arguments.containsKey("send")) {
            sendFileServer(cli.arguments.get("send"));
        } else if (cli.arguments.containsKey("receive")) {
            receiveFile(cli.arguments.get("receive"));
        }
    }

    private static void sendFileServer(String file) throws IOException, ExecutionException, InterruptedException {
        NatTraversal nat = new NatTraversal();

        System.out.println("Local address: " + nat.getLocalAddress());
        System.out.println("Public address: " + nat.getPublicAddress());

        try {
            nat.openPort();

            System.out.println("Opened port for file transfer: " + NatTraversal.CONTROL_PORT);

            String address = nat.getPublicAddress() + ":" + NatTraversal.CONTROL_PORT;

            System.out.println("Server address is '" + address + "'");
            System.out.println("Waiting for clients to connect...");
        } catch (NatTraversal.UnableToOpenPortException e) {
            System.err.println(e.getMessage());
            return;
        } finally {
//            nat.releasePort();
        }

        Server server = new Server(NatTraversal.CONTROL_PORT, NatTraversal.DATA_PORT, new File("README.md"));
        server.start();
    }

    private static void receiveFile(String address) throws IOException {
        String[] addressComponents = address.split(":");

        String host = addressComponents[0];
        int port = Integer.parseInt(addressComponents[1]);

        ClientInstance client = new ClientInstance(host, NatTraversal.CONTROL_PORT, NatTraversal.DATA_PORT, new File("out.md"));
        client.start();

        System.out.println("Receiving file from " + host + ":" + port);
    }
}
