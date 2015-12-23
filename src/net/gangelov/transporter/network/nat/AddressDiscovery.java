package net.gangelov.transporter.network.nat;

import net.gangelov.transporter.network.HTTP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class AddressDiscovery {
    private static final String IP_REFLECTION_API = "http://api.ipify.org";
    private static final String IP_REFLECTION_HOST = "api.ipify.org";
    private static final int IP_REFLECTION_PORT = 80;

    public static String discoverPublicAddress() throws IOException {
        return HTTP.get(IP_REFLECTION_API);
    }

    public static String discoverLocalAddress() throws IOException {
        Socket socket = new Socket(IP_REFLECTION_HOST, IP_REFLECTION_PORT);

        try {
            InetSocketAddress socketAddress = (InetSocketAddress)socket.getLocalSocketAddress();

            return socketAddress.getAddress().getHostAddress();
        } finally {
            socket.close();
        }
    }
}
