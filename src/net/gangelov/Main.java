package net.gangelov;

import net.gangelov.transporter.network.nat.NatTraversal;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        System.out.println("Hello");

        NatTraversal nat = new NatTraversal();

//        Future<String> ipFuture = Async.run(() -> nat.discoverPublicAddress());
//
//        System.out.println("Your public address is " + ipFuture.get());

        System.out.println("Local address: " + nat.getLocalAddress());
        System.out.println("Public address: " + nat.getPublicAddress());

        try {
            System.out.println("Open port: " + nat.openPort());
        } catch (NatTraversal.UnableToOpenPortException e) {
            System.out.println(e.getMessage());
        } finally {
            nat.releasePort();
        }
    }
}
