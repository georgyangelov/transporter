package net.gangelov;

import net.gangelov.transporter.network.NatTraversal;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello");

        NatTraversal nat = new NatTraversal();

        System.out.println("Your public address is " + nat.getPublicAddress());
    }
}
