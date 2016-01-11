package net.gangelov.transporter.network.nat;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.support.model.PortMapping;

public class UPNPClient {
    private UpnpService service = null;

    public void tryToOpenPort(String localAddress, int portNumber1, int portNumber2, String description1, String description2) {
        PortMapping portMapping = new PortMapping(
            portNumber1,
            localAddress,
            PortMapping.Protocol.TCP,
            description1
        );

        PortMapping portMapping2 = new PortMapping(
            portNumber2,
            localAddress,
            PortMapping.Protocol.TCP,
            description2
        );

        service = new UpnpServiceImpl(
            new PortMappingListener(portMapping),
            new PortMappingListener(portMapping2)
        );

        service.getControlPoint().search();
    }

    public void releasePort() {
        if (service != null) {
            service.shutdown();
        }
    }
}
