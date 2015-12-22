package net.gangelov.transporter.network;

import java.io.IOException;
import java.net.InetAddress;

public class NatTraversal {
    private static final String IP_REFLECTION_API = "https://api.ipify.org";

    private String address = null;

    public String getPublicAddress() throws IOException {
        if (address == null) {
            address = HTTP.get(IP_REFLECTION_API);
        }

        return address;
    }
}
