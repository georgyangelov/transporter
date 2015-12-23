package net.gangelov.transporter.network.nat;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.support.model.PortMapping;

public class UPNPClient {
    private UpnpService service = null;

    public void tryToOpenPort(String localAddress, int portNumber, String description) {
        PortMapping portMapping = new PortMapping(
            portNumber,
            localAddress,
            PortMapping.Protocol.TCP,
            description
        );

        service = new UpnpServiceImpl(
            new PortMappingListener(portMapping)
        );

        service.getControlPoint().search();
    }

    public void releasePort() {
        if (service != null) {
            service.shutdown();
        }
    }
}
