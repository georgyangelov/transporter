package net.gangelov;

import net.gangelov.transporter.concurrency.Async;
import net.gangelov.transporter.network.NatTraversal;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        System.out.println("Hello");

        NatTraversal nat = new NatTraversal();

        Future<String> ipFuture = Async.run(() -> nat.getPublicAddress());

        System.out.println("Your public address is " + ipFuture.get());
    }
}
