package interception.restclient;

import interception.restclient.IConnectionRegistrator;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import interception.models.ConnectionsEntity;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.URI;

public class RestConnectionRegistrator implements IConnectionRegistrator {
        private String serverUrl;

    public RestConnectionRegistrator(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void registerConnections(ConnectionsEntity connectionsEntity) throws ConnectException {
        RestTemplate restTemplate = new RestTemplate();
        try
        {
            if (connectionsEntity.sourceId == null)
            {
                connectionsEntity.sourceId = InetAddress.getLocalHost().getHostName();
            }
            HttpEntity<ConnectionsEntity> request = new HttpEntity<ConnectionsEntity>(connectionsEntity);
            restTemplate.postForObject(new URI(serverUrl), request, Object.class);
        }
        catch (Exception e)
        {
            throw new ConnectException("Address is not accessible");
        }
    }
}
