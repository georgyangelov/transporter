package net.gangelov.transporter.network.nat;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class NatTraversal {
    public static int CONTROL_PORT = 4143,
                      DATA_PORT = 4144;

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

    public void openPort() throws UnableToOpenPortException, ExecutionException, InterruptedException {
        upnpClient = new UPNPClient();

        upnpClient.tryToOpenPort(localAddress, CONTROL_PORT, "Transporter control port");
        upnpClient.tryToOpenPort(localAddress, DATA_PORT, "Transporter data port");

        // Give the UPnP service a little time
        Thread.currentThread().sleep(3000);

//        if (!testPort(CONTROL_PORT)) {
//            upnpClient.releasePort();
//
//            throw new UnableToOpenPortException();
//        }
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
