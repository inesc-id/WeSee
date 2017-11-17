package ws.soapserver;

import datasource.ConnectionDataRepository;
import datasource.IInterceptionDataProvider;
import check.IpCheck;
import datasource.model.ConnectionRecord;
import datasource.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ws.soapserver.build.Host;
import ws.soapserver.build.RegisterConnectionRequest;
import ws.soapserver.build.RegisterConnectionResponse;


@Endpoint
public class ConnectionRegistrationEndpoint {

    private static final String NAMESPACE_URI = "https://github.com/inesc-id/WeSee";

    private IInterceptionDataProvider connectionDataRepository;

    @Autowired
    public ConnectionRegistrationEndpoint() {
        this.connectionDataRepository = ConnectionDataRepository.getProvider();
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "registerConnection")
    @ResponsePayload
    public RegisterConnectionResponse registerConnection(@RequestPayload RegisterConnectionRequest request) {
        RegisterConnectionResponse response = new RegisterConnectionResponse();
        User sourceUser = getUser(request.getSource());
        User desctinationUser = getUser(request.getDestination());
        if (sourceUser == null || desctinationUser == null)
        {
            response.setAccepted(false);
            return response;
        }
        ConnectionRecord record = new ConnectionRecord(sourceUser, desctinationUser);
        connectionDataRepository.addRecord(record);
        response.setAccepted(true);
        return response;
    }

    private User getUser(Host host)
    {
        IpCheck.IpVersion version = IpCheck.recogniseIp(host.getIp());
        if (version == IpCheck.IpVersion.unknown)
            return null;
        return new User(host.getIp(), version == IpCheck.IpVersion.ipv4, host.getDns());
    }
}
