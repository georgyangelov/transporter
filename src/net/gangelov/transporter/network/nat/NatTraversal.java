package net.gangelov.transporter.network.nat;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class NatTraversal {
    private static int APPLICATION_PORT = 4143;

    private String publicAddress = null,
                   localAddress = null;

    private UPNPClient upnpClient = null;

    public NatTraversal() throws IOException {
        publicAddress = AddressDiscovery.discoverPublicAddress();
        localAddress = AddressDiscovery.discoverLocalAddress();
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public int openPort() throws UnableToOpenPortException, ExecutionException, InterruptedException {
        upnpClient = new UPNPClient();

        upnpClient.tryToOpenPort(localAddress, APPLICATION_PORT, "Transporter listener port");

        // Give the UPnP service a little time
        Thread.currentThread().sleep(3000);

        if (testPort(APPLICATION_PORT)) {
            return APPLICATION_PORT;
        } else {
            upnpClient.releasePort();

            throw new UnableToOpenPortException();
        }
    }

    public void releasePort() {
        if (upnpClient != null) {
            upnpClient.releasePort();
        }
    }

    private boolean testPort(int port) throws ExecutionException, InterruptedException {
        try {
            return OpenPortTest.test(publicAddress, port);
        } catch (IOException e) {
            return false;
        }
    }

    public static class UnableToOpenPortException extends Exception {
        public UnableToOpenPortException() {
            super("Unable to open public port");
        }
    }
}
