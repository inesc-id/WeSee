import interception.models.Connection;
import interception.models.ConnectionsEntity;
import org.junit.Test;
import org.springframework.util.Assert;
import interception.restclient.IConnectionRegistrator;
import interception.restclient.RestConnectionRegistrator;

public class ConnectivityTest{
    @Test
    public void testConnectionToServer()
    {
        String restInterfaceUrl = "http://localhost:8080/registerConnections";
        ConnectionsEntity connectionsEntity = new ConnectionsEntity();
        connectionsEntity.connections = new Connection[0];

        IConnectionRegistrator registrator = new RestConnectionRegistrator(restInterfaceUrl);
        boolean isSent = false;
        try
        {
            registrator.registerConnections(connectionsEntity);
            isSent = true;
        }
        catch (Exception e)
        {
        }
        Assert.isTrue(isSent, "message sent");
    }
}
